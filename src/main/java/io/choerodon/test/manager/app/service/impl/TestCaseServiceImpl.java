package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.core.utils.PageableHelper;
import io.choerodon.mybatis.helper.snowflake.SnowflakeHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.api.vo.agile.*;
import io.choerodon.test.manager.api.vo.devops.AppServiceDeployVO;
import io.choerodon.test.manager.api.vo.devops.AppServiceVersionRespVO;
import io.choerodon.test.manager.api.vo.devops.ApplicationRepDTO;
import io.choerodon.test.manager.api.vo.devops.InstanceValueVO;
import io.choerodon.test.manager.app.assembler.TestCaseAssembler;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.IssueTypeCode;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.feign.operator.AgileClientOperator;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import static io.choerodon.test.manager.infra.constant.DataLogConstants.BATCH_UPDATE_CASE_PRIORITY;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseServiceImpl implements TestCaseService {
    private static final String API_TYPE = "api";
    private static final String REPLAY = "replay";
    private static final String UI="ui";
    public static  final String CUSTOM_NUM_PATTERN = "^([A-Za-z0-9]+(-[A-Za-z0-9]+)*)$";
    @Autowired
    private BaseFeignClient baseFeignClient;

    @Autowired
    private ApplicationFeignClient applicationFeignClient;

    @Autowired
    private AgileClientOperator agileClientOperator;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCaseStepService testCaseStepService;

    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestCaseLinkMapper testCaseLinkMapper;

    @Autowired
    private TestDataLogMapper testDataLogMapper;

    @Autowired
    private TestCaseLinkService testCaseLinkService;

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Autowired
    private TestCaseAttachmentService testCaseAttachmentService;

    @Autowired
    private TestCaseAssembler testCaseAssembler;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestPriorityMapper testPriorityMapper;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    @Lazy
    private SnowflakeHelper snowflakeHelper;

//    @Value("${services.attachment.url}")
//    private String attachmentUrl;

    @Override
    public Page<IssueListTestVO> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithoutSub.param.projectId.not.null");
        Assert.notNull(pageRequest, "error.TestCaseService.listIssueWithoutSub.param.pageRequest.not.null");
        Sort sort = pageRequest.getSort();
        String sortSql = null;
        if (!ObjectUtils.isEmpty(sort)) {
            sortSql = PageableHelper.getSortSql(sort);
            sortSql = sortSql.replace(" ", ",");
        }
        return agileClientOperator.listIssueWithoutSubToTestComponent(projectId, searchDTO, organizationId, pageRequest.getPage(), pageRequest.getSize(), sortSql);
    }

    @Override
    public Page<IssueComponentDetailVO> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithoutSubDetail.param.projectId.not.null");
        Assert.notNull(pageRequest, "error.TestCaseService.listIssueWithoutSubDetail.param.pageRequest.not.null");
        return agileClientOperator.listIssueWithoutSubDetail(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort()), projectId, searchDTO, organizationId);
    }

    @Override
    public IssueDTO queryIssue(Long projectId, Long issueId, Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.queryIssue.param.projectId.not.null");
        Assert.notNull(issueId, "error.TestCaseService.queryIssue.param.issueId.not.null");
        return agileClientOperator.queryIssue(projectId, issueId, organizationId);
    }

    /**
     * 获取issue信息并且更新分页信息
     *
     * @param projectId
     * @param searchDTO
     * @param pageRequest
     * @return
     */
    @Override
    public <T> Map<Long, IssueInfosVO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Page page, Long organizationId) {
        Page<IssueListTestWithSprintVersionDTO> returnDto = listIssueWithLinkedIssues(projectId, searchDTO, pageRequest, organizationId);
        Assert.notNull(returnDto, "error.TestCaseService.getIssueInfoMapAndPopulatePageInfo.param.page.not.be.null");
        page.setNumber(returnDto.getNumber());
        page.setSize(returnDto.getSize());
        page.setTotalElements(returnDto.getTotalElements());
        return returnDto.getContent().stream().collect(Collectors.toMap(IssueListTestWithSprintVersionDTO::getIssueId, IssueInfosVO::new));

    }

    @Override
    public Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail, Long organizationId) {
        PageRequest pageRequest = new PageRequest(0, 999999999, Sort.Direction.DESC, "issueId");
        if (needDetail) {
            return listIssueWithoutSubDetail(projectId, searchDTO, pageRequest, organizationId).getContent().stream().collect(Collectors.toMap(IssueComponentDetailVO::getIssueId, IssueInfosVO::new));
        } else {
            return listIssueWithoutSub(projectId, searchDTO, pageRequest, organizationId).getContent().stream().collect(Collectors.toMap(IssueListTestVO::getIssueId, IssueInfosVO::new));
        }
    }

    @Override
    public Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail, Long organizationId) {
        if (ObjectUtils.isEmpty(issueIds)) {
            return new HashMap<>();
        }
        return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), needDetail, organizationId);
    }

    private SearchDTO buildIdsSearchDTO(Long[] issueIds) {
        SearchDTO searchDTO = new SearchDTO();
        Map map = new HashMap();
        map.put("issueIds", issueIds);
        searchDTO.setOtherArgs(map);
        return searchDTO;
    }


    @Override
    public List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, List<Long> issueId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueLinkByIssueId.param.projectId.not.null");
        if (ObjectUtils.isEmpty(issueId)) {
            return new ArrayList<>();
        }
        return agileClientOperator.listIssueLinkByBatch(projectId, issueId);
    }

    @Override
    public LookupTypeWithValuesDTO queryLookupValueByCode(String typeCode) {
        return agileClientOperator.queryLookupValueByCode(typeCode);
    }


    @Override
    public List<IssueStatusDTO> listStatusByProjectId(Long projectId) {
        return agileClientOperator.listStatusByProjectId(projectId);
    }

    @Override
    public String getVersionValue(Long projectId, Long appVersionId) {
        return applicationFeignClient.getVersionValue(projectId, appVersionId).getBody();
    }

    @Override
    public ApplicationRepDTO queryByAppId(Long projectId, Long applicationId) {
        return applicationFeignClient.queryByAppId(projectId, applicationId).getBody();
    }

    @Override
    public List<AppServiceVersionRespVO> getAppversion(Long projectId, List<Long> appVersionId) {
        return applicationFeignClient.getAppversion(projectId, TypeUtil.longsToArray(appVersionId)).getBody();
    }

    @Override
    public InstanceValueVO previewValues(Long projectId, InstanceValueVO replaceResult, Long appVersionId) {
        return applicationFeignClient.previewValues(projectId, replaceResult, appVersionId).getBody();
    }

    @Override
    public void deployTestApp(Long projectId, AppServiceDeployVO appServiceDeployVO) {
        applicationFeignClient.deployTestApp(projectId, appServiceDeployVO);
    }

    @Override
    public TestCaseRepVO createTestCase(Long projectId, TestCaseVO testCaseVO){
        return this.createTestCase(projectId, testCaseVO, null);
    }

    @Override
    public TestCaseRepVO createTestCase(Long projectId, TestCaseVO testCaseVO, AtomicLong outsideCount) {
        checkCustomNum(projectId, testCaseVO.getCustomNum());
        Long caseNum;
        if (Objects.isNull(outsideCount)){
            TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
            testProjectInfoDTO.setProjectId(projectId);
            TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
            if (ObjectUtils.isEmpty(testProjectInfo)) {
                throw new CommonException("error.query.project.info.null");
            }
            testCaseVO.setProjectId(projectId);
            caseNum = CaseNumUtil.getNewCaseNum(projectId);
            //修改projectInfo maxCaseNum
            if (testProjectInfoMapper.updateMaxCaseNum(projectId, caseNum) != 1) {
                throw new CommonException("error.case.max.num.update");
            }
        }else {
            caseNum = outsideCount.incrementAndGet();
        }
        testCaseVO.setCaseNum(caseNum.toString());
        TestCaseDTO testCaseDTO = baseInsert(testCaseVO);
        // 创建测试步骤
        List<TestCaseStepVO> caseStepVOS = testCaseVO.getCaseStepVOS();
        if (!CollectionUtils.isEmpty(caseStepVOS)) {
            caseStepVOS.forEach(v -> {
                v.setIssueId(testCaseDTO.getCaseId());
                testCaseStepService.changeStep(v, projectId, false);
            });
        }
        // 返回数据
        List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderMapper.selectListByProjectId(projectId);
        Map<Long, TestIssueFolderDTO> folderMap = testIssueFolderDTOS.stream().collect(Collectors.toMap(TestIssueFolderDTO::getFolderId, Function.identity()));
        TestCaseRepVO testCaseRepVO = testCaseAssembler.dtoToRepVo(testCaseDTO, folderMap);
        return testCaseRepVO;
    }

    private void checkCustomNum(Long projectId, String customNum){
        if (!ObjectUtils.isEmpty(customNum)) {
            if (customNum.length() > 50) {
                throw new CommonException("error.custom.num.length.rang.out.range");
            }
            Boolean matches = Pattern.matches(CUSTOM_NUM_PATTERN, customNum);
            if (!matches) {
                throw new CommonException("error.custom.num.illegal");
            }
        }
    }

    @Override
    public TestCaseInfoVO queryCaseInfo(Long projectId, Long caseId) {
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(caseId);
        if (ObjectUtils.isEmpty(testCaseDTO)) {
            throw new CommonException("error.test.case.is.not.exist");
        }
        TestCaseInfoVO testCaseInfoVO = testCaseAssembler.dtoToInfoVO(testCaseDTO);
        return testCaseInfoVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCase(Long projectId, Long caseId) {
        // 删除测试用例步骤
        testCaseStepService.removeStepByIssueId(projectId, caseId);
        // 删除问题链接
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        testCaseLinkDTO.setLinkCaseId(caseId);
        testCaseLinkDTO.setProjectId(projectId);
        testCaseLinkMapper.delete(testCaseLinkDTO);
        // 删除测试用例相关的dataLog
        TestDataLogDTO testDataLogDTO = new TestDataLogDTO();
        testDataLogDTO.setProjectId(projectId);
        testDataLogDTO.setCaseId(caseId);
        testDataLogMapper.delete(testDataLogDTO);
        // 删除附件信息
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        testCaseAttachmentDTO.setProjectId(projectId);
        testCaseAttachmentDTO.setCaseId(caseId);
        List<TestCaseAttachmentDTO> attachmentDTOS = testAttachmentMapper.select(testCaseAttachmentDTO);
        attachmentDTOS.forEach(v -> testCaseAttachmentService.delete(projectId, v.getAttachmentId()));
        // 删除测试用例
        testCaseMapper.deleteByPrimaryKey(caseId);

        // 删除测试计划中选择自动同步的计划中的有关联的执行
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(projectId, caseId);
        if (CollectionUtils.isEmpty(testCycleCaseDTOS)) {
            List<Long> executeIds = testCycleCaseDTOS.stream().map(TestCycleCaseDTO::getExecuteId).collect(Collectors.toList());
            testCycleCaseService.batchDeleteByExecuteIds(executeIds);
        }
    }

    @Override
    public Page<TestCaseRepVO> listAllCaseByFolderId(Long projectId, Long folderId, PageRequest pageRequest, SearchDTO searchDTO, Long planId) {
        // 查询文件夹下所有的目录
        Set<Long> folderIds = new HashSet<>();
        TestIssueFolderDTO testIssueFolder = new TestIssueFolderDTO();
        testIssueFolder.setProjectId(projectId);
        Map<Long, List<TestIssueFolderDTO>> folderMap = testIssueFolderMapper.select(testIssueFolder).stream().filter(issueFolderDTO -> !API_TYPE.equals(issueFolderDTO.getType()) && !REPLAY.equals(issueFolderDTO.getType()) && !UI.equals(issueFolderDTO.getType())).collect(Collectors.groupingBy(TestIssueFolderDTO::getParentId));
        queryAllFolderIds(folderId, folderIds, folderMap);
        // 处理排序
        checkPageRequest(pageRequest);
        // 查询文件夹下的的用例
        Page<TestCaseDTO> longPageInfo = PageHelper.doPageAndSort(pageRequest, () -> testCaseMapper.listCase(projectId, folderIds, searchDTO));
        Page<TestCaseRepVO> pageRepList;
        if (CollectionUtils.isEmpty(longPageInfo.getContent())) {
            pageRepList = PageUtil.buildPageInfoWithPageInfoList(longPageInfo, new ArrayList<>());
            return pageRepList;
        }
        List<TestCaseRepVO> repVOS = testCaseAssembler.listDtoToRepVo(projectId, longPageInfo.getContent(), planId);
        pageRepList = PageUtil.buildPageInfoWithPageInfoList(longPageInfo, repVOS);
        return pageRepList;
    }

    private void checkPageRequest(PageRequest pageRequest) {
        Sort sort = pageRequest.getSort();
        if (Objects.isNull(sort)){
            pageRequest.setSort(new Sort(new Sort.Order(Sort.Direction.ASC, TestCaseDTO.FIELD_CASE_ID)));
            return;
        }
    }

    @Override
    public int getCaseCountByFolderId(Long folderId) {
        TestCaseDTO testCaseDTO = new TestCaseDTO();
        testCaseDTO.setFolderId(folderId);
        return testCaseMapper.selectCount(testCaseDTO);
    }

    @Override
    @DataLog(type = DataLogConstants.CASE_UPDATE)
    public TestCaseRepVO updateCase(Long projectId, TestCaseRepVO testCaseRepVO, String[] fieldList) {
        if (ObjectUtils.isEmpty(testCaseRepVO) || ObjectUtils.isEmpty(testCaseRepVO.getCaseId())) {
            throw new CommonException("error.case.is.not.null");
        }
        checkCustomNum(projectId, testCaseRepVO.getCustomNum());
        TestCaseDTO testCaseDTO = baseQuery(testCaseRepVO.getCaseId());
        TestCaseDTO map = modelMapper.map(testCaseRepVO, TestCaseDTO.class);
        map.setVersionNum(testCaseDTO.getVersionNum() + 1);
        List<String> list = new ArrayList<>(Arrays.asList(fieldList));
        list.add("versionNum");
        updateByOptional(map, list.toArray(new String[list.size()]));
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(testCaseDTO.getProjectId(), testCaseDTO.getCaseId());
        if (!CollectionUtils.isEmpty(testCycleCaseDTOS)) {
            List<TestCycleCaseDTO> collect = new ArrayList<>();
            if (!ObjectUtils.isEmpty(testCaseRepVO.getExecuteId())) {
                List<TestCycleCaseDTO> collect1 = testCycleCaseDTOS.stream().filter(v -> !testCaseRepVO.getExecuteId().equals(v.getExecuteId())).collect(Collectors.toList());
                collect.addAll(collect1);
            } else {
                collect.addAll(testCycleCaseDTOS);
            }
            autoAsyncCase(collect, true, false, false);
        }
        TestCaseDTO testCaseDTO1 = testCaseMapper.selectByPrimaryKey(map.getCaseId());
        List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderMapper.selectListByProjectId(projectId);
        Map<Long, TestIssueFolderDTO> folderMap = testIssueFolderDTOS.stream().collect(Collectors.toMap(TestIssueFolderDTO::getFolderId, Function.identity()));
        TestCaseRepVO testCaseRepVO1 = testCaseAssembler.dtoToRepVo(testCaseDTO1, folderMap);
        return testCaseRepVO1;
    }

    private void updateByOptional(TestCaseDTO map, String[] fieldList) {
        if (testCaseMapper.updateOptional(map, fieldList) != 1) {
           throw new CommonException("error.test.case.update");
        }
    }


    @Override
    @DataLog(type = DataLogConstants.BATCH_MOVE, single = false)
    public void batchMove(Long projectId, Long folderId, List<TestCaseRepVO> testCaseRepVOS) {
        if (ObjectUtils.isEmpty(testCaseRepVOS)) {
            return;
        }
        if (ObjectUtils.isEmpty(testIssueFolderMapper.selectByPrimaryKey(folderId))) {
            throw new CommonException("error.query.folder.not.exist");
        }
        for (TestCaseRepVO testCaseRepVO : testCaseRepVOS) {
            // 待定 以后可能增加 rank 排序
//            if (!StringUtils.isEmpty(testCaseRepVO.getLastRank()) || !StringUtils.isEmpty(testCaseRepVO.getNextRank())) {
//                testCaseRepVO.setRank(RankUtil.Operation.UPDATE.getRank(testCaseRepVO.getLastRank(), testCaseRepVO.getNextRank()));
//            }
            TestCaseDTO testCaseDTO = baseQuery(testCaseRepVO.getCaseId());
            TestCaseDTO map = modelMapper.map(testCaseRepVO, TestCaseDTO.class);
            map.setObjectVersionNumber(testCaseDTO.getObjectVersionNumber());
            map.setFolderId(folderId);
            DBValidateUtil.executeAndvalidateUpdateNum(testCaseMapper::updateByPrimaryKeySelective, map, 1, "error.update.case");
        }

    }

    @Override
    public List<TestCaseDTO> batchCopy(Long projectId, Long folderId, List<TestCaseRepVO> testCaseRepVOS) {
        return this.batchCopy(projectId, folderId, testCaseRepVOS, null);
    }

    @Override
    public List<TestCaseDTO> batchCopy(Long projectId, Long folderId, List<TestCaseRepVO> testCaseRepVOS, AtomicLong outsideCount) {
        if (CollectionUtils.isEmpty(testCaseRepVOS)) {
            return Collections.emptyList();
        }
        if (ObjectUtils.isEmpty(testIssueFolderMapper.selectByPrimaryKey(folderId))) {
            throw new CommonException("error.query.folder.not.exist");
        }
        // 复制用例
        List<Long> collect = testCaseRepVOS.stream().map(TestCaseRepVO::getCaseId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return Collections.emptyList();
        }
        List<TestCaseDTO> testCaseDTOS = testCaseMapper.listCopyCase(projectId, collect);
        for (TestCaseDTO testCaseDTO : testCaseDTOS) {
            Long oldCaseId = testCaseDTO.getCaseId();
            testCaseDTO.setCaseId(null);
            testCaseDTO.setVersionNum(1L);
            testCaseDTO.setFolderId(folderId);
            testCaseDTO.setObjectVersionNumber(null);
            TestCaseRepVO testCase = createTestCase(projectId, modelMapper.map(testCaseDTO, TestCaseVO.class), outsideCount);
            // 复制用例步骤
            TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
            testCaseStepVO.setIssueId(oldCaseId);
            testCaseStepService.batchClone(testCaseStepVO, testCase.getCaseId(), projectId);
            // 复制用例链接
            testCaseLinkService.copyByCaseId(projectId, testCase.getCaseId(), oldCaseId);
            // 复制附件
            testCaseAttachmentService.cloneAttachmentByCaseId(projectId, testCase.getCaseId(), oldCaseId);
        }
        return testCaseDTOS;
    }

    @Override
    public void updateVersionNum(Long caseId) {
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(caseId);
        if (ObjectUtils.isEmpty(testCaseDTO)) {
            throw new CommonException("error.query.case.not.exist");
        }
        testCaseDTO.setVersionNum(testCaseDTO.getVersionNum() + 1);
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseMapper::updateByPrimaryKeySelective, testCaseDTO, 1, "error.update.case");
    }

    @Override
    public void updateVersionNumNotObjectVersion(Long caseId, Long userId) {
        testCaseMapper.updateVersionNumNotObjectVersion(caseId, userId);
    }

    @Override
    public Page<IssueListFieldKVVO> listUnLinkIssue(Long caseId, Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        List<Long> issueIds = new ArrayList<>();
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        testCaseLinkDTO.setProjectId(projectId);
        if (caseId != 0L) {
            testCaseLinkDTO.setLinkCaseId(caseId);
            List<TestCaseLinkDTO> caseLinkList = testCaseLinkMapper.select(testCaseLinkDTO);
            if (!CollectionUtils.isEmpty(caseLinkList)) {
                caseLinkList.forEach(caseLink -> issueIds.add(caseLink.getIssueId()));
            }
        }
        Map<String, Object> otherArgs = searchDTO.getOtherArgs();
        otherArgs.put("excludeIssueIds", issueIds);
        List<String> excludeTypeCodes = new ArrayList<>();
        excludeTypeCodes.add("issue_epic");
        otherArgs.put("excludeTypeCodes", excludeTypeCodes);

        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        List<String> applyTypes = new ArrayList<>();
        if (Boolean.TRUE.equals(checkContainProjectCategory(projectDTO.getCategories(), "N_WATERFALL_AGILE"))) {
            applyTypes.add("waterfall");
            applyTypes.add("agile");
        } else if (Boolean.TRUE.equals(checkContainProjectCategory(projectDTO.getCategories(), "N_WATERFALL"))) {
            applyTypes.add("waterfall");
        }
        searchDTO.setApplyTypes(applyTypes);
        return agileClientOperator.queryListIssueWithSub(projectId, searchDTO, pageRequest, organizationId);
    }

    private Boolean checkContainProjectCategory(List<ProjectCategoryDTO> categories, String category){
        if (CollectionUtils.isEmpty(categories)) {
            return false;
        }
        Set<String> codes = categories.stream().map(ProjectCategoryDTO::getCode).collect(Collectors.toSet());
        return codes.contains(category);
    }

    @Override
    public List<TestCaseDTO> queryByCustomNum(Long projectId, String customNum) {
        if (ObjectUtils.isEmpty(customNum)) {
            return new ArrayList<>();
        }
        TestCaseDTO testCaseDTO = new TestCaseDTO();
        testCaseDTO.setProjectId(projectId);
        testCaseDTO.setCustomNum(customNum);
        return testCaseMapper.select(testCaseDTO);
    }

    @Override
    public List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId) {
        return listIssueLinkByIssueId(projectId, issueId).stream()
                .filter(u -> u.getTypeCode().matches(IssueTypeCode.ISSUE_TEST + "|" + IssueTypeCode.ISSUE_AUTO_TEST)).collect(Collectors.toList());
    }

    @Override
    public List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId) {
        return listIssueLinkByIssueId(projectId, issueId).stream().collect(Collectors.toList());
    }

//    @Override
//    public Map<Long, ProductVersionDTO> getVersionInfo(Long projectId) {
//        Assert.notNull(projectId, "error.TestCaseService.getVersionInfo.param.projectId.not.be.null");
//        return productionVersionClient.listByProjectId(projectId).getBody().stream().collect(Collectors.toMap(ProductVersionDTO::getVersionId, Function.identity()));
//    }

//    public Long[] getVersionIds(Long projectId) {
//        Assert.notNull(projectId, "error.TestCaseService.getVersionIds.param.projectId.not.be.null");
//        return productionVersionClient.listByProjectId(projectId).getBody().stream().map(ProductVersionDTO::getVersionId).distinct().toArray(Long[]::new);
//
//    }

    @Override
    public ProjectDTO getProjectInfo(Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.getProjectInfo.param.projectId.not.be.null");
        return baseFeignClient.queryProject(projectId).getBody();
    }

    @Override
    public List<Long> queryIssueIdsByOptions(SearchDTO searchDTO, Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.queryIssueIdsByOptions.param.projectId.not.be.null");
        return agileClientOperator.queryIssueIdsByOptions(projectId, searchDTO);
    }

    @Override
    public IssueDTO createTest(IssueCreateDTO issueCreateDTO, Long projectId, String applyType) {
        Assert.notNull(projectId, "error.TestCaseService.createTest.param.projectId.not.be.null");
        return agileClientOperator.createIssue(projectId, applyType, issueCreateDTO);
    }

    @Override
    public void batchDeleteIssues(Long projectId, List<Long> issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchDeleteIssues.param.projectId.not.be.null");
        if (!CollectionUtils.isEmpty(issueIds)) {
            testCaseStepMapper.deleteByCaseIds(projectId, issueIds);
            testCaseMapper.batchDeleteCases(projectId, issueIds);
        }
    }

    private Page<IssueListTestWithSprintVersionDTO> listIssueWithLinkedIssues(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithLinkedIssues.param.projectId.not.null");
        Assert.notNull(pageRequest, "error.TestCaseService.listIssueWithLinkedIssues.param.pageRequest.not.null");
        return agileClientOperator.listIssueWithLinkedIssues(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort()), projectId, searchDTO, organizationId);
    }

    private TestCaseDTO baseInsert(TestCaseVO testCaseVO) {
        if (testCaseVO == null || testCaseVO.getCaseId() != null) {
            throw new CommonException("error.test.case.insert.caseId.should.be.null");
        }
        if (Objects.isNull(testCaseVO.getPriorityId())){
            TestPriorityDTO priorityDTO = new TestPriorityDTO();
            priorityDTO.setOrganizationId(ConvertUtils.getOrganizationId(testCaseVO.getProjectId()));
            priorityDTO.setDefaultFlag(true);
            testCaseVO.setPriorityId(testPriorityMapper.selectOne(priorityDTO).getId());
        }
        TestCaseDTO testCaseDTO = modelMapper.map(testCaseVO, TestCaseDTO.class);
        testCaseDTO.setVersionNum(1L);
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseMapper::insert, testCaseDTO, 1, "error.testcase.insert");
        return testCaseDTO;
    }

    private void queryAllFolderIds(Long folderId, Set<Long> folderIds, Map<Long, List<TestIssueFolderDTO>> folderMap) {
        folderIds.add(folderId);
        List<TestIssueFolderDTO> testIssueFolderDTOS = folderMap.get(folderId);
        if (!CollectionUtils.isEmpty(testIssueFolderDTOS)) {
            testIssueFolderDTOS.forEach(v -> queryAllFolderIds(v.getFolderId(), folderIds, folderMap));
        }
    }

    @Override
    public TestCaseDTO baseUpdate(TestCaseDTO testCaseDTO) {
        if (ObjectUtils.isEmpty(testCaseDTO) || ObjectUtils.isEmpty(testCaseDTO.getCaseId())) {
            throw new CommonException("error.case.is.not.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseMapper::updateByPrimaryKeySelective, testCaseDTO, 1, "error.testcase.update");
        return testCaseDTO;
    }

    @Override
    public Set<Long> selectFolderIds(Long projectId, Long folderId) {
        // 查询文件夹下所有的目录
        Set<Long> folderIds = new HashSet<>();
        TestIssueFolderDTO testIssueFolder = new TestIssueFolderDTO();
        testIssueFolder.setProjectId(projectId);
        Map<Long, List<TestIssueFolderDTO>> folderMap = testIssueFolderMapper.select(testIssueFolder).stream().filter(issueFolderDTO -> !API_TYPE.equals(issueFolderDTO.getType()) && !REPLAY.equals(issueFolderDTO.getType()) && !UI.equals(issueFolderDTO.getType())).collect(Collectors.groupingBy(TestIssueFolderDTO::getParentId));
        queryAllFolderIds(folderId, folderIds, folderMap);
        return folderIds;
    }

    @Override
    @DataLog(type = BATCH_UPDATE_CASE_PRIORITY, single = false)
    public void batchUpdateCasePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId,
                                         List<Long> projectIds) {
        testCaseMapper.batchUpdateCasePriority(priorityId, changePriorityId, userId, projectIds);
    }

    private TestCaseDTO baseQuery(Long caseId) {
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(caseId);
        if (ObjectUtils.isEmpty(testCaseDTO)) {
            throw new CommonException("error.case.is.not.exist");
        }
        return testCaseDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TestCaseDTO importTestCase(IssueCreateDTO issueCreateDTO, Long projectId, String applyType) {
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(projectId);
        TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
        if (ObjectUtils.isEmpty(testProjectInfo)) {
            throw new CommonException("error.query.project.info.null");
        }

        TestCaseDTO testCaseDTO = ConvertUtils.convertObject(issueCreateDTO, TestCaseDTO.class);
        Long caseNum = CaseNumUtil.getNewCaseNum(projectId);
        testCaseDTO.setCaseNum(caseNum.toString());
        testCaseDTO.setVersionNum(1L);
        // 插入测试用例
        testCaseMapper.insert(testCaseDTO);

        //修改projectInfo maxCaseNum
        if (testProjectInfoMapper.updateMaxCaseNum(projectId, caseNum) != 1) {
           throw new CommonException("error.case.max.num.update");
        }
        // 更新记录关联表
        List<TestCaseLinkDTO> testCaseLinkDTOList = issueCreateDTO.getTestCaseLinkDTOList();
        if (testCaseLinkDTOList != null && !testCaseLinkDTOList.isEmpty()) {
            for (TestCaseLinkDTO testCaseLinkDTO : testCaseLinkDTOList) {
                testCaseLinkDTO.setProjectId(projectId);
                testCaseLinkDTO.setLinkCaseId(testCaseDTO.getCaseId());
                testCaseLinkMapper.insert(testCaseLinkDTO);
            }
        }
        return testCaseDTO;
    }

    @Override
    public void batchImportTestCase(List<IssueCreateDTO> issueCreateDTOList, TestProjectInfoDTO testProjectInfo) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        if (ObjectUtils.isEmpty(testProjectInfo)) {
            throw new CommonException("error.query.project.info.null");
        }
        if (CollectionUtils.isEmpty(issueCreateDTOList)) {
            return;
        }
        Map<Object, IssueCreateDTO> issueCreateMap = new HashMap<>(issueCreateDTOList.size());
        List<TestCaseDTO> testCaseList = new ArrayList<>();
        for (IssueCreateDTO issueCreateDTO : issueCreateDTOList) {
            Long caseNum = CaseNumUtil.getNewCaseNum(testProjectInfo.getProjectId());
            testProjectInfo.setCaseMaxNum(caseNum);
            String caseNumStr = String.valueOf(caseNum);
            issueCreateMap.put(caseNumStr, issueCreateDTO);
            TestCaseDTO testCaseDTO = ConvertUtils.convertObject(issueCreateDTO, TestCaseDTO.class);
            testCaseDTO.setCaseId(snowflakeHelper.next());
            testCaseDTO.setCaseNum(String.valueOf(caseNum));
            testCaseDTO.setVersionNum(1L);
            testCaseDTO.setCreatedBy(userId);
            testCaseDTO.setLastUpdatedBy(userId);
            testCaseList.add(testCaseDTO);
        }
        // 插入测试用例
        testCaseMapper.batchInsert(testCaseList);
        // 插入测试工作项关联
        batchInsertTestCaseLink(testProjectInfo.getProjectId(), userId, testCaseList, issueCreateMap);
        // 插入测试步骤
        batchInsertTestCaseStep(issueCreateDTOList);
        testProjectInfoMapper.updateMaxCaseNum(testProjectInfo.getProjectId(), testProjectInfo.getCaseMaxNum());
    }

    @Override
    public void batchUpdateTestCase(List<IssueCreateDTO> issueUpdateDTOList, TestProjectInfoDTO testProjectInfo) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        if (ObjectUtils.isEmpty(testProjectInfo)) {
            throw new CommonException("error.query.project.info.null");
        }
        if (CollectionUtils.isEmpty(issueUpdateDTOList)) {
            return;
        }
        Map<Object, IssueCreateDTO> issueUpdateMap = new HashMap<>(issueUpdateDTOList.size());
        List<TestCaseDTO> testCaseList = new ArrayList<>();
        for (IssueCreateDTO issueUpdateDTO : issueUpdateDTOList) {
            issueUpdateMap.put(issueUpdateDTO.getCaseNum(), issueUpdateDTO);
            TestCaseDTO update = ConvertUtils.convertObject(issueUpdateDTO, TestCaseDTO.class);
            TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(update.getCaseId());
            update.setVersionNum(testCaseDTO.getVersionNum() + 1);
            update.setObjectVersionNumber(testCaseDTO.getObjectVersionNumber());
            testCaseList.add(update);
        }
        // 更新测试用例并生成日志
        for (TestCaseDTO update : testCaseList) {
            TestCaseRepVO testCaseRepVO = modelMapper.map(update, TestCaseRepVO.class);
            testCaseService.updateTestCaseWithDatalog(update, testCaseRepVO, new String[]{"summary", "description", "Folder Link"});
        }
        // 插入关联工作项
        batchInsertTestCaseLink(testProjectInfo.getProjectId(), userId, testCaseList, issueUpdateMap);
        // 插入测试步骤
        batchInsertTestCaseStep(issueUpdateDTOList);
    }

    @Override
    @DataLog(type = DataLogConstants.CASE_UPDATE)
    public void updateTestCaseWithDatalog(TestCaseDTO update, TestCaseRepVO testCaseRepVO, String[] fieldList) {
        testCaseMapper.updateByPrimaryKeySelective(update);
    }

    private void batchInsertTestCaseLink(Long projectId, Long userId, List<TestCaseDTO> testCaseList, Map<Object, IssueCreateDTO> issueCreateMap) {
        List<TestCaseLinkDTO> testCaseLinkDTOList = new ArrayList<>();
        testCaseList.forEach(testCase -> {
            IssueCreateDTO issueCreateDTO = issueCreateMap.get(testCase.getCaseNum());
            if(issueCreateDTO == null){
                return;
            }
            issueCreateDTO.setCaseId(testCase.getCaseId());
            if(!CollectionUtils.isEmpty(issueCreateDTO.getTestCaseLinkDTOList())){
                issueCreateDTO.getTestCaseLinkDTOList().forEach(testCaseLinkDTO -> {
                    testCaseLinkDTO.setProjectId(projectId);
                    testCaseLinkDTO.setLinkCaseId(testCase.getCaseId());
                    testCaseLinkDTO.setCreatedBy(userId);
                    testCaseLinkDTO.setLastUpdatedBy(userId);
                    testCaseLinkDTO.setObjectVersionNumber(1L);
                    testCaseLinkDTO.setCreationDate(new Date());
                    testCaseLinkDTO.setLastUpdateDate(new Date());
                    testCaseLinkDTOList.add(testCaseLinkDTO);
                });
            }
        });
        if (!CollectionUtils.isEmpty(testCaseLinkDTOList)) {
            testCaseLinkMapper.batchInsert(testCaseLinkDTOList);
        }
    }

    private void batchInsertTestCaseStep(List<IssueCreateDTO> issueUpdateDTOList) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        List<TestCaseStepProDTO> addStepList = new ArrayList<>();
        issueUpdateDTOList.forEach(issueUpdateDTO -> {
            if (!CollectionUtils.isEmpty(issueUpdateDTO.getTestCaseStepProList())) {
                String rank = null;
                for (TestCaseStepProDTO addStep : issueUpdateDTO.getTestCaseStepProList()) {
                    rank = RankUtil.genNext(RankUtil.Operation.INSERT.getRank(rank, null));
                    addStep.setRank(rank);
                    addStep.setIssueId(issueUpdateDTO.getCaseId());
                    addStep.setCreatedBy(userId);
                    addStep.setLastUpdatedBy(userId);
                    addStepList.add(addStep);
                }
            }
        });
        if (!CollectionUtils.isEmpty(addStepList)) {
            testCaseStepService.batchCreateOneStep(addStepList);
        }
    }

    @Override
    public List<Long> listAllCaseByFolderId(Long projectId, Long folderId) {
        // 查询文件夹下所有的目录
        Set<Long> folderIds = new HashSet<>();
        TestIssueFolderDTO testIssueFolder = new TestIssueFolderDTO();
        testIssueFolder.setProjectId(projectId);
        Map<Long, List<TestIssueFolderDTO>> folderMap = testIssueFolderMapper.select(testIssueFolder).stream().filter(issueFolderDTO -> !API_TYPE.equals(issueFolderDTO.getType()) && !REPLAY.equals(issueFolderDTO.getType()) && !UI.equals(issueFolderDTO.getType())).collect(Collectors.groupingBy(TestIssueFolderDTO::getParentId));
        queryAllFolderIds(folderId, folderIds, folderMap);
        // 查询文件夹下的的用例
        List<Long> caseIds = testCaseMapper.listCaseIds(projectId, folderIds, null);
        return caseIds;
    }

    @Override
    public List<TestCaseDTO> listByCaseIds(Long projectId, List<Long> caseIds) {
        if (CollectionUtils.isEmpty(caseIds)) {
            return new ArrayList<>();
        }
        return testCaseMapper.listByCaseIds(projectId, caseIds, true);
    }

    @Override
    public TestCaseInfoVO queryCaseRep(Long caseId) {
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(caseId);
        if (ObjectUtils.isEmpty(testCaseDTO)) {
            return new TestCaseInfoVO();
        }
        TestCaseInfoVO testCaseInfoVO = testCaseAssembler.dtoToInfoVO(testCaseDTO);
        List<TestCaseStepDTO> testCaseStepDTOS = testCaseStepService.listByCaseIds(Arrays.asList(caseId));
        if (!CollectionUtils.isEmpty(testCaseStepDTOS)) {
            testCaseInfoVO.setTestCaseStepS(testCaseStepDTOS);
        }
        return testCaseInfoVO;
    }

    @Override
    public Page<TestCaseVO> queryCaseByContent(Long projectId, PageRequest pageRequest,String content, Long issueId) {
        if (Objects.isNull(projectId)) {
            throw new CommonException("error.queryCase.projectId.not.null");
        }
        Page<TestCaseVO> casePageInfo = PageHelper.doPageAndSort(pageRequest,
                () -> testCaseMapper.queryCaseByContent(projectId, content, issueId));

        return casePageInfo;
    }

    @Override
    public void autoAsyncCase(List<TestCycleCaseDTO> testCycleCaseDTOS, Boolean changeCase, Boolean changeStep, Boolean changeAttach) {
        testCycleCaseDTOS.forEach(v -> {
            CaseCompareRepVO caseCompareVO = new CaseCompareRepVO();
            caseCompareVO.setCaseId(v.getCaseId());
            caseCompareVO.setExecuteId(v.getExecuteId());
            caseCompareVO.setSyncToCase(false);
            caseCompareVO.setChangeStep(changeStep);
            caseCompareVO.setChangeCase(changeCase);
            caseCompareVO.setChangeAttach(changeAttach);
            testCycleCaseService.updateCompare(v.getProjectId(), caseCompareVO);
        });

    }
}
