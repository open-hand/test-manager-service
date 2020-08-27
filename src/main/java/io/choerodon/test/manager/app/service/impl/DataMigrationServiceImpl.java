package io.choerodon.test.manager.app.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.api.vo.agile.*;
import io.choerodon.test.manager.infra.enums.TestPlanInitStatus;

import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections4.CollectionUtils;

import io.choerodon.test.manager.infra.util.RankUtil;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.feign.DataFixFeignClient;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.mapper.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class DataMigrationServiceImpl implements DataMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(DataMigrationServiceImpl.class);
    private static final String API_TYPE = "api";

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    TestCaseMapper testCaseMapper;

    @Autowired
    TestAttachmentMapper testAttachmentMapper;

    @Autowired
    private TestCaseLinkService testCaseLinkService;

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

    @Autowired
    private TestPriorityService testPriorityService;

    @Autowired
    private TestPriorityMapper testPriorityMapper;

    @Autowired
    private BaseFeignClient baseFeignClient;

    @Async
    @Override
    public void fixData() {
        logger.info("==============================>>>>>>>> Data Migrate Start <<<<<<<<=================================");
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
        //8.cycle()
        fixCycleCase();
        //9.step
        fixCycleCaseStep();
        //10.source
        fixCycleSource();
        //11 isnertStatus
        fixStatus();
        //12.cycleCaseRank
        fixCycleCaseStepRank();
        // 13 fix CycleCaseRank
        fixCycleCaseRank();
        //14 fixCaseFolderRank
        fixCaseFolderRank();
        //15 fixCycleCaseFolderRank
        fixCycleCaseFolderRank();
        logger.info("==========================>>>>>>>> Data Migrate Succeed!!! FINISHED!!! <<<<<<<<============================");
    }


    @Async
    @Override
    public void fixDataTestCasePriority() {
        logger.info("==============================>>>>>>>> test case proority Start <<<<<<<<=================================");
        // 为所有组织修复优先级
        List<TenantVO> body = getAllOrg();
        // 为所有组织创建优先级
        Map<Long, Long> orgMap = body.stream().collect(Collectors.toMap(TenantVO::getTenantId,
                (tenant -> testPriorityService.createDefaultPriority(tenant.getTenantId()))));
        // 修复用例优先级数据
        List<Long> caseProjectIdList = Optional.ofNullable(testCaseMapper.selectALLProjectId()).orElse(Collections.emptyList());
        List<Long> cycleCaseProjectIdList =
                Optional.ofNullable(testCycleCaseMapper.selectALLProjectId()).orElse(Collections.emptyList());
        Set<Long> projectIdSet = new HashSet<>();
        projectIdSet.addAll(caseProjectIdList);
        projectIdSet.addAll(cycleCaseProjectIdList);
        List<ProjectDTO> projectList = baseFeignClient.queryProjects(projectIdSet).getBody();
        Map<Long, List<Long>> projectMap = projectList.stream().collect(Collectors.toMap(ProjectDTO::getOrganizationId,
                project ->{
                    List<Long> list = new ArrayList<>();
                    list.add(project.getId());
                    return list;
                }, (v1, v2) -> {
                    v1.addAll(v2);
                    return v1;
                }));
        Long defaultPriority;
        List<Long> failList = new ArrayList<>();
        long successCount = 0;
        for (Map.Entry<Long, List<Long>> entry : projectMap.entrySet()) {
            // 如果不存在则创建默认三条高中低，并返回默认优先级id
            defaultPriority = orgMap.get(entry.getKey());
            if (Objects.isNull(defaultPriority)){
                failList.add(entry.getKey());
                continue;
            }
            if (CollectionUtils.isNotEmpty(entry.getValue())){
                testCaseMapper.updatePriorityByProject(entry.getValue(), defaultPriority);
                testCycleCaseMapper.updatePriorityByProject(entry.getValue(), defaultPriority);
            }
            successCount++;
        }
        logger.info("organiztion priority fix: success count: [{}], fail list: [{}]", successCount, failList);
        logger.info("==============================>>>>>>>> test case proority end <<<<<<<<=================================");
    }

    private List<TenantVO> getAllOrg() {
        int currentPage = 0;
        int size = 9999;
        Page<TenantVO> body = baseFeignClient.getAllOrgs(currentPage, size).getBody();
        if (CollectionUtils.isEmpty(body)){
            return Collections.emptyList();
        }
        List<TenantVO> result = new ArrayList<>(body.getContent());
        long page = body.getTotalElements() / body.getNumberOfElements();
        if (page > 0){
            for (int i = 1; i <= page; i++) {
                Page<TenantVO> temp = baseFeignClient.getAllOrgs(i, size).getBody();
                if (CollectionUtils.isEmpty(temp)){
                    break;
                }
                result.addAll(temp);
            }
        }
        return result;
    }

    private void migrateFolder() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        logger.info("=======================>>>project number:{}===============>>>{}", projectIdList.size(), projectIdList);
        for (Long projectFolderId : projectIdList) {
            List<ProductVersionDTO> productVersionDTOList = productionVersionClient.listByProjectId(projectFolderId).getBody();
            Map<Long, String> versionNameMap = productVersionDTOList.stream().filter(e -> e.getName() != null).collect(Collectors.toMap(ProductVersionDTO::getVersionId, ProductVersionDTO::getName));
            List<Long> versionIds = testIssueFolderMapper.selectVersionIdList(projectFolderId);
            for (Long versionId : versionIds) {
                String folderName = versionNameMap.get(versionId);
                TestIssueFolderVO newFolderVO = new TestIssueFolderVO();
                newFolderVO.setName(folderName == null || Objects.equals(folderName, "") ? "test" : folderName);
                newFolderVO.setParentId(0L);
                newFolderVO.setProjectId(projectFolderId);
                newFolderVO.setType("cycle");
                newFolderVO.setVersionId(0L);
                TestIssueFolderVO testIssueFolderVO = testIssueFolderService.create(projectFolderId, newFolderVO);

                testIssueFolderMapper.updateByVersionId(projectFolderId, versionId, testIssueFolderVO.getFolderId());

            }
            logger.info("============issueFolder=================>project:{} copy successed", projectFolderId);
        }
        logger.info("============issueFolder=================> copy successed");
    }

    private void migrateIssue() {
        List<Long> projectIds = testIssueFolderService.queryProjectIdList();
        logger.info("=======================>>>project number:{}===============>>>{}", projectIds.size(), projectIds);
        for (Long projectId : projectIds) {
            List<TestCaseMigrateDTO> testCaseMigrateDTOS = dataFixFeignClient.migrateTestCase(projectId).getBody();
            for (TestCaseMigrateDTO testCaseMigrateDTO : testCaseMigrateDTOS) {
                testCaseMapper.batchInsertTestCase(testCaseMigrateDTO);
            }
            logger.info("=========>>>>>>>>Insert Test Case By  ProjectId:{} <<<<<<<<=============", projectId);
        }
        logger.info("===========>>>>>>>>Test Case Data Migrate Succeed<<<<<<<<==========");

        //更新文件夹相关联的folderid
        testCaseMapper.updateTestCaseFolder();
        logger.info("===========>>>>>>>>Update Test Case Related Folder Succeed<<<<<<<<============");
    }

    private void migrateAttachment() {
        List<TestCaseAttachmentDTO> attachmentDTOS = dataFixFeignClient.migrateAttachment().getBody();
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (!CollectionUtils.isEmpty(attachmentDTOS)) {
            for (TestCaseAttachmentDTO testCaseAttachmentDTO : attachmentDTOS) {
                if (testCaseAttachmentDTO != null) {
                    logger.info("==========>>>>>>>>SET Test Case Attachment caseId:{} <<<<<<<<=======", testCaseAttachmentDTO.getCaseId());
                    testCaseAttachmentDTO.setUrl("/agile-service/" + testCaseAttachmentDTO.getUrl());
                    testCaseAttachmentDTO.setCreatedBy(userDetails.getUserId());
                    testCaseAttachmentDTO.setLastUpdatedBy(userDetails.getUserId());
                }
            }
            testAttachmentMapper.batchInsert(attachmentDTOS);
        }
        logger.info("===========attachment=============> copy successed");
    }

    private void migrateLink() {
        List<Long> projectIdList = testIssueFolderService.queryProjectIdList();
        for (Long projectId : projectIdList) {
            List<IssueLinkFixVO> issueLinkFixVOList = dataFixFeignClient.listIssueLinkByIssueIds(projectId).getBody();
            if (!CollectionUtils.isEmpty(issueLinkFixVOList)) {
                List<TestCaseLinkDTO> testCaseLinkDTOS = issueLinkFixVOList.stream().map(this::linkFixVOToDTO).collect(Collectors.toList());
                testCaseLinkService.batchInsert(testCaseLinkDTOS);
            }
            logger.info("===========link=============>project:{} link copy successed", projectId);
        }
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
        for (Long projectId : projectIdList) {
            List<DataLogFixVO> dataLogFixVOS = dataFixFeignClient.migrateDataLog(projectId).getBody();
            if (!CollectionUtils.isEmpty(dataLogFixVOS)) {
                List<TestDataLogDTO> testDataLogDTOS = dataLogFixVOS.stream().map(this::dataLogVoToDto).collect(Collectors.toList());
                testDataLogService.batchInsert(testDataLogDTOS);
            }
            logger.info("=========== data_log =============>project:{} copy successed", projectId);
        }
        logger.info("=============>>>>>>>> datalog copy successed <<<<<<<<===============");
    }

    private void migreateVersion() {
        List<TestVersionFixVO> testVersionFixVOS = dataFixFeignClient.migrateVersion().getBody();
        List<Long> versionIds = testCycleMapper.selectVersionId();
        List<TestVersionFixVO> collect = testVersionFixVOS.stream().filter(e -> versionIds.contains(e.getVersionId())).collect(Collectors.toList());
        List<TestPlanDTO> testPlanDTOS = collect.stream().map(this::planVOToDto).collect(Collectors.toList());
        for (TestPlanDTO t : testPlanDTOS) {
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
            t.setInitStatus(TestPlanInitStatus.SUCCESS);
            testPlanMapper.insert(t);
            fixCycle(t.getVersionId(), t.getPlanId());
        }

        logger.info("===========version=============> copy successed");
    }

    private void fixCycleCase() {
        testCycleCaseMapper.fixCycleCase();
        logger.info("===============>>>>>>> cycle case fix success!!! <<<<<<<<==============");
    }

    private void fixCycleCaseStep() {
        testCycleCaseStepMapper.fixCycleCaseStep();
        logger.info("=============>>>>>>> cycle case step fix suceess!!! <<<<<<<<===============");
    }

    private void fixCycleSource() {
        testCycleCaseMapper.fixSource();
        logger.info("=============>>>>>>> cycle source fix suceess!!! <<<<<<<<===============");
    }

    private void fixStatus() {
        TestStatusDTO caseStatus = new TestStatusDTO();
        caseStatus.setStatusType("CYCLE_CASE");
        caseStatus.setProjectId(0L);
        caseStatus.setStatusName("重测");
        caseStatus.setStatusColor("rgba(255,177,0,100)");

        TestStatusDTO caseStatus1 = new TestStatusDTO();
        caseStatus1.setStatusType("CYCLE_CASE");
        caseStatus1.setProjectId(0L);
        caseStatus1.setStatusName("无需测试");
        caseStatus1.setStatusColor("rgba(77,144,254,100)");

        TestStatusDTO stepStatus = new TestStatusDTO();
        stepStatus.setStatusType("CASE_STEP");
        stepStatus.setProjectId(0L);
        stepStatus.setStatusName("重测");
        stepStatus.setStatusColor("rgba(255,177,0,100)");

        TestStatusDTO stepStatus1 = new TestStatusDTO();
        stepStatus1.setStatusType("CASE_STEP");
        stepStatus1.setProjectId(0L);
        stepStatus1.setStatusName("无需测试");
        stepStatus1.setStatusColor("rgba(77,144,254,100)");

        List<TestStatusDTO> statusDTOS = new ArrayList<>();
        statusDTOS.add(caseStatus);
        statusDTOS.add(caseStatus1);
        statusDTOS.add(stepStatus);
        statusDTOS.add(stepStatus1);
        statusDTOS.stream().forEach(e -> testStatusMapper.insert(e));
        logger.info("=============>>>>>>> status fix suceess!!! <<<<<<<<===============");
    }

    private void fixCycleCaseStepRank() {
        testCycleCaseStepMapper.fixCycleCaseStepRank();
        logger.info("=============>>>>>>> cycle case step fix suceess!!! <<<<<<<<===============");
    }

    private void fixCycleCaseRank() {
        List<TestCycleCaseDTO> testCycleCaseDTOList = testCycleCaseMapper.selectByPlanId();
        Map<Long, List<TestCycleCaseDTO>> longListMap = testCycleCaseDTOList.stream().collect(Collectors.groupingBy(TestCycleCaseDTO::getPlanId));
        for (Map.Entry<Long, List<TestCycleCaseDTO>> map : longListMap.entrySet()) {
            String preRank = null;
            List<TestCycleCaseDTO> testCycleCaseDTOS = map.getValue();
            if (!CollectionUtils.isEmpty(testCycleCaseDTOS)) {
                for (TestCycleCaseDTO testCycleCaseDTO : map.getValue()) {
                    testCycleCaseDTO.setRank(RankUtil.Operation.INSERT.getRank(preRank, null));
                    preRank = testCycleCaseDTO.getRank();
                }
                //更新
                testCycleCaseMapper.fixRank(testCycleCaseDTOS);
            }
        }
        logger.info("=============>>>>>>> cycle case rank fix suceess!!! <<<<<<<<===============");
    }

    private void fixCaseFolderRank() {
        List<TestIssueFolderDTO> testIssueFolderDTOList = testIssueFolderMapper.selectAll();
        Map<Long, List<TestIssueFolderDTO>> longListMap = testIssueFolderDTOList.stream().filter(issueFolderDTO -> !StringUtils.equals(API_TYPE, issueFolderDTO.getType())).collect(Collectors.groupingBy(TestIssueFolderDTO::getProjectId));
        for (Map.Entry<Long, List<TestIssueFolderDTO>> map : longListMap.entrySet()) {
            if (!CollectionUtils.isEmpty(map.getValue())) {
                String preRank = null;
                for (TestIssueFolderDTO folderDto : map.getValue()) {
                    folderDto.setRank(RankUtil.Operation.INSERT.getRank(preRank, null));
                    preRank = folderDto.getRank();
                }
                testIssueFolderMapper.fixRank(map.getValue());
            }
        }
        logger.info("=============>>>>>>> folder rank fix suceess!!! <<<<<<<<===============");
    }

    private void fixCycleCaseFolderRank() {
        List<TestCycleDTO> testCycleDTOS = testCycleMapper.selectAll();
        Map<Long, List<TestCycleDTO>> longListMap = testCycleDTOS.stream().filter(e -> e.getPlanId() != null).collect(Collectors.groupingBy(TestCycleDTO::getPlanId));
        for (Map.Entry<Long, List<TestCycleDTO>> map : longListMap.entrySet()) {
            if (!CollectionUtils.isEmpty(map.getValue())) {
                String preRank = null;
                for (TestCycleDTO folderDto : map.getValue()) {
                    folderDto.setRank(RankUtil.Operation.INSERT.getRank(preRank, null));
                    preRank = folderDto.getRank();
                }
                testCycleMapper.fixRank(map.getValue());
            }
        }
        logger.info("=============>>>>>>> plan folder fix suceess!!! <<<<<<<<===============");
    }

    private void fixCycle(Long versionId, Long planId) {
        testCycleMapper.fixPlanId(versionId, planId);
        logger.info("===========fix cycle planId:{} =============> fix cycle successed", planId);
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

    private TestPlanDTO planVOToDto(TestVersionFixVO testVersionFixVO) {
        TestPlanDTO testPlanDTO = new TestPlanDTO();
        BeanUtils.copyProperties(testVersionFixVO, testPlanDTO);
        testPlanDTO.setPlanId(testVersionFixVO.getVersionId());
        testPlanDTO.setEndDate(testVersionFixVO.getReleaseDate() == null ? testVersionFixVO.getExpectReleaseDate() : testVersionFixVO.getReleaseDate());
        testPlanDTO.setStartDate(testVersionFixVO.getStartDate() == null ? testVersionFixVO.getReleaseDate() : testVersionFixVO.getStartDate());
        return testPlanDTO;
    }
}