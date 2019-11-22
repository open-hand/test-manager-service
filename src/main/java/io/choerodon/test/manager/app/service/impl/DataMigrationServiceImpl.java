package io.choerodon.test.manager.app.service.impl;

import java.util.*;
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
import io.choerodon.agile.api.vo.ProjectInfoFixVO;
import io.choerodon.test.manager.api.vo.IssueLinkFixVO;
import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.feign.*;
import io.choerodon.test.manager.infra.mapper.*;

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
    private ProjectInfoFeignClient projectInfoFeignClient;
    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;

    @Async
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void fixData() {
        logger.info("=====Data Migrate Start=====");
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
        //7.项目
        migrateProject();
        logger.info("=====Data Migrate Succeed!!!=====");
    }

    @Override
    public void migrateIssue() {
        List<Long> projectIds = testCaseFeignClient.queryIds().getBody();
        for (Long projectId : projectIds){
            List<TestCaseMigrateDTO> testCaseMigrateDTOS = testCaseFeignClient.migrateTestCase(projectId).getBody();
            for (TestCaseMigrateDTO testCaseMigrateDTO : testCaseMigrateDTOS){
                testCaseMapper.batchInsertTestCase(testCaseMigrateDTO);
            }
            logger.info("=====Insert Test Case By  ProjectId{}=====", projectId);
        }
        logger.info("=====Test Case Data Migrate Succeed=====");

        //更新文件夹相关联的folderid
        logger.info("======Update Test Case Related Folder=====");
        testCaseMapper.updateTestCaseFolder();
        logger.info("======Update Test Case Related Folder Succeed=====");
    }

    @Override
    public void migrateAttachment(){

        List<TestCaseAttachmentDTO> attachmentDTOS = testCaseFeignClient.migrateAttachment().getBody();
        if (!CollectionUtils.isEmpty(attachmentDTOS)){
            for (TestCaseAttachmentDTO testCaseAttachmentDTO: attachmentDTOS){
                if (testCaseAttachmentDTO != null){
                    logger.info("=====Insert Test Case Attachment{}=====",testCaseAttachmentDTO.getCaseId());
                    testAttachmentMapper.insert(testCaseAttachmentDTO);
                }
            }
        }
        logger.info("=====Test Case Attachment Migrate Succeed=====");
    }

    @Override
    public void migrateLink() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId->{
            List<IssueLinkFixVO> issueLinkFixVOList = issueLinkFeignClient.listIssueLinkByIssueIds(projectId).getBody();
            if(!CollectionUtils.isEmpty(issueLinkFixVOList)){
                List<TestCaseLinkDTO> testCaseLinkDTOS = issueLinkFixVOList.stream().map(this::linkFixVOToDTO).collect(Collectors.toList());
                testCaseLinkService.batchInsert(testCaseLinkDTOS);
            }
            logger.info("===========link=============>projectId {}link copy successed",projectId);
        });
    }

    @Override
    public void migrateLabel() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId -> {
            List<LabelFixVO> issueLabelDTOS = testIssueLabelFeignClient.listAllLabel(projectId).getBody();
            if (!CollectionUtils.isEmpty(issueLabelDTOS)) {
                List<TestCaseLabelDTO> testCaseLabelDTOList = modelMapper.map(issueLabelDTOS, new TypeToken<List<TestCaseLabelDTO>>() {
                }.getType());
                testCaseLabelService.batchInsert(testCaseLabelDTOList);
                logger.info("===========label=============> copy successed");
            }
        });
    }
    @Override
    public void migrateLabelCaseRel() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId->{
            List<LabelIssueRelFixVO> labelIssueRelDTOS = testIssueLabelRelFeignClient.queryIssueLabelRelList(projectId).getBody();
            if(!CollectionUtils.isEmpty(labelIssueRelDTOS)){
                List<TestCaseLabelRelDTO> testCaseLabelRelDTOS = labelIssueRelDTOS.stream().map(this::caseIssueVoTocaseDto).collect(Collectors.toList());
                testCaseLabelRelService.batchInsert(testCaseLabelRelDTOS);
            }
        });

        logger.info("===========label_issue_rel=============> copy successed");
    }

    @Override
    public void migrateFolder() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectFolderId -> {
            List<ProductVersionDTO> productVersionDTOList = productionVersionClient.listByProjectId(projectFolderId).getBody();
            Map<Long, String> versionNameMap = productVersionDTOList.stream().filter(e->e.getName()!=null).collect(Collectors.toMap(ProductVersionDTO::getVersionId, ProductVersionDTO::getName));
            List<TestIssueFolderVO> testIssueFolderVOS = testIssueFolderService.queryListByProjectId(projectFolderId);
            //以version区分
            Map<Long, List<TestIssueFolderVO>> projectVersionFolders = testIssueFolderVOS.stream().
                    filter(e -> e.getVersionId() != null).
                    collect(Collectors.groupingBy(TestIssueFolderVO::getVersionId));
            for (Map.Entry<Long, List<TestIssueFolderVO>> entry : projectVersionFolders.entrySet()) {
                //1.创建版本文件目录
                String folderName = versionNameMap.get(entry.getKey());
                if(!StringUtils.isEmpty(folderName)){
                    TestIssueFolderVO newFolderVO = new TestIssueFolderVO();
                    newFolderVO.setName(folderName);
                    newFolderVO.setParentId(0L);
                    newFolderVO.setProjectId(projectFolderId);
                    newFolderVO.setType("cycle");
                    newFolderVO.setVersionId(0L);
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

    @Override
    public void migrateProject() {
        List<ProjectInfoFixVO> projectInfoFixVOS = projectInfoFeignClient.queryAllProjectInfo().getBody();
        if(!CollectionUtils.isEmpty(projectInfoFixVOS)){
            List<TestProjectInfoDTO> testProjectInfoDTOS = projectInfoFixVOS.stream().map(this::projectInfVoToDto).collect(Collectors.toList());
            testProjectInfoMapper.batchInsert(testProjectInfoDTOS);
        }
        logger.info("===========project=============> copy successed");
    }

    private TestCaseLinkDTO linkFixVOToDTO(IssueLinkFixVO issueLinkFixVOList){
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        BeanUtils.copyProperties(issueLinkFixVOList,testCaseLinkDTO);
        testCaseLinkDTO.setLinkCaseId(issueLinkFixVOList.getLinkedIssueId());
        return testCaseLinkDTO;
    }
    private TestCaseLabelRelDTO caseIssueVoTocaseDto(LabelIssueRelFixVO labelIssueRelDTO) {
        TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
        BeanUtils.copyProperties(labelIssueRelDTO, testCaseLabelRelDTO);
        testCaseLabelRelDTO.setCaseId(labelIssueRelDTO.getIssueId());
        return testCaseLabelRelDTO;
    }

    private TestProjectInfoDTO projectInfVoToDto(ProjectInfoFixVO projectInfoFixVO) {
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        BeanUtils.copyProperties(projectInfoFixVO, testProjectInfoDTO);
        testProjectInfoDTO.setCaseMaxNum(projectInfoFixVO.getIssueMaxNum());
        return testProjectInfoDTO;
    }
}