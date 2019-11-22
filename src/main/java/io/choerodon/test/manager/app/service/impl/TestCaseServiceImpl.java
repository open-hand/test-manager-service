package io.choerodon.test.manager.app.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.PageObjectUtil;
import io.choerodon.agile.api.vo.*;
import io.choerodon.agile.infra.common.enums.IssueTypeCode;
import io.choerodon.test.manager.app.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.exception.CommonException;
import io.choerodon.devops.api.vo.*;
import io.choerodon.mybatis.entity.Criteria;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import io.choerodon.test.manager.infra.util.PageUtil;
import io.choerodon.test.manager.infra.util.TypeUtil;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.GitInfoContributor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@Component
@Transactional(rollbackFor = Exception.class)
public class TestCaseServiceImpl implements TestCaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseServiceImpl.class);

    private static final String BACKETNAME = "agile-service";

    @Autowired
    private TestCaseFeignClient testCaseFeignClient;

    @Autowired
    private ProductionVersionClient productionVersionClient;

    @Autowired
    private BaseFeignClient baseFeignClient;

    @Autowired
    private ApplicationFeignClient applicationFeignClient;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCaseStepService testCaseStepService;

    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestCaseLinkMapper testCaseLinkMapper;

    @Autowired
    private TestDataLogMapper testDataLogMapper;

    @Autowired
    private TestCaseLinkService testCaseLinkService;

    @Autowired
    private TestCaseLabelRelService testCaseLabelRelService;

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Autowired
    private TestCaseAttachmentService testCaseAttachmentService;

    @Autowired
    private TestCaseLabelService testCaseLabelService;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Override
    public ResponseEntity<PageInfo<IssueListTestVO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithoutSub.param.projectId.not.null");
        Assert.notNull(pageable, "error.TestCaseService.listIssueWithoutSub.param.pageable.not.null");
        return testCaseFeignClient.listIssueWithoutSubToTestComponent(projectId, searchDTO, organizationId, pageable.getPageNumber(), pageable.getPageSize(), PageUtil.sortToSql(pageable.getSort()));
    }

    @Override
    public ResponseEntity<PageInfo<IssueComponentDetailVO>> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithoutSubDetail.param.projectId.not.null");
        Assert.notNull(pageable, "error.TestCaseService.listIssueWithoutSubDetail.param.pageable.not.null");
        return testCaseFeignClient.listIssueWithoutSubDetail(pageable.getPageNumber(), pageable.getPageSize(), PageUtil.sortToSql(pageable.getSort()), projectId, searchDTO, organizationId);
    }

    @Override
    public ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId, Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.queryIssue.param.projectId.not.null");
        Assert.notNull(issueId, "error.TestCaseService.queryIssue.param.issueId.not.null");
        return testCaseFeignClient.queryIssue(projectId, issueId, organizationId);
    }

    @Override
    public Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId) {
        return listIssueWithoutSub(projectId, searchDTO, pageable, organizationId).getBody().getList().stream().collect(Collectors.toMap(IssueListTestVO::getIssueId, IssueInfosVO::new));
    }

    /**
     * 获取issue信息并且更新分页信息
     *
     * @param projectId
     * @param searchDTO
     * @param pageable
     * @return
     */
    public <T> Map<Long, IssueInfosVO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, Pageable pageable, Page page, Long organizationId) {
        PageInfo<IssueListTestWithSprintVersionDTO> returnDto = listIssueWithLinkedIssues(projectId, searchDTO, pageable, organizationId).getBody();
        Assert.notNull(returnDto, "error.TestCaseService.getIssueInfoMapAndPopulatePageInfo.param.page.not.be.null");
        page.setPageNum(returnDto.getPageNum());
        page.setPageSize(returnDto.getPageSize());
        page.setTotal(returnDto.getTotal());
        return returnDto.getList().stream().collect(Collectors.toMap(IssueListTestWithSprintVersionDTO::getIssueId, IssueInfosVO::new));

    }

    @Override
    public Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail, Long organizationId) {
        Pageable pageable = PageRequest.of(1, 999999999, Sort.Direction.DESC, "issueId");
        if (needDetail) {
            return listIssueWithoutSubDetail(projectId, searchDTO, pageable, organizationId).getBody().getList().stream().collect(Collectors.toMap(IssueComponentDetailVO::getIssueId, IssueInfosVO::new));
        } else {
            return listIssueWithoutSub(projectId, searchDTO, pageable, organizationId).getBody().getList().stream().collect(Collectors.toMap(IssueListTestVO::getIssueId, IssueInfosVO::new));
        }
    }

    @Override
    public Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail, Long organizationId) {
        if (ObjectUtils.isEmpty(issueIds)) {
            return new HashMap<>();
        }
        return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), needDetail, organizationId);
    }

    @Override
    public Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, Long[] issueIds, Pageable pageable, Long organizationId) {
        if (ObjectUtils.isEmpty(issueIds)) {
            return new HashMap<>();
        }
        return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), pageable, organizationId);
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
        return testCaseFeignClient.listIssueLinkByBatch(projectId, issueId).getBody();
    }

    public List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds) {
        return testCaseFeignClient.listByIssueIds(projectId, issueIds).getBody();
    }

    @Override
    public PageInfo<ComponentForListDTO> listByProjectId(Long projectId) {
        return testCaseFeignClient.listByProjectId(projectId, new SearchDTO()).getBody();
    }

    @Override
    public List<IssueLabelDTO> listIssueLabel(Long projectId) {
        return testCaseFeignClient.listIssueLabel(projectId).getBody();
    }

    @Override
    public LookupTypeWithValuesDTO queryLookupValueByCode(String typeCode) {
        return testCaseFeignClient.queryLookupValueByCode(typeCode).getBody();
    }


    @Override
    public List<IssueStatusDTO> listStatusByProjectId(Long projectId) {
        return testCaseFeignClient.listStatusByProjectId(projectId).getBody();
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
    @Transactional
    public TestCaseRepVO createTestCase(Long projectId, TestCaseVO testCaseVO) {
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(projectId);
        TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
        if (ObjectUtils.isEmpty(testProjectInfo)) {
            throw new CommonException("error.query.project.info.null");
        }
        testCaseVO.setProjectId(projectId);
        testCaseVO.setCaseNum(testProjectInfo.getCaseMaxNum() + 1);
        TestCaseDTO testCaseDTO = baseInsert(testCaseVO);
        // 创建测试步骤
        List<TestCaseStepVO> caseStepVOS = testCaseVO.getCaseStepVOS();
        if (!CollectionUtils.isEmpty(caseStepVOS)) {
            caseStepVOS.forEach(v -> {
                v.setIssueId(testCaseDTO.getCaseId());
                testCaseStepService.changeStep(v, projectId, false);
            });
        }
        // 关联测试用例与标签
        if (!CollectionUtils.isEmpty(testCaseVO.getLableIds())) {
            testCaseVO.getLableIds().forEach(v -> {
                TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
                testCaseLabelRelDTO.setCaseId(testCaseDTO.getCaseId());
                testCaseLabelRelDTO.setLabelId(v);
                testCaseLabelRelDTO.setProjectId(projectId);
                testCaseLabelRelService.baseCreate(testCaseLabelRelDTO);
            });

        }

        //  附件信息
        if (!CollectionUtils.isEmpty(testCaseVO.getAttachment())) {
            testCaseVO.getAttachment().forEach(v -> {
                v.setCaseId(testCaseDTO.getCaseId());
                v.setProjectId(projectId);
                testAttachmentMapper.insertSelective(v);
            });
        }
        // 返回数据
        testProjectInfo.setCaseMaxNum(testCaseVO.getCaseNum());
        testProjectInfoMapper.updateByPrimaryKeySelective(testProjectInfo);
        List<Long> userIds = new ArrayList<>();
        userIds.add(testCaseDTO.getCreatedBy());
        userIds.add(testCaseDTO.getLastUpdatedBy());
        Map<Long, UserMessageDTO> userMessageDTOMap = userService.queryUsersMap(userIds, false);
        TestCaseRepVO testCaseRepVO = dtoToRepVo(testCaseDTO, userMessageDTOMap);
        return testCaseRepVO;
    }

    @Override
    public TestCaseInfoVO queryCaseInfo(Long projectId, Long caseId) {
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(caseId);
        if (ObjectUtils.isEmpty(testCaseDTO)) {
            throw new CommonException("error.test.case.is.not.exist");
        }
        TestCaseInfoVO testCaseInfoVO = modelMapper.map(testCaseDTO, TestCaseInfoVO.class);
        // 获取用户信息
        Set<Long> ids = new HashSet<>();
        ids.add(testCaseDTO.getCreatedBy());
        ids.add(testCaseDTO.getLastUpdatedBy());
        List<Long> collect = modelMapper.map(ids, new TypeToken<List<Long>>() {
        }.getType());
        Map<Long, UserMessageDTO> UserMessageDTOMap = userService.queryUsersMap(collect, true);
        if (!ObjectUtils.isEmpty(UserMessageDTOMap.get(testCaseDTO.getCreatedBy()))) {
            testCaseInfoVO.setCreateUser(UserMessageDTOMap.get(testCaseDTO.getCreatedBy()));
        }
        if (!ObjectUtils.isEmpty(UserMessageDTOMap.get(testCaseDTO.getCreatedBy()))) {
            testCaseInfoVO.setLastUpdateUser(UserMessageDTOMap.get(testCaseDTO.getLastUpdatedBy()));
        }
        //  获取用例的标签
        List<TestCaseLabelRelDTO> testCaseLabelRelDTOS = testCaseLabelRelService.listLabelByCaseId(caseId);
        if (!CollectionUtils.isEmpty(testCaseLabelRelDTOS)) {
            List<Long> labelIds = testCaseLabelRelDTOS.stream().map(TestCaseLabelRelDTO::getLabelId).collect(Collectors.toList());
            testCaseInfoVO.setLableIds(labelIds);
        }
        // 用例的问题链接
        testCaseInfoVO.setIssuesInfos(testCaseLinkService.listIssueInfo(projectId, caseId));
        // 查询附件信息
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        testCaseAttachmentDTO.setCaseId(caseId);
        List<TestCaseAttachmentDTO> attachment = testAttachmentMapper.select(testCaseAttachmentDTO);
        if (!CollectionUtils.isEmpty(attachment)) {
            attachment.forEach(v -> {
                v.setUrl(attachmentUrl + "/" + BACKETNAME + "/" + v.getUrl());
            });
            testCaseInfoVO.setAttachment(attachment);
        }
        // 查询测试用例所属的文件夹
        TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(testCaseDTO.getFolderId());
        if (!ObjectUtils.isEmpty(testIssueFolderDTO)) {
            testCaseInfoVO.setFolder(testIssueFolderDTO.getName());
        }

        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(testCaseDTO.getProjectId());
        TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
        String issue = String.format("%s-%s", testProjectInfo.getProjectCode(), testCaseDTO.getCaseNum());
        testCaseInfoVO.setIssueNum(issue);
        return testCaseInfoVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCase(Long projectId, Long caseId) {
        // 删除测试用例步骤
        testCaseStepService.removeStepByIssueId(caseId);
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
        // 删除测试用例关联的标签
        testCaseLabelRelService.deleteByCaseId(caseId);
        // 删除附件信息
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        testCaseAttachmentDTO.setProjectId(projectId);
        testCaseAttachmentDTO.setCaseId(caseId);
        testAttachmentMapper.delete(testCaseAttachmentDTO);
        // 删除测试用例
        testCaseMapper.deleteByPrimaryKey(caseId);
    }

    @Override
    public PageInfo<TestCaseRepVO> listAllCaseByFolderId(Long projectId, Long folderId, Pageable pageable, SearchDTO searchDTO) {
        // 查询文件夹下所有的目录
        Set<Long> folderIds = new HashSet<>();
        queryAllFolderIds(folderId, folderIds);

        // 查询文件夹下的的用例
        List<TestCaseDTO> testCaseDTOS = testCaseMapper.listCaseByFolderIds(projectId, folderIds, searchDTO);
        if (CollectionUtils.isEmpty(testCaseDTOS)) {
            return new PageInfo<>(new ArrayList<>());
        }
        List<Long> userIds = new ArrayList<>();
        testCaseDTOS.forEach(v -> {
            userIds.add(v.getCreatedBy());
            userIds.add(v.getLastUpdatedBy());
        });
        Map<Long, UserMessageDTO> userMessageDTOMap = userService.queryUsersMap(userIds, false);
        List<TestCaseRepVO> collect = testCaseDTOS.stream().map(v -> dtoToRepVo(v, userMessageDTOMap)).collect(Collectors.toList());
        return PageUtil.createPageFromList(collect, pageable);

    }

    @Override
    public List<TestCaseDTO> listCaseByFolderId(Long folderId) {
        TestCaseDTO testCaseDTO = new TestCaseDTO();
        testCaseDTO.setFolderId(folderId);
        return testCaseMapper.select(testCaseDTO);
    }

    @Override
    @Transactional
    @DataLog(type = DataLogConstants.CASE_UPDATE)
    public TestCaseRepVO updateCase(Long projectId, TestCaseRepVO testCaseRepVO, String[] fieldList) {
        if (ObjectUtils.isEmpty(testCaseRepVO) || ObjectUtils.isEmpty(testCaseRepVO.getCaseId())) {
            throw new CommonException("error.case.is.not.null");
        }
        TestCaseDTO testCaseDTO = baseQuery(testCaseRepVO.getCaseId());
        TestCaseDTO map = modelMapper.map(testCaseRepVO, TestCaseDTO.class);
        map.setCaseNum(testCaseDTO.getCaseNum() + 1);

        baseUpdate(testCaseDTO);

        // 更新标签
        List<TestCaseLabelDTO> labels = testCaseRepVO.getLabels();
        if (!CollectionUtils.isEmpty(labels)) {
            changeLabel(projectId, testCaseDTO.getCaseId(), labels);
        }

        TestCaseDTO testCaseDTO1 = testCaseMapper.selectByPrimaryKey(map.getCaseId());
        List<Long> userIds = new ArrayList<>();
        userIds.add(testCaseDTO1.getCreatedBy());
        userIds.add(testCaseDTO1.getLastUpdatedBy());
        Map<Long, UserMessageDTO> userMessageDTOMap = userService.queryUsersMap(userIds, false);
        TestCaseRepVO testCaseRepVO1 = dtoToRepVo(testCaseDTO1, userMessageDTOMap);
        testCaseRepVO1.setLabels(labels);
        return testCaseRepVO1;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @DataLog(type = DataLogConstants.BATCH_MOVE, single = false)
    public void batchMove(Long projectId, Long folderId, List<TestCaseRepVO> testCaseRepVOS) {
        if (ObjectUtils.isEmpty(testCaseRepVOS)) {
            return;
        }
        if (ObjectUtils.isEmpty(testIssueFolderMapper.selectByPrimaryKey(folderId))) {
            throw new CommonException("error.query.folder.not.exist");
        }
        for (TestCaseRepVO testCaseRepVO : testCaseRepVOS) {
            TestCaseDTO testCaseDTO = baseQuery(testCaseRepVO.getCaseId());
            TestCaseDTO map = modelMapper.map(testCaseRepVO, TestCaseDTO.class);
            map.setObjectVersionNumber(testCaseDTO.getObjectVersionNumber());
            map.setVersionNum(testCaseDTO.getVersionNum() + 1);
            map.setFolderId(folderId);
            DBValidateUtil.executeAndvalidateUpdateNum(testCaseMapper::updateByPrimaryKeySelective, map, 1, "error.update.case");
        }

    }

    @Override
    public void batchCopy(Long projectId, Long folderId, List<TestCaseRepVO> testCaseRepVOS) {
        if (CollectionUtils.isEmpty(testCaseRepVOS)) {
            return;
        }
        if (ObjectUtils.isEmpty(testIssueFolderMapper.selectByPrimaryKey(folderId))) {
            throw new CommonException("error.query.folder.not.exist");
        }
        // 复制用例
        List<Long> collect = testCaseRepVOS.stream().map(TestCaseRepVO::getCaseId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return;
        }
        List<TestCaseDTO> testCaseDTOS = testCaseMapper.listCopyCase(projectId, collect);
        for (TestCaseDTO testCaseDTO : testCaseDTOS) {
            Long oldCaseId = testCaseDTO.getCaseId();
            testCaseDTO.setCaseId(null);
            testCaseDTO.setVersionNum(1L);
            testCaseDTO.setFolderId(folderId);
            testCaseDTO.setObjectVersionNumber(null);
            TestCaseRepVO testCase = createTestCase(projectId, modelMapper.map(testCaseDTO, TestCaseVO.class));
            // 复制用例步骤
            TestCaseStepVO testCaseStepVO = new TestCaseStepVO();
            testCaseStepVO.setIssueId(oldCaseId);
            testCaseStepService.batchClone(testCaseStepVO, testCase.getCaseId(), projectId);
            // 复制用例链接
            testCaseLinkService.copyByCaseId(projectId, testCase.getCaseId(), oldCaseId);
            // 复制标签
            testCaseLabelRelService.copyByCaseId(projectId, testCase.getCaseId(), oldCaseId);
            // 复制附件
            testCaseAttachmentService.cloneAttachmentByCaseId(projectId, testCase.getCaseId(), oldCaseId);
        }

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
    public List<TestCaseDTO> queryAllCase() {
        return testCaseMapper.selectAll();
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

    @Override
    public Map<Long, ProductVersionDTO> getVersionInfo(Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.getVersionInfo.param.projectId.not.be.null");
        return productionVersionClient.listByProjectId(projectId).getBody().stream().collect(Collectors.toMap(ProductVersionDTO::getVersionId, Function.identity()));
    }

    @Override
    public ResponseEntity<PageInfo<ProductVersionPageDTO>> getTestCycleVersionInfo(Long projectId, Map<String, Object> searchParamMap) {
        return productionVersionClient.listByOptions(projectId, searchParamMap);
    }

    public Long[] getVersionIds(Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.getVersionIds.param.projectId.not.be.null");
        return productionVersionClient.listByProjectId(projectId).getBody().stream().map(ProductVersionDTO::getVersionId).distinct().toArray(Long[]::new);

    }

    @Override
    public ProjectDTO getProjectInfo(Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.getProjectInfo.param.projectId.not.be.null");
        return baseFeignClient.queryProject(projectId).getBody();
    }

    @Override
    public List<Long> queryIssueIdsByOptions(SearchDTO searchDTO, Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.queryIssueIdsByOptions.param.projectId.not.be.null");
        return testCaseFeignClient.queryIssueIdsByOptions(projectId, searchDTO).getBody();
    }

    @Override
    public IssueDTO createTest(IssueCreateDTO issueCreateDTO, Long projectId, String applyType) {
        Assert.notNull(projectId, "error.TestCaseService.createTest.param.projectId.not.be.null");
        return testCaseFeignClient.createIssue(projectId, applyType, issueCreateDTO).getBody();
    }

    @Override
    public List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchIssueToVersion.param.projectId.not.be.null");
        return testCaseFeignClient.batchIssueToVersion(projectId, versionId, issueIds).getBody();
    }


    @Override
    public List<Long> batchCloneIssue(Long projectId, Long versionId, Long[] issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchCloneIssue.param.projectId.not.be.null");
        return testCaseFeignClient.batchCloneIssue(projectId, versionId, issueIds).getBody();
    }

    @Override
    public ResponseEntity batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchIssueToVersionTest.param.projectId.not.be.null");
        return testCaseFeignClient.batchIssueToVersionTest(projectId, versionId, issueIds);
    }

    @Override
    public ResponseEntity batchDeleteIssues(Long projectId, List<Long> issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchDeleteIssues.param.projectId.not.be.null");
        return testCaseFeignClient.batchDeleteIssues(projectId, issueIds);
    }

    private ResponseEntity<PageInfo<IssueListTestWithSprintVersionDTO>> listIssueWithLinkedIssues(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithLinkedIssues.param.projectId.not.null");
        Assert.notNull(pageable, "error.TestCaseService.listIssueWithLinkedIssues.param.pageable.not.null");
        return testCaseFeignClient.listIssueWithLinkedIssues(pageable.getPageNumber(), pageable.getPageSize(), PageUtil.sortToSql(pageable.getSort()), projectId, searchDTO, organizationId);
    }

    private TestCaseDTO baseInsert(TestCaseVO testCaseVO) {
        if (testCaseVO == null || testCaseVO.getCaseId() != null) {
            throw new CommonException("error.test.case.insert.caseId.should.be.null");
        }
        TestCaseDTO testCaseDTO = modelMapper.map(testCaseVO, TestCaseDTO.class);
        testCaseDTO.setVersionNum(1L);
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseMapper::insert, testCaseDTO, 1, "error.testcase.insert");
        return testCaseDTO;
    }

    private void queryAllFolderIds(Long folderId, Set<Long> folderIds) {
        TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(folderId);
        folderIds.add(folderId);
        TestIssueFolderDTO testIssueFolder = new TestIssueFolderDTO();
        testIssueFolder.setParentId(folderId);
        List<TestIssueFolderDTO> folderDTOS = testIssueFolderMapper.select(testIssueFolder);
        if (!CollectionUtils.isEmpty(folderDTOS)) {
            folderDTOS.forEach(v -> queryAllFolderIds(v.getFolderId(), folderIds));
        }
    }

    private TestCaseRepVO dtoToRepVo(TestCaseDTO testCaseDTO, Map<Long, UserMessageDTO> map) {
        TestCaseRepVO testCaseRepVO = new TestCaseRepVO();
        modelMapper.map(testCaseDTO, testCaseRepVO);
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(testCaseDTO.getProjectId());
        TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
        String issue = String.format("%s-%s", testProjectInfo.getProjectCode(), testCaseDTO.getCaseNum());
        testCaseRepVO.setIssueNum(issue);
        testCaseRepVO.setCreateUser(map.get(testCaseDTO.getCreatedBy()));
        testCaseRepVO.setLastUpdateUser(map.get(testCaseDTO.getLastUpdatedBy()));
        return testCaseRepVO;
    }

    private TestCaseDTO baseUpdate(TestCaseDTO testCaseDTO) {
        if (ObjectUtils.isEmpty(testCaseDTO) || ObjectUtils.isEmpty(testCaseDTO.getCaseId())) {
            throw new CommonException("error.case.is.not.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseMapper::updateByPrimaryKey, testCaseDTO, 1, "error.testcase.update");
        return testCaseDTO;
    }

    private TestCaseDTO baseQuery(Long caseId) {
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(caseId);
        if (ObjectUtils.isEmpty(testCaseDTO)) {
            throw new CommonException("error.case.is.not.exist");
        }
        return testCaseDTO;
    }

    private void changeLabel(Long projectId, Long caseId, List<TestCaseLabelDTO> labels) {
        // 查询已有的标签
        List<TestCaseLabelRelDTO> testCaseLabelRelDTOS = testCaseLabelRelService.listLabelByCaseId(caseId);
        List<Long> olderIds = testCaseLabelRelDTOS.stream().map(TestCaseLabelRelDTO::getLabelId).collect(Collectors.toList());
        List<Long> newIds = new ArrayList<>();
        labels.forEach(v -> {
            if (ObjectUtils.isEmpty(v.getLabelId())) {
                TestCaseLabelDTO orUpdate = testCaseLabelService.createOrUpdate(projectId, v);
                newIds.add(orUpdate.getLabelId());
            } else {
                newIds.add(v.getLabelId());
            }
        });
        // 比较 差集
        List<Long> newLabels = new ArrayList<>();
        newLabels.addAll(newIds);
        List<Long> olderLabels = new ArrayList<>();
        olderLabels.addAll(olderIds);

        newLabels.removeAll(olderIds);
        olderLabels.removeAll(newIds);
        // 删除不存在的，添加新增的
        createOrDeleteLabel(projectId, caseId, olderLabels, false);
        createOrDeleteLabel(projectId, caseId, newLabels, true);
    }

    private void createOrDeleteLabel(Long projectId, Long caseId, List<Long> labels, Boolean isCreate) {
        // 遍历labelId 集合
        if (!CollectionUtils.isEmpty(labels)) {
            labels.forEach(v -> {
                TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
                testCaseLabelRelDTO.setProjectId(projectId);
                testCaseLabelRelDTO.setCaseId(caseId);
                testCaseLabelRelDTO.setLabelId(v);
                // 如果是true，就去新建关联
                // 如果是false,就去删除，删除完成后，看看有没有其他用例关联该标签，没有就删除标签
                if (isCreate) {
                    testCaseLabelRelService.baseCreate(testCaseLabelRelDTO);
                } else {
                    testCaseLabelRelService.baseDelete(testCaseLabelRelDTO);
                    TestCaseLabelRelDTO testCaseLabelRel = new TestCaseLabelRelDTO();
                    testCaseLabelRel.setLabelId(v);
                    if (CollectionUtils.isEmpty(testCaseLabelRelService.query(testCaseLabelRel))) {
                        testCaseLabelService.baseDelete(v);
                    }
                }
            });
        }
    }
}
