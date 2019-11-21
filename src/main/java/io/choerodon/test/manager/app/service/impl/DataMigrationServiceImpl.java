package io.choerodon.test.manager.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.agile.api.vo.LabelFixVO;
import io.choerodon.agile.api.vo.LabelIssueRelFixVO;
import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.test.manager.api.vo.IssueLinkFixVO;
import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.feign.*;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper;

@Service
public class DataMigrationServiceImpl implements DataMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(TestIssueFolderServiceImpl.class);

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    TestCaseFeignClient testCaseFeignClient;

    @Autowired
    TestCaseMapper testCaseMapper;

    @Autowired
    TestAttachmentMapper testAttachmentMapper;

    @Autowired
    TestIssueFolderRelMapper testIssueFolderRelMapper;
    @Autowired
    private TestCaseLinkService testCaseLinkService;

    @Autowired
    private IssueLinkFeignClient issueLinkFeignClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TestIssueLabelFeignClient testIssueLabelFeignClient;

    @Autowired
    private TestCaseLabelService testCaseLabelService;

    @Autowired
    private TestIssueLabelRelFeignClient testIssueLabelRelFeignClient;

    @Autowired
    private TestCaseLabelRelService testCaseLabelRelService;

    @Autowired
    private ProductionVersionClient productionVersionClient;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;
    @Async
    @Transactional
    @Override
    public void fixData() {
        //1.文件夹
        migrateFolder();
        //2.用例
        migrateIssue();
        //3.标签关系
        migrateLabelCaseRel();
        //4标签
        migrateLabel();
        //5附件
        migrateAttachment();
        //6.连接
        migrateLink();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void migrateIssue() {
        List<Long> projectIds = testCaseFeignClient.queryIds(1L).getBody();
        Long startTime = System.currentTimeMillis();
        for (Long projectId : projectIds){
            List<TestCaseMigrateDTO> testCaseMigrateDTOS = testCaseFeignClient.migrateTestCase(projectId).getBody();
            for (TestCaseMigrateDTO testCaseMigrateDTO : testCaseMigrateDTOS){
                logger.info("caseID"+ testCaseMigrateDTO.getCaseId().toString());
                logger.info("description"+ testCaseMigrateDTO.getDescription());
                testCaseMapper.batchInsertTestCase(testCaseMigrateDTO);
                logger.info("InsertTestCase----By----ProjectId{}", testCaseMigrateDTO.getProjectId());
            }
        }
        logger.info("TestCaseDataMigrateSucceed");
        logger.info("Cost {}ms",System.currentTimeMillis() - startTime);

        //更新文件夹相关联的folderid
        List<Long> issueIds = testCaseMapper.listIssueIds();
        for (Long issueid : issueIds) {
            TestIssueFolderRelDTO relDTO = new TestIssueFolderRelDTO();
            relDTO.setIssueId(issueid);
            List<TestIssueFolderRelDTO> list = testIssueFolderRelMapper.select(relDTO);
            for (TestIssueFolderRelDTO testIssueFolderRelDTO : list){
                TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(issueid);
                testCaseDTO.setFolderId(testIssueFolderRelDTO.getFolderId());
                testCaseMapper.updateByPrimaryKeySelective(testCaseDTO);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void migrateAttachment(){

        logger.info("start---migrate---test-case-attachment");
        Long startTime = System.currentTimeMillis();
        List<TestCaseAttachmentDTO> attachmentDTOS = testCaseFeignClient.migrateAttachment(1L).getBody();
        if (!CollectionUtils.isEmpty(attachmentDTOS)){
            for (TestCaseAttachmentDTO testCaseAttachmentDTO: attachmentDTOS){
                if (testCaseAttachmentDTO != null){
                    logger.info("insert-test-case-attachment{}",testCaseAttachmentDTO.getCaseId());
                    testAttachmentMapper.insert(testCaseAttachmentDTO);
                }
            }
        }
        logger.info("TestCaseAttachmentMigrateSucceed");
        logger.info("Cost {} ms",System.currentTimeMillis() - startTime);
    }

    @Override
    public void migrateLink() {
        List<TestCaseDTO> testCaseDTOS = testCaseService.queryAllCase();
        Map<Long, List<TestCaseDTO>> projectIds = testCaseDTOS.stream().collect(Collectors.groupingBy(TestCaseDTO::getProjectId));
        for (Map.Entry<Long, List<TestCaseDTO>> project : projectIds.entrySet()) {
            List<Long> caseIdList = project.getValue().stream().map(TestCaseDTO::getCaseId).collect(Collectors.toList());
            List<IssueLinkFixVO> issueLinkFixVOList = issueLinkFeignClient.listIssueLinkByIssueIds(project.getKey(), caseIdList).getBody();
            if(!CollectionUtils.isEmpty(issueLinkFixVOList)){
                List<TestCaseLinkDTO> testCaseLinkDTOS = issueLinkFixVOList.stream().map(this::linkFixVOToDTO).collect(Collectors.toList());
                testCaseLinkService.batchInsert(testCaseLinkDTOS);
            }
            logger.info("projectId {}link copy successed",project);
        }
    }

    @Override
    public void migrateLabel() {
        List<LabelFixVO> issueLabelDTOS = testIssueLabelFeignClient.listAllLabel(0L).getBody();
        List<TestCaseLabelDTO> testCaseLabelDTOList = modelMapper.map(issueLabelDTOS, new TypeToken<List<TestCaseLabelDTO>>() {
        }.getType());
        testCaseLabelService.batchInsert(testCaseLabelDTOList);
        logger.info("===========label=============> copy successed");
    }

    @Override
    public void migrateLabelCaseRel() {
        List<TestCaseDTO> testCaseDTOS = testCaseService.queryAllCase();
        Map<Long, List<TestCaseDTO>> projectIds = testCaseDTOS.stream().collect(Collectors.groupingBy(TestCaseDTO::getProjectId));
        for (Map.Entry<Long, List<TestCaseDTO>> projectId : projectIds.entrySet()) {
            List<Long> caseIdList = projectId.getValue().stream().map(TestCaseDTO::getCaseId).collect(Collectors.toList());
            List<LabelIssueRelFixVO> labelIssueRelDTOS = testIssueLabelRelFeignClient.queryIssueLabelRelList(projectId.getKey(), caseIdList).getBody();
            if(!CollectionUtils.isEmpty(labelIssueRelDTOS)){
                List<TestCaseLabelRelDTO> testCaseLabelRelDTOS = labelIssueRelDTOS.stream().map(this::caseIssueDtoTocaseDto).collect(Collectors.toList());
                testCaseLabelRelService.batchInsert(testCaseLabelRelDTOS);
            }
        }
    }

    @Override
    public void migrateFolder() {
        TestIssueFolderVO testIssueFolder = new TestIssueFolderVO();
        List<TestIssueFolderVO> testIssueFolderVOS = modelMapper.map(testIssueFolderMapper.select(modelMapper
                .map(testIssueFolder, TestIssueFolderDTO.class)), new TypeToken<List<TestIssueFolderVO>>() {
        }.getType());
        Set<Long> projectFolderIds = testIssueFolderVOS.stream().map(TestIssueFolderVO::getProjectId).collect(Collectors.toSet());
        projectFolderIds.forEach(projectFolderId -> {
            List<ProductVersionDTO> productVersionDTOList = productionVersionClient.listByProjectId(projectFolderId).getBody();
            Map<Long, String> versionNameMap = productVersionDTOList.stream().filter(e->e.getName()!=null).collect(Collectors.toMap(ProductVersionDTO::getVersionId, ProductVersionDTO::getName));
            TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
            testIssueFolderDTO.setProjectId(projectFolderId);
            List<TestIssueFolderVO> testIssueProjectFolderVOs = modelMapper.map(testIssueFolderMapper.select(modelMapper
                    .map(testIssueFolderDTO, TestIssueFolderDTO.class)), new TypeToken<List<TestIssueFolderVO>>() {
            }.getType());
            //以version区分
            Map<Long, List<TestIssueFolderVO>> projectVersionFolderVOs = testIssueProjectFolderVOs.stream().filter(e -> e.getVersionId() != null).collect(Collectors.groupingBy(TestIssueFolderVO::getVersionId));
            for (Map.Entry<Long, List<TestIssueFolderVO>> entry : projectVersionFolderVOs.entrySet()) {
                //1.创建版本文件目录
                String folderName = versionNameMap.get(entry.getKey());
                if(!StringUtils.isEmpty(folderName)){
                    TestIssueFolderVO newFolderVO = new TestIssueFolderVO();
                    newFolderVO.setName(folderName);
                    newFolderVO.setParentId(0L);
                    newFolderVO.setProjectId(projectFolderId);
                    newFolderVO.setType("cycle");
                    TestIssueFolderVO testIssueFolderVO = testIssueFolderService.create(projectFolderId, newFolderVO);
                    //2.更新二级目录
                    if (!CollectionUtils.isEmpty(entry.getValue())) {
                        entry.getValue().stream().forEach(folderVO -> {
                            folderVO.setParentId(testIssueFolderVO.getFolderId());
                            testIssueFolderService.update(folderVO);
                        });
                    }
                }
            }
            logger.info("============issueFolder=================>project:{} copy successed",projectFolderId);
        });
    }

    private TestCaseLinkDTO linkFixVOToDTO(IssueLinkFixVO issueLinkFixVOList){
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        BeanUtils.copyProperties(issueLinkFixVOList,testCaseLinkDTO);
        testCaseLinkDTO.setLinkCaseId(issueLinkFixVOList.getLinkedIssueId());
        return testCaseLinkDTO;
    }
    private TestCaseLabelRelDTO caseIssueDtoTocaseDto(LabelIssueRelFixVO labelIssueRelDTO) {
        TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
        BeanUtils.copyProperties(labelIssueRelDTO, testCaseLabelRelDTO);
        testCaseLabelRelDTO.setCaseId(labelIssueRelDTO.getIssueId());
        return testCaseLabelRelDTO;
    }
}