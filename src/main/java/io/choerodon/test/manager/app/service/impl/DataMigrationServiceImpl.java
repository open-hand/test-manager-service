package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.agile.api.vo.DataLogFixVO;
import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.agile.api.vo.ProjectInfoFixVO;
import io.choerodon.agile.api.vo.TestVersionFixVO;
import io.choerodon.test.manager.api.vo.IssueLinkFixVO;
import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.feign.DataFixFeignClient;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
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
    private ModelMapper modelMapper;

    @Autowired
    private ProductionVersionClient productionVersionClient;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private DataFixFeignClient dataFixFeignClient;

    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestDataLogService testDataLogService;

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private TestCycleMapper testCycleMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Async
    @Override
    public void fixData() {
        logger.info("=====Data Migrate Start=====");
        //1.文件夹
        migrateFolder();
        //2.用例
        migrateIssue();
        //3附件
        migrateAttachment();
        //4.连接
        migrateLink();
        //5.项目
        migrateProject();
        //6.日志
        migreateDataLog();
        //7.版本
        migreateVersion();
//        //8.删除
//        deleteStage();
        //9.删除重复
        deleteRepeat();
        //10.cycle()
        fixCycleCase();
        //11.step
        fixCycleCaseStep();
        //12.source
        fixCycleSource();
        //13 isnertStatus
        fixStatus();
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
            List<TestCaseMigrateDTO> testCaseMigrateDTOS = dataFixFeignClient.migrateTestCase(projectId).getBody();
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

    private void migrateAttachment() {

        List<TestCaseAttachmentDTO> attachmentDTOS = dataFixFeignClient.migrateAttachment().getBody();
        if (!CollectionUtils.isEmpty(attachmentDTOS)) {
            for (TestCaseAttachmentDTO testCaseAttachmentDTO : attachmentDTOS) {
                if (testCaseAttachmentDTO != null) {
                    logger.info("=====Insert Test Case Attachment{}=====", testCaseAttachmentDTO.getCaseId());
                    testCaseAttachmentDTO.setUrl("/agile-service/"+testCaseAttachmentDTO.getUrl());
                    testAttachmentMapper.insert(testCaseAttachmentDTO);
                }
            }
        }
        logger.info("===========attachment=============> copy successed");
    }

    private void migrateLink() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId -> {
            List<IssueLinkFixVO> issueLinkFixVOList = dataFixFeignClient.listIssueLinkByIssueIds(projectId).getBody();
            if (!CollectionUtils.isEmpty(issueLinkFixVOList)) {
                List<TestCaseLinkDTO> testCaseLinkDTOS = issueLinkFixVOList.stream().map(this::linkFixVOToDTO).collect(Collectors.toList());
                testCaseLinkService.batchInsert(testCaseLinkDTOS);
            }
            logger.info("===========link=============>project:{}link copy successed", projectId);
        });
        logger.info("===========link=============> copy successed");
    }

    private void migrateProject() {
        List<ProjectInfoFixVO> projectInfoFixVOS = dataFixFeignClient.queryAllProjectInfo().getBody();
        if (!CollectionUtils.isEmpty(projectInfoFixVOS)) {
            List<TestProjectInfoDTO> testProjectInfoDTOS = projectInfoFixVOS.stream().map(this::projectInfVoToDto).collect(Collectors.toList());
            testProjectInfoMapper.batchInsert(testProjectInfoDTOS);
        }
        logger.info("===========project=============> copy successed");
    }

    private void migreateDataLog() {

        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        projectIdList.forEach(projectId -> {
            List<DataLogFixVO> dataLogFixVOS = dataFixFeignClient.migrateDataLog(projectId).getBody();
            if (!CollectionUtils.isEmpty(dataLogFixVOS)) {
                List<TestDataLogDTO> testDataLogDTOS = dataLogFixVOS.stream().map(this::dataLogVoToDto).collect(Collectors.toList());
                testDataLogService.batchInsert(testDataLogDTOS);
            }
            logger.info("===========data_log=============>project:{} copy successed",projectId);
        });

        logger.info("===========data_log=============> copy successed");
    }

    private void migreateVersion(){
        List<TestVersionFixVO> testVersionFixVOS = dataFixFeignClient.migrateVersion().getBody();
        List<Long> versionIds = testCycleMapper.selectVersionId();
        List<TestVersionFixVO> collect = testVersionFixVOS.stream().filter(e -> versionIds.contains(e.getVersionId())).collect(Collectors.toList());
        List<TestPlanDTO> testPlanDTOS = collect.stream().map(this::planVOToDto).collect(Collectors.toList());
        testPlanDTOS.forEach(t->{
            switch (t.getStatusCode()) {
                case "version_planning":
                    t.setStatusCode("todo");
                    break;
                case "released":
                    t.setStatusCode("done");
                    break;
                case "archived":
                    t.setStatusCode("done");
                    break;
                default:
                    break;
            }
            t.setAutoSync(false);
            t.setInitStatus("done");
            testPlanMapper.insert(t);
            fixCycle(t.getVersionId(),t.getPlanId());
        });

        logger.info("===========version=============> copy successed");
    }

//    private void deleteStage(){
//        testCycleMapper.deleteByType();
//        logger.info("===========cycle=============> delete successed");
//    }

    private void deleteRepeat(){
        List<TestCycleDTO> testCycleDTOS = testCycleMapper.selectRepeat();
        testCycleDTOS.forEach(testCycleDTO->{
                testCycleMapper.deleteRepeat(testCycleDTO.getProjectId(), testCycleDTO.getVersionId(),testCycleDTO.getFolderId());
        });
        logger.info("===========cycle=============> delete repeat successed");
    }

    private void fixCycleCase(){
        testCycleCaseMapper.fixCycleCase();
        logger.info("===========cycle=============> fix repeat successed");
    }

    private void fixCycleCaseStep(){
        testCycleCaseStepMapper.fixCycleCaseStep();
        logger.info("===========cycle step=============> fix repeat successed");
    }


    private void fixCycle(Long versionId,Long planId){
        testCycleMapper.fixPlanId(versionId, planId);
        logger.info("===========cycle cycle=============> fix cycle successed");
    }
    private  void fixCycleSource(){
        testCycleCaseMapper.fixSource();
    }
    private  void fixStatus(){
        TestStatusDTO caseStatus = new TestStatusDTO();
        TestStatusDTO caseStatus1 = new TestStatusDTO();
        TestStatusDTO stepStatus = new TestStatusDTO();
        TestStatusDTO stepStatus1 = new TestStatusDTO();
        caseStatus.setStatusType("CYCLE_CASE");
        caseStatus.setProjectId(0L);
        caseStatus.setStatusName("重测");
        caseStatus.setStatusColor("rgba(255,177,0,100)");

        caseStatus1.setStatusType("CYCLE_CASE");
        caseStatus1.setProjectId(0L);
        caseStatus1.setStatusName("无需测试");
        caseStatus1.setStatusColor("rgba(77,144,254,100)");

        stepStatus.setStatusType("CASE_STEP");
        stepStatus.setProjectId(0L);
        stepStatus.setStatusName("重测");
        stepStatus.setStatusColor("rgba(255,177,0,100)");

        stepStatus1.setStatusType("CASE_STEP");
        stepStatus1.setProjectId(0L);
        stepStatus1.setStatusName("无需测试");
        stepStatus1.setStatusColor("rgba(77,144,254,100)");
        List<TestStatusDTO> statusDTOS = new ArrayList<>();
        statusDTOS.add(caseStatus);
        statusDTOS.add(caseStatus1);
        statusDTOS.add(stepStatus);
        statusDTOS.add(stepStatus1);
        statusDTOS.stream().forEach(e->testStatusMapper.insert(e));
    }
    private TestCaseLinkDTO linkFixVOToDTO(IssueLinkFixVO issueLinkFixVOList) {
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        BeanUtils.copyProperties(issueLinkFixVOList, testCaseLinkDTO);
        testCaseLinkDTO.setLinkCaseId(issueLinkFixVOList.getLinkedIssueId());
        return testCaseLinkDTO;
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

    private TestPlanDTO planVOToDto(TestVersionFixVO testVersionFixVO){
            TestPlanDTO testPlanDTO = new TestPlanDTO();
            BeanUtils.copyProperties(testVersionFixVO,testPlanDTO);
            testPlanDTO.setPlanId(testVersionFixVO.getVersionId());
            testPlanDTO.setEndDate(testVersionFixVO.getReleaseDate() == null ? testVersionFixVO.getExpectReleaseDate():testVersionFixVO.getReleaseDate());
            testPlanDTO.setStartDate(testVersionFixVO.getStartDate()==null?testVersionFixVO.getReleaseDate():testVersionFixVO.getStartDate());
            return testPlanDTO;
    }
}