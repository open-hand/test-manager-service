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
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.agile.api.vo.*;
import io.choerodon.test.manager.api.vo.IssueLinkFixVO;
import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.feign.*;
import io.choerodon.test.manager.infra.mapper.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class DataMigrationServiceImpl implements DataMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(DataMigrationServiceImpl.class);

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

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestDataLogFeignClient testDataLogFeignClient;

    @Autowired
    private TestDataLogService testDataLogService;

    @Async
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
        //8.日志
        migreateDataLog();
        logger.info("===================>Data Migrate Succeed!!!<====================");
    }

    private void migrateFolder() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectFolderId -> {
            List<ProductVersionDTO> productVersionDTOList = productionVersionClient.listByProjectId(projectFolderId).getBody();
            Map<Long, String> versionNameMap = productVersionDTOList.stream().filter(e -> e.getName() != null).collect(Collectors.toMap(ProductVersionDTO::getVersionId, ProductVersionDTO::getName));
            List<Long> versionIds = testIssueFolderMapper.selectVersionIdList(projectFolderId);
            versionIds.forEach(versionId -> {
                String folderName = versionNameMap.get(versionId);
                TestIssueFolderVO newFolderVO = new TestIssueFolderVO();
                newFolderVO.setName(folderName == null ? "test" : folderName);
                newFolderVO.setParentId(0L);
                newFolderVO.setProjectId(projectFolderId);
                newFolderVO.setType("cycle");
                newFolderVO.setVersionId(0L);
                TestIssueFolderVO testIssueFolderVO = testIssueFolderService.create(projectFolderId, newFolderVO);

                testIssueFolderMapper.updateByVersionId(projectFolderId, versionId, testIssueFolderVO.getFolderId());

            });
            logger.info("============issueFolder=================>project:{} copy successed", projectFolderId);
        });
        logger.info("============issueFolder=================> copy successed");
    }

    private void migrateIssue() {
        List<Long> projectIds = testIssueFolderService.queryProjectIdList();
        for (Long projectId : projectIds) {
            List<TestCaseMigrateDTO> testCaseMigrateDTOS = testCaseFeignClient.migrateTestCase(projectId).getBody();
            for (TestCaseMigrateDTO testCaseMigrateDTO : testCaseMigrateDTOS) {
                testCaseMapper.batchInsertTestCase(testCaseMigrateDTO);
            }
            logger.info("=====Insert Test Case By  ProjectId:{}=====", projectId);
        }
        logger.info("=====Test Case Data Migrate Succeed=====");

        //更新文件夹相关联的folderid
        testCaseMapper.updateTestCaseFolder();
        logger.info("======Update Test Case Related Folder Succeed=====");
    }

    private void migrateLabelCaseRel() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId -> {
            List<LabelIssueRelFixVO> labelIssueRelDTOS = testIssueLabelRelFeignClient.queryIssueLabelRelList(projectId).getBody();
            if (!CollectionUtils.isEmpty(labelIssueRelDTOS)) {
                List<TestCaseLabelRelDTO> testCaseLabelRelDTOS = labelIssueRelDTOS.stream().map(this::caseIssueVoTocaseDto).collect(Collectors.toList());
                testCaseLabelRelService.batchInsert(testCaseLabelRelDTOS);
            }
            logger.info("===========label_issue_rel=============>project:{} copy successed");
        });

        logger.info("===========label_issue_rel=============> copy successed");
    }

    private void migrateLabel() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId -> {
            List<LabelFixVO> issueLabelDTOS = testIssueLabelFeignClient.listAllLabel(projectId).getBody();
            if (!CollectionUtils.isEmpty(issueLabelDTOS)) {
                List<TestCaseLabelDTO> testCaseLabelDTOList = modelMapper.map(issueLabelDTOS, new TypeToken<List<TestCaseLabelDTO>>() {
                }.getType());
                testCaseLabelService.batchInsert(testCaseLabelDTOList);
            }
            logger.info("===========label=============>project:{} copy successed",projectId);
        });
        logger.info("===========label=============> copy successed");
    }

    private void migrateAttachment() {

        List<TestCaseAttachmentDTO> attachmentDTOS = testCaseFeignClient.migrateAttachment().getBody();
        if (!CollectionUtils.isEmpty(attachmentDTOS)) {
            for (TestCaseAttachmentDTO testCaseAttachmentDTO : attachmentDTOS) {
                if (testCaseAttachmentDTO != null) {
                    logger.info("=====Insert Test Case Attachment{}=====", testCaseAttachmentDTO.getCaseId());
                    testAttachmentMapper.insert(testCaseAttachmentDTO);
                }
            }
        }
        logger.info("===========attachment=============> copy successed");
    }

    private void migrateLink() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId -> {
            List<IssueLinkFixVO> issueLinkFixVOList = issueLinkFeignClient.listIssueLinkByIssueIds(projectId).getBody();
            if (!CollectionUtils.isEmpty(issueLinkFixVOList)) {
                List<TestCaseLinkDTO> testCaseLinkDTOS = issueLinkFixVOList.stream().map(this::linkFixVOToDTO).collect(Collectors.toList());
                testCaseLinkService.batchInsert(testCaseLinkDTOS);
            }
            logger.info("===========link=============>project:{}link copy successed", projectId);
        });
        logger.info("===========link=============> copy successed");
    }

    private void migrateProject() {
        List<ProjectInfoFixVO> projectInfoFixVOS = projectInfoFeignClient.queryAllProjectInfo().getBody();
        if (!CollectionUtils.isEmpty(projectInfoFixVOS)) {
            List<TestProjectInfoDTO> testProjectInfoDTOS = projectInfoFixVOS.stream().map(this::projectInfVoToDto).collect(Collectors.toList());
            testProjectInfoMapper.batchInsert(testProjectInfoDTOS);
        }
        logger.info("===========project=============> copy successed");
    }

    private void migreateDataLog() {

        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId -> {
            List<DataLogFixVO> dataLogFixVOS = testDataLogFeignClient.migrateDataLog(projectId).getBody();
            if (!CollectionUtils.isEmpty(dataLogFixVOS)) {
                List<TestDataLogDTO> testDataLogDTOS = dataLogFixVOS.stream().map(this::dataLogVoToDto).collect(Collectors.toList());
                testDataLogService.batchInsert(testDataLogDTOS);
            }
            logger.info("===========data_log=============>project:{} copy successed",projectId);
        });

        logger.info("===========data_log=============> copy successed");
    }

    private TestCaseLinkDTO linkFixVOToDTO(IssueLinkFixVO issueLinkFixVOList) {
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        BeanUtils.copyProperties(issueLinkFixVOList, testCaseLinkDTO);
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

    private TestDataLogDTO dataLogVoToDto(DataLogFixVO dataLogFixVO) {
        TestDataLogDTO testDataLogDTO = new TestDataLogDTO();
        BeanUtils.copyProperties(dataLogFixVO, testDataLogDTO);
        testDataLogDTO.setCaseId(dataLogFixVO.getIssueId());
        return testDataLogDTO;
    }
}