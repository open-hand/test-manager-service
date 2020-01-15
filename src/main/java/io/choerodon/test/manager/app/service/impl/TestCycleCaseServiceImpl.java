package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.app.assembler.TestCaseAssembler;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.enums.TestCycleCaseDefectCode;
import io.choerodon.test.manager.infra.enums.TestStatusType;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.ConvertUtils;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import io.choerodon.test.manager.infra.util.RankUtil;
import io.choerodon.test.manager.infra.util.VerifyUpdateUtil;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCycleCaseServiceImpl implements TestCycleCaseService {

    private static final  double AVG_NUM = 500.00;

    @Autowired
    private TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    private TestCycleCaseAttachmentRelService attachmentRelService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestStatusService testStatusService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleMapper cycleMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCycleCaseStepService testCycleCaseStepService;

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Autowired
    private TestCaseAssembler testCaseAssembler;

    @Autowired
    private TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Autowired
    private TestCycleCaseHistoryMapper testCycleCaseHistory;

    @Autowired
    private TestCycleMapper testCycleMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;


    @Autowired
    private TestCaseAttachmentService testCaseAttachmentService;

    @Autowired
    private IIssueAttachmentService iIssueAttachmentService;

    @Autowired
    private VerifyUpdateUtil verifyUpdateUtil;



    @Override
    public void delete(Long cycleCaseId, Long projectId) {
        TestCycleCaseVO dto = new TestCycleCaseVO();
        dto.setExecuteId(cycleCaseId);
        delete(modelMapper.map(dto, TestCycleCaseDTO.class));
    }


    /**
     * 查询issues的cycleCase 在生成报表处使用
     *
     * @param issueIds
     * @param projectId
     * @return
     */
    @Override
    public List<TestCycleCaseVO> queryInIssues(Long[] issueIds, Long projectId, Long organizationId) {
        if (issueIds == null || issueIds.length == 0) {
            return new ArrayList<>();
        }
        Assert.notEmpty(issueIds, "erorr.query.cycle.in.issues.issueIds.not.null");
        List<TestCycleCaseVO> dto = modelMapper.map(testCycleCaseMapper.queryInIssues(issueIds), new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());
        if (dto == null || dto.isEmpty()) {
            return new ArrayList<>();
        }
        populateCycleCaseWithDefect(dto, projectId, organizationId, false);
        return dto;
    }

    @Override
    public List<TestCycleCaseVO> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds, Long projectId, Long organizationId) {
        Assert.notNull(cycleIds, "error.query.case.in.versions.project.not.be.null");

        if (!(ObjectUtils.isEmpty(cycleIds) ^ ObjectUtils.isEmpty(versionIds))) {
            Assert.notEmpty(cycleIds, "erorr.query.cycle.in.issues.issueIds.not.null");
        }

        List<TestCycleCaseVO> dto = modelMapper.map(testCycleCaseMapper.queryCaseAllInfoInCyclesOrVersions(cycleIds, versionIds), new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());
        populateCycleCaseWithDefect(dto, projectId, organizationId, false);
        populateUsers(dto);
        return dto;
    }

    /**
     * 将实例查询的Issue信息和缺陷关联的Issue信息合并到一起，为了减少一次外部调用。
     *
     * @param testCycleCaseVOS
     * @param projectId
     */
    private void populateCycleCaseWithDefect(List<TestCycleCaseVO> testCycleCaseVOS, Long projectId, Long organizationId, Boolean needDetails) {
        List<TestCycleCaseDefectRelVO> list = new ArrayList<>();
        for (TestCycleCaseVO v : testCycleCaseVOS) {
            List<TestCycleCaseDefectRelVO> defects = v.getDefects();
            Optional.ofNullable(defects).ifPresent(list::addAll);
            Optional.ofNullable(v.getSubStepDefects()).ifPresent(list::addAll);
        }

        Long[] issueLists = Stream.concat(list.stream().map(TestCycleCaseDefectRelVO::getIssueId),
                testCycleCaseVOS.stream().map(TestCycleCaseVO::getIssueId)).filter(Objects::nonNull).distinct()
                .toArray(Long[]::new);
        if (ObjectUtils.isEmpty(issueLists)) {
            return;
        }
        Map<Long, IssueInfosVO> defectMap = testCaseService.getIssueInfoMap(projectId, issueLists, needDetails, organizationId);
        list.forEach(v -> v.setIssueInfosVO(defectMap.get(v.getIssueId())));
        testCycleCaseVOS.forEach(v -> v.setIssueInfosVO(defectMap.get(v.getIssueId())));
    }

    private void populateUsers(List<TestCycleCaseVO> users) {
        List<Long> usersId = new ArrayList<>();
        users.stream().forEach(v -> {
            Optional.ofNullable(v.getAssignedTo()).ifPresent(usersId::add);
            Optional.ofNullable(v.getLastUpdatedBy()).ifPresent(usersId::add);
        });
        List<Long> ids = usersId.stream().distinct().filter(v -> !v.equals(Long.valueOf(0))).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(ids)) {
            Map<Long, UserDO> userMaps = userService.query(ids.toArray(new Long[ids.size()]));
            users.forEach(v -> {
                Optional.ofNullable(userMaps.get(v.getAssignedTo())).ifPresent(v::setAssigneeUser);
                Optional.ofNullable(userMaps.get(v.getLastUpdatedBy())).ifPresent(v::setLastUpdateUser);

            });
        }
    }

    @Override
    public TestCycleCaseVO create(TestCycleCaseVO testCycleCaseVO, Long projectId) {
        if (ObjectUtils.isEmpty(testCycleCaseVO.getExecutionStatus())) {
            testCycleCaseVO.setExecutionStatus(testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE));
        }
        testCycleCaseVO.setLastRank(testCycleCaseMapper.getLastedRank(testCycleCaseVO.getCycleId()));
        return runTestCycleCase(testCycleCaseVO, projectId);
    }

    @Override
    public List<TestCycleCaseVO> batchCreateForAutoTest(List<TestCycleCaseVO> list, Long projectId) {
        if (list == null || list.isEmpty()) {
            throw new CommonException("error.cycle.case.list.empty");
        }
        Date now = new Date();
        List<TestCycleCaseDTO> testCycleCaseDTOS = new ArrayList<>();
        for (TestCycleCaseVO testCycleCaseVO : list) {
            if (testCycleCaseVO == null || testCycleCaseVO.getExecuteId() != null) {
                throw new CommonException("error.cycle.case.insert.executeId.should.be.null");
            }
            TestCycleCaseDTO testCycleCaseDTO = modelMapper.map(testCycleCaseVO, TestCycleCaseDTO.class);
            testCycleCaseDTO.setCreationDate(now);
            testCycleCaseDTO.setLastUpdateDate(now);
            testCycleCaseDTO.setProjectId(projectId);
            testCycleCaseDTOS.add(testCycleCaseDTO);
        }
        testCycleCaseMapper.batchInsertTestCycleCases(testCycleCaseDTOS);

        return modelMapper.map(testCycleCaseDTOS, new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());
    }


    @Override
    public TestCycleCaseVO changeOneCase(TestCycleCaseVO testCycleCaseVO, Long projectId) {
        TestCycleCaseVO dto = modelMapper.map(changeStep(projectId, testCycleCaseVO), TestCycleCaseVO.class);
        return dto;
    }

    public void delete(TestCycleCaseDTO testCycleCaseDTO) {
        Optional.ofNullable(testCycleCaseMapper.select(testCycleCaseDTO)).ifPresent(m ->
                m.forEach(this::deleteCaseWithSubStep));
    }

    private void deleteCaseWithSubStep(TestCycleCaseDTO testCycleCaseDTO) {
        deleteByTestCycleCase(testCycleCaseDTO);
        attachmentRelService.delete(testCycleCaseDTO.getExecuteId(), TestAttachmentCode.ATTACHMENT_CYCLE_CASE);
        deleteLinkedDefect(testCycleCaseDTO.getExecuteId());
        testCycleCaseMapper.delete(testCycleCaseDTO);
    }

    private void deleteLinkedDefect(Long executeId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setDefectLinkId(executeId);
        testCycleCaseDefectRelDTO.setDefectType(TestCycleCaseDefectCode.CYCLE_CASE);
        testCycleCaseDefectRelMapper.select(testCycleCaseDefectRelDTO).forEach(v -> testCycleCaseDefectRelMapper.delete(v));
    }

    private void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteId(testCycleCaseDTO.getExecuteId());
        deleteStep(testCycleCaseStepDTO);
    }

    private void deleteStep(TestCycleCaseStepDTO testCycleCaseStepDTO) {
        Optional.ofNullable(testCycleCaseStepMapper.select(testCycleCaseStepDTO)).ifPresent(
                m -> m.forEach(v -> {
                    attachmentRelService.delete(v.getExecuteStepId(), TestAttachmentCode.ATTACHMENT_CYCLE_STEP);
                    deleteLinkedDefect(v.getExecuteStepId());
                })
        );
        testCycleCaseStepMapper.delete(testCycleCaseStepDTO);
    }

    @Override
    public List<TestCycleCaseDTO> queryWithAttachAndDefect(TestCycleCaseDTO convert, Pageable pageable) {
        return testCycleCaseMapper.queryWithAttachAndDefect(convert, (pageable.getPageNumber() - 1) * pageable.getPageSize(), pageable.getPageSize());
    }

    @Override
    public ExecutionStatusVO queryExecuteStatus(Long projectId, Long planId, Long cycleId) {
        Long total = 0L;
        Set<Long> cycleIds = new HashSet<>();
        // 查询文件夹下所有的目录
        if (!ObjectUtils.isEmpty(cycleId)) {
            cycleIds.addAll(queryCycleIds(cycleId, planId));
        }
        // 查询项目下自定义和默认状态
        TestStatusDTO testStatusDTO = new TestStatusDTO();
        testStatusDTO.setProjectId(projectId);
        testStatusDTO.setStatusType("CYCLE_CASE");
        List<TestStatusDTO> testStatusDTOList = testStatusMapper.queryAllUnderProject(testStatusDTO);
        testStatusDTOList.stream().forEach(e -> e.setCount(0L));
        List<TestStatusDTO> testStatusDTOS = testCycleCaseMapper.queryExecutionStatus(planId, cycleIds);
        if (!CollectionUtils.isEmpty(testStatusDTOS)) {
            for (TestStatusDTO test : testStatusDTOS) {
                total += test.getCount();
                testStatusDTOList.forEach(status -> {
                    if (test.getStatusId().equals(status.getStatusId())) {
                        status.setCount(test.getCount());
                    }
                });
            }
        }
        List<TestStatusVO> testStatusVOList = modelMapper.map(testStatusDTOList, new TypeToken<List<TestStatusVO>>() {
        }.getType());

        return new ExecutionStatusVO(total, testStatusVOList);
    }

    @Override
    public void update(TestCycleCaseVO testCycleCaseVO) {
        TestCycleCaseDTO testCycleCaseDTO = modelMapper.map(testCycleCaseVO, TestCycleCaseDTO.class);
        if (!StringUtils.isEmpty(testCycleCaseVO.getLastRank()) || !StringUtils.isEmpty(testCycleCaseVO.getNextRank())) {
            testCycleCaseDTO.setRank(RankUtil.Operation.UPDATE.getRank(testCycleCaseVO.getLastRank(), testCycleCaseVO.getNextRank()));
        }

        baseUpdate(testCycleCaseDTO);
        TestStatusVO defaultCaseStatus = testStatusService.queryDefaultStatus(TestStatusType.STATUS_TYPE_CASE, "通过");
        if(defaultCaseStatus.getStatusId().equals(testCycleCaseVO.getExecutionStatus())){
            List<TestCycleCaseStepDTO> testCycleCaseStepDTOList = testCycleCaseStepMapper.queryStepByExecuteId(testCycleCaseDTO.getExecuteId());
            if(!CollectionUtils.isEmpty(testCycleCaseStepDTOList)){
                if(testCycleCaseStepMapper.updateCycleCaseStepStatus(testCycleCaseDTO.getExecuteId())!=1){
                    throw new CommonException("error.update.step.status");
                }
            }
        }
    }

    @Override
    public void updateCaseAndStep(Long projectId, TestCycleCaseUpdateVO testCycleCaseUpdateVO, Boolean isAsync) {
        List<TestCycleCaseStepUpdateVO> testCycleCaseStepVOList = testCycleCaseUpdateVO.getTestCycleCaseStepUpdateVOS();
        List<TestCycleCaseStepVO> newTestCycleCaseStepVOS = modelMapper.map(testCycleCaseStepVOList, new TypeToken<List<TestCycleCaseStepVO>>() {
        }.getType());
        doRank(newTestCycleCaseStepVOS);
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteId(testCycleCaseUpdateVO.getExecuteId());
        List<TestCycleCaseStepDTO> oldTestCycleCaseStepDTOS = testCycleCaseStepMapper.select(testCycleCaseStepDTO);
        List<Long> noldStepIds = oldTestCycleCaseStepDTOS.stream().map(TestCycleCaseStepDTO::getExecuteStepId).collect(Collectors.toList());
        newTestCycleCaseStepVOS.forEach(newcCycle -> {
            if (!noldStepIds.contains(newcCycle.getExecuteStepId())) {
                //添加步骤
                testCycleCaseStepService.create(newcCycle);
            } else {
                //更新步骤
                testCycleCaseStepService.update(newcCycle);
            }
        });
        List<Long> newIds = newTestCycleCaseStepVOS.stream().map(TestCycleCaseStepVO::getExecuteStepId).collect(Collectors.toList());
        oldTestCycleCaseStepDTOS.forEach(oldeCycle -> {
            if (!newIds.contains(oldeCycle.getExecuteStepId())) {
                //删除步骤
                testCycleCaseStepService.delete(oldeCycle.getExecuteStepId());
            }
        });
        //3.更新执行用例
        TestCycleCaseDTO testCycleCaseDTO = modelMapper.map(testCycleCaseUpdateVO, TestCycleCaseDTO.class);
        baseUpdate(testCycleCaseDTO);

        if (isAsync) {
            TestCycleCaseDTO testCycleCase = testCycleCaseMapper.selectByPrimaryKey(testCycleCaseDTO.getExecuteId());
            CaseCompareRepVO caseCompareRepVO = new CaseCompareRepVO();
            caseCompareRepVO.setSyncToCase(true);
            caseCompareRepVO.setExecuteId(testCycleCase.getExecuteId());
            caseCompareRepVO.setCaseId(testCycleCase.getCaseId());
            Map<Long, CaseCompareVO> cycleCaseMap = testCycleCaseMapper.queryTestCaseMap(Arrays.asList(testCycleCase.getExecuteId()));
            Map<Long, CaseCompareVO> caseMap = testCaseMapper.queryTestCaseMap(Arrays.asList(testCycleCase.getCaseId()), Arrays.asList(testCycleCase.getExecuteId()));
            List<TestCycleCaseDTO> testCycleCaseDTOS = Arrays.asList(testCycleCase);
            testCycleCaseDTOS.forEach(cycleCase -> {
                CaseCompareVO cycleCaseVo = cycleCaseMap.get(cycleCase.getCaseId());
                CaseCompareVO caseVo = caseMap.get(cycleCase.getCaseId());
                Boolean changeCase = false;
                Boolean changeAttach = false;
                Boolean changeStep = false;
                if (!ObjectUtils.isEmpty(caseVo)) {
                    if (!Objects.equals(JSON.toJSON(cycleCaseVo.getTestCase()), JSON.toJSON(caseVo.getTestCase()))) {
                        changeCase = true;
                    }
                    if (!Objects.equals(JSON.toJSON(cycleCaseVo.getCaseStep()), JSON.toJSON(caseVo.getCaseStep()))) {
                        changeStep = true;
                    }
                    if (!Objects.equals(JSON.toJSON(cycleCaseVo.getCaseAttach()), JSON.toJSON(caseVo.getCaseAttach()))) {
                        changeAttach = true;
                    }
                }
                caseCompareRepVO.setChangeCase(changeCase);
                caseCompareRepVO.setChangeAttach(changeAttach);
                caseCompareRepVO.setChangeStep(changeStep);
                updateCompare(projectId, caseCompareRepVO);
            });

        }
    }

    @Override
    public PageInfo<TestFolderCycleCaseVO> listAllCaseByCycleId(Long projectId, Long planId, Long cycleId, Pageable pageable, SearchDTO searchDTO) {
        // 查询文件夹下所有的目录
        Set<Long> cycleIds = new HashSet<>();
        if (!ObjectUtils.isEmpty(cycleId)) {
            cycleIds.addAll(queryCycleIds(cycleId, planId));
        }
        // 查询文件夹下的的用例
        PageInfo<TestCycleCaseDTO> caseDTOPageInfo = PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() ->
                testCycleCaseMapper.queryFolderCycleCase(planId, cycleIds, searchDTO));
        Map<Long, UserMessageDTO> userMap = testCaseAssembler.getUserMap(null, modelMapper.map(caseDTOPageInfo.getList(), new TypeToken<List<BaseDTO>>() {
        }.getType()));
        List<TestFolderCycleCaseVO> testFolderCycleCaseVOS = caseDTOPageInfo.getList().stream().map(v -> testCaseAssembler.setAssianUser(v,userMap)).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(testFolderCycleCaseVOS)) {
            return new PageInfo<>();
        }

        List<Long> executedIds = testFolderCycleCaseVOS.stream().map(TestFolderCycleCaseVO::getExecuteId).collect(Collectors.toList());
        //对比是否更新
        List<Long> caseIds = testFolderCycleCaseVOS.stream().map(TestFolderCycleCaseVO::getCaseId).collect(Collectors.toList());

        Map<Long, CaseCompareVO> cycleCaseMap = testCycleCaseMapper.queryTestCaseMap(executedIds);
        Map<Long, CaseCompareVO> caseMap = testCaseMapper.queryTestCaseMap(caseIds, executedIds);
        List<TestCaseDTO> testCaseDTOS = testCaseService.listByCaseIds(projectId, caseIds);
        Map<Long, TestCaseDTO> testCaseMap = testCaseDTOS.stream().collect(Collectors.toMap(TestCaseDTO::getCaseId, Function.identity()));
        testFolderCycleCaseVOS.forEach(cycleCase -> {
            Long caseId = cycleCase.getCaseId();
            TestCaseDTO testCaseDTO = testCaseMap.get(caseId);
            if (ObjectUtils.isEmpty(testCaseDTO)) {
                return;
            }
            Boolean hasChange = false;
            CaseCompareVO cycleCaseVo = cycleCaseMap.get(caseId);
            CaseCompareVO caseVo = caseMap.get(caseId);
            Boolean changeCase = false;
            Boolean changeStep = false;
            Boolean changeAttach = false;
            if (cycleCase.getVersionNum() < testCaseDTO.getVersionNum()) {
                if (!ObjectUtils.isEmpty(caseVo)) {
                    if (!Objects.equals(JSON.toJSON(cycleCaseVo.getTestCase()), JSON.toJSON(caseVo.getTestCase()))) {
                        hasChange = true;
                        changeCase = true;
                    }
                    if (!Objects.equals(JSON.toJSON(cycleCaseVo.getCaseStep()), JSON.toJSON(caseVo.getCaseStep()))) {
                        hasChange = true;
                        changeStep = true;
                    }
                    if (!Objects.equals(JSON.toJSON(cycleCaseVo.getCaseAttach()), JSON.toJSON(caseVo.getCaseAttach()))) {
                        hasChange = true;
                        changeAttach = true;
                    }
                }
            }
            cycleCase.setChangeCase(changeCase);
            cycleCase.setChangeStep(changeStep);
            cycleCase.setChangeAttach(changeAttach);
            cycleCase.setHasChange(hasChange);

        });

        PageInfo<TestFolderCycleCaseVO> testFolderCycleCaseVOPageInfo = modelMapper.map(caseDTOPageInfo, PageInfo.class);
        testFolderCycleCaseVOPageInfo.setList(testFolderCycleCaseVOS);
        return testFolderCycleCaseVOPageInfo;
    }

    @Override
    public TestCycleCaseInfoVO queryCycleCaseInfo(Long executeId, Long projectId, Long planId, Long cycleId, SearchDTO searchDTO) {
        Set<Long> cycleIds = new HashSet<>();
        if (!ObjectUtils.isEmpty(cycleId)) {
            cycleIds.addAll(queryCycleIds(cycleId, planId));
        }
        // 查询循环下的用例
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.queryFolderCycleCase(planId, cycleIds, searchDTO);
        int index = 0;
        TestCycleCaseDTO testCycleCaseDTO = null;
        for (TestCycleCaseDTO cyclecase : testCycleCaseDTOS) {
            if (cyclecase.getExecuteId().equals(executeId)) {
                testCycleCaseDTO = cyclecase;
                index = testCycleCaseDTOS.indexOf(cyclecase);
                break;
            }
        }
        if (ObjectUtils.isEmpty(testCycleCaseDTO)) {
            throw new CommonException("error.cycle.case.not.exist");
        }
        TestCycleCaseInfoVO testCycleCaseInfoVO = modelMapper.map(testCycleCaseDTO, TestCycleCaseInfoVO.class);

        testCycleCaseInfoVO.setExecutorDate(testCycleCaseDTO.getLastUpdateDate());
        previousNextId(index, testCycleCaseDTOS, testCycleCaseInfoVO);
        return testCaseAssembler.cycleCaseExtraInfo(testCycleCaseInfoVO);
    }

    @Override
    public void batchInsertByTestCase(Map<Long, TestCycleDTO> testCycleMap, List<Long> caseIds, Long project) {
        int count = testCaseMapper.countByProjectIdAndCaseIds(project, caseIds);
        int ceil = (int) Math.ceil(count / AVG_NUM == 0 ? 1 : count / AVG_NUM);
        for (int i = 1; i <= ceil; i++) {
            PageInfo<TestCaseDTO> testCasePage = PageHelper.startPage(i, (int) AVG_NUM).doSelectPageInfo(() -> testCaseMapper.listByCaseIds(project, caseIds));
            List<TestCaseDTO> testCaseDTOS = testCasePage.getList();
            if (CollectionUtils.isEmpty(testCaseDTOS)) {
                return;
            }
            // 获取case关联的步骤
            List<Long> currentIds = testCaseDTOS.stream().map(TestCaseDTO::getCaseId).collect(Collectors.toList());

            // 获取case关联的附件
            List<TestCaseAttachmentDTO> attachmentDTOS = testAttachmentMapper.listByCaseIds(currentIds);
            Map<Long, List<TestCaseAttachmentDTO>> attachmentMap = attachmentDTOS.stream().collect(Collectors.groupingBy(TestCaseAttachmentDTO::getCaseId));
            // 插入
            Long defaultStatusId = testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE);
            List<TestCycleCaseDTO> testCycleCaseDTOS = caseToCycleCase(testCaseDTOS, testCycleMap, defaultStatusId);
            bathcInsert(testCycleCaseDTOS);
            int stepCount = testCaseStepMapper.countByProjectIdAndCaseIds(currentIds);
            int ceilStep = (int) Math.ceil(stepCount / AVG_NUM == 0 ? 1 : stepCount / AVG_NUM);
            for (int page = 1; page <= ceilStep; page++) {
                PageInfo<TestCaseStepDTO> stepPageInfo = PageHelper.startPage(page, (int) AVG_NUM).doSelectPageInfo(() -> testCaseStepMapper.listByCaseIds(currentIds));
                List<TestCaseStepDTO> testCaseStepDTOS = stepPageInfo.getList();
                Map<Long, List<TestCaseStepDTO>> caseStepMap = testCaseStepDTOS.stream().collect(Collectors.groupingBy(TestCaseStepDTO::getIssueId));
                testCycleCaseStepService.batchInsert(testCycleCaseDTOS, caseStepMap);
            }
            // 同步附件
            testCycleCaseAttachmentRelService.batchInsert(testCycleCaseDTOS, attachmentMap);
        }
    }

    @Override
    public TestCycleCaseUpdateVO queryCaseAndStep(Long executeId) {
        TestCycleCaseDTO testCycleCaseDTO = testCycleCaseMapper.selectCycleCaseAndStep(executeId);
        return testCaseAssembler.dtoToUpdateVO(testCycleCaseDTO);
    }

    @Override
    public void batchAssignCycleCase(Long projectId, Long userId, List<Long> cycleCaseId) {
        if (CollectionUtils.isEmpty(cycleCaseId)) {
            throw new CommonException("error cycleCase id is null ");
        }
        testCycleCaseMapper.batchAssign(userId, cycleCaseId);
    }

    @Override
    public void baseUpdate(TestCycleCaseDTO testCycleCaseDTO) {
        if (testCycleCaseMapper.updateByPrimaryKeySelective(testCycleCaseDTO) != 1) {
            throw new CommonException("error.update.cycle.case");
        }
    }


    @Override
    public void batchDeleteByExecuteIds(List<Long> executeIds) {
        if (CollectionUtils.isEmpty(executeIds)) {
            return;
        }
        // 删除步骤
        List<TestCycleCaseStepDTO> list = testCycleCaseStepMapper.listByexecuteIds(executeIds);
        if (!CollectionUtils.isEmpty(list)) {
            List<Long> stepIds = list.stream().map(TestCycleCaseStepDTO::getExecuteStepId).collect(Collectors.toList());
            testCycleCaseDefectRelMapper.batchDeleteByLinkIdsAndType(stepIds, TestAttachmentCode.ATTACHMENT_CYCLE_STEP);
            testCycleCaseAttachmentRelService.batchDeleteByExecutIds(stepIds, TestAttachmentCode.ATTACHMENT_CYCLE_STEP);
        }
        testCycleCaseStepMapper.batchDeleteByExecutIds(executeIds);
        // 删除附件信息
        testCycleCaseAttachmentRelService.batchDeleteByExecutIds(executeIds, TestAttachmentCode.ATTACHMENT_CYCLE_CASE);
        // 删除测试执行
        testCycleCaseMapper.batchDeleteByExecutIds(executeIds);
        // 删除执行关联的缺陷
        testCycleCaseDefectRelMapper.batchDeleteByLinkIdsAndType(executeIds, TestAttachmentCode.ATTACHMENT_CYCLE_CASE);
        // 删除日志
        testCycleCaseHistory.batchDeleteByExecutIds(executeIds);
    }

    @Override
    public CaseChangeVO selectUpdateCompare(Long projectId, Long executeId) {
        TestCycleCaseDTO testCycleCaseDTO = testCycleCaseMapper.selectByPrimaryKey(executeId);
        Long caseId = testCycleCaseDTO.getCaseId();
        TestCycleDTO testCycleDTO = cycleMapper.selectByPrimaryKey(testCycleCaseDTO.getCycleId());
        TestCycleCaseVO testCycleCaseVO = dtoToVo(testCycleCaseDTO, testCycleDTO);
        TestCaseInfoVO testCaseInfoVO = testCaseService.queryCaseRep(caseId);
        CaseChangeVO caseChangeVO = new CaseChangeVO();
        caseChangeVO.setTestCycleCase(testCycleCaseVO);
        caseChangeVO.setTestCase(testCaseInfoVO);
        return caseChangeVO;

    }

    @Override
    public void updateCompare(Long projectId, CaseCompareRepVO caseCompareRepVO) {
        TestCycleCaseDTO testCycleCaseDTO = testCycleCaseMapper.selectByPrimaryKey(caseCompareRepVO.getExecuteId());
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(caseCompareRepVO.getCaseId());
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (!caseCompareRepVO.getSyncToCase()) {
            TestCycleCaseDTO testCycleCase = new TestCycleCaseDTO();
            if (caseCompareRepVO.getChangeCase()) {
                testCycleCase.setSummary(testCaseDTO.getSummary());
                testCycleCase.setDescription(testCaseDTO.getDescription());
                testCycleCase.setVersionNum(testCaseDTO.getVersionNum());
            }
            if (caseCompareRepVO.getChangeAttach()) {
                testCycleCaseAttachmentRelService.snycByCase(testCycleCaseDTO, testCaseDTO);
                testCycleCase.setVersionNum(testCaseDTO.getVersionNum());
            }
            if (caseCompareRepVO.getChangeStep()) {
                testCycleCaseStepService.snycByCase(testCycleCaseDTO, testCaseDTO);
                testCycleCase.setVersionNum(testCaseDTO.getVersionNum());
            }
            testCycleCase.setExecuteId(testCycleCaseDTO.getExecuteId());
            testCycleCase.setObjectVersionNumber(testCycleCaseDTO.getObjectVersionNumber());
            testCycleCase.setLastUpdatedBy(userDetails.getUserId());
            baseUpdate(testCycleCase);
        } else {
            if (caseCompareRepVO.getChangeCase()) {
                TestCaseRepVO testCaseRepVO = new TestCaseRepVO();
                testCaseRepVO.setCaseId(testCycleCaseDTO.getCaseId());
                testCaseRepVO.setSummary(testCycleCaseDTO.getSummary());
                testCaseRepVO.setDescription(testCycleCaseDTO.getDescription());
                testCaseRepVO.setObjectVersionNumber(testCaseDTO.getObjectVersionNumber());
                testCaseRepVO.setExecuteId(testCycleCaseDTO.getExecuteId());
                List<String> fieldList = verifyUpdateUtil.verifyUpdateData((JSONObject) JSON.toJSON(testCaseRepVO), testCaseRepVO);
                testCaseService.updateCase(testCaseDTO.getProjectId(), testCaseRepVO, fieldList.toArray(new String[fieldList.size()]));
                testCycleCaseDTO.setVersionNum(testCaseDTO.getVersionNum() + 1);
                baseUpdate(testCycleCaseDTO);
            }
            if (caseCompareRepVO.getChangeAttach()) {
                List<TestCycleCaseAttachmentRelVO> testCycleCaseAttachmentRelVOS = testCycleCaseAttachmentRelService.listByExecuteId(caseCompareRepVO.getExecuteId());
                testCaseAttachmentService.asynAttachToCase(testCycleCaseAttachmentRelVOS,testCaseDTO,testCycleCaseDTO.getExecuteId());
            }
            if (caseCompareRepVO.getChangeStep()) {
                testCaseStepMapper.deleteByCaseId(caseCompareRepVO.getCaseId());
                List<TestCycleCaseStepDTO> cycleCaseStepDTOS = testCycleCaseStepMapper.listByexecuteIds(Arrays.asList(testCycleCaseDTO.getExecuteId()));
                if (CollectionUtils.isEmpty(cycleCaseStepDTOS)) {
                    return;
                }
                Map<Long, TestCycleCaseStepDTO> stepMap = cycleCaseStepDTOS.stream().collect(Collectors.toMap(TestCycleCaseStepDTO::getExecuteStepId, Function.identity()));
                List<TestCaseStepDTO> stepDTOS = cycleCaseStepDTOS.stream().map(v -> testCaseAssembler.cycleStepToCaseStep(v, testCaseDTO, userDetails)).collect(Collectors.toList());
                testCaseStepMapper.batchInsertTestCaseSteps(stepDTOS);
                stepDTOS.forEach(v -> {
                    TestCycleCaseStepDTO testCycleCaseStepDTO = stepMap.get(v.getCycleCaseStepId());
                    testCycleCaseStepDTO.setStepId(v.getStepId());
                    testCycleCaseStepService.baseUpdate(testCycleCaseStepDTO);
                });
                List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(testCaseDTO.getProjectId(), testCaseDTO.getCaseId());
                if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
                        List<TestCycleCaseDTO> list = testCycleCaseDTOS.stream().filter(v -> !testCycleCaseDTO.getExecuteId().equals(v.getExecuteId())).collect(Collectors.toList());
                        testCaseAssembler.autoAsyncCase(list,false,true,false);
                }
            }

        }
    }

    @Override
    public void ignoreUpdate(Long projectId, Long executedId) {
        TestCycleCaseDTO testCycleCaseDTO = testCycleCaseMapper.selectByPrimaryKey(executedId);
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(testCycleCaseDTO.getCaseId());
        testCycleCaseDTO.setVersionNum(testCaseDTO.getVersionNum());
        baseUpdate(testCycleCaseDTO);
    }

    @Override
    public void importCase(Long projectId, Long cycleId, Map<Long, CaseSelectVO> map, Long planId) {
        // 校验是不是底层文件夹
        checkImport(cycleId);
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        List<TestCaseDTO> testCaseDTOS = testCaseMapper.listByProject(projectId);
        Map<Long, TestCaseDTO> caseMap = testCaseDTOS.stream().collect(Collectors.toMap(TestCaseDTO::getCaseId, Function.identity()));
        Map<Long, List<TestCaseDTO>> folderCaseMap = testCaseDTOS.stream().collect(Collectors.groupingBy(TestCaseDTO::getFolderId));
        List<Long> existCaseIds = testCycleCaseMapper.listByPlanId(planId);
        List<TestCaseDTO> list = new ArrayList<>();
        for (Map.Entry<Long,CaseSelectVO> entry : map.entrySet()) {
            List<Long> insertCaseIds = new ArrayList<>();
            CaseSelectVO caseSelectVO = entry.getValue();
            List<TestCaseDTO> caseList = folderCaseMap.get(entry.getKey());
            if (CollectionUtils.isEmpty(caseList)) {
                continue;
            }
            List<Long> collect = caseList.stream().map(TestCaseDTO::getCaseId).collect(Collectors.toList());
            if (!caseSelectVO.getCustom()) {
                // 去掉文件夹下已经导入的用例
                collect.removeAll(existCaseIds);
                insertCaseIds.addAll(collect);
            } else {

                if (!CollectionUtils.isEmpty(caseSelectVO.getSelected())) {
                    insertCaseIds.addAll(caseSelectVO.getSelected());

                } else if (!CollectionUtils.isEmpty(caseSelectVO.getUnSelected())) {
                    collect.removeAll(existCaseIds);
                    collect.removeAll(caseSelectVO.getUnSelected());
                    insertCaseIds.addAll(collect);
                }

            }
            insertCaseIds.forEach(v -> list.add(caseMap.get(v)));
        }
        TestCycleDTO testCycleDTO = cycleMapper.selectByPrimaryKey(cycleId);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Long defaultStatusId = testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE);
        List<List<TestCaseDTO>> lists = ConvertUtils.averageAssign(list, (int) Math.ceil(list.size() / AVG_NUM == 0 ? 1 : list.size() / AVG_NUM));
        lists.forEach(testCaseDTOList -> {
            List<Long> caseIds = testCaseDTOList.stream().map(TestCaseDTO::getCaseId).collect(Collectors.toList());
            List<TestCycleCaseDTO> testCycleCaseDTOS = caseToCycleCase(testCaseDTOList, testCycleDTO, defaultStatusId);
            bathcInsert(testCycleCaseDTOS);
            // 同步步骤
            int count = testCaseStepMapper.countByProjectIdAndCaseIds(caseIds);
            int ceil = (int) Math.ceil(count / AVG_NUM == 0 ? 1 : count / AVG_NUM);
            for(int page = 1;page <= ceil;page++) {
                PageInfo<TestCaseStepDTO> caseStepDTOPageInfo = PageHelper.startPage(page, 500).doSelectPageInfo(() -> testCaseStepMapper.listByCaseIds(caseIds));
                List<TestCaseStepDTO> testCaseStepDTOS = caseStepDTOPageInfo.getList();
                Map<Long, List<TestCaseStepDTO>> caseStepMap = testCaseStepDTOS.stream().collect(Collectors.groupingBy(TestCaseStepDTO::getIssueId));
                testCycleCaseStepService.batchInsert(testCycleCaseDTOS, caseStepMap);
            }
            // 同步附件
            List<TestCaseAttachmentDTO> attachmentDTOS = testAttachmentMapper.listByCaseIds(caseIds);
            Map<Long, List<TestCaseAttachmentDTO>> attachmentMap = attachmentDTOS.stream().collect(Collectors.groupingBy(TestCaseAttachmentDTO::getCaseId));
            testCycleCaseAttachmentRelService.batchInsert(testCycleCaseDTOS, attachmentMap);
        });
    }

    @Override
    public void cloneCycleCase(Map<Long, Long> cycleMapping, List<Long> cycIds) {
        Long defaultStatusId = testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE);
        Integer count = testCycleCaseMapper.countByCycleIds(cycIds);
        int ceil = (int) Math.ceil(count / AVG_NUM == 0 ? 1 : count / AVG_NUM);
        for(int page=1;page<=ceil;page ++) {
            PageInfo<TestCycleCaseDTO> testCyclePageInfo = PageHelper.startPage(page, 500).doSelectPageInfo(() -> testCycleCaseMapper.listByCycleIds(cycIds));
            if (CollectionUtils.isEmpty(testCyclePageInfo.getList())) {
                return;
            }
            List<TestCycleCaseDTO> testCycleCaseDTOS = testCyclePageInfo.getList();
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            List<Long> olderExecuteIds = new ArrayList<>();
            testCycleCaseDTOS .forEach(v -> {
                olderExecuteIds.add(v.getExecuteId());
                v.setLastExecuteId(v.getExecuteId());
                v.setCycleId(cycleMapping.get(v.getCycleId()));
                v.setCreatedBy(userDetails.getUserId());
                v.setLastUpdatedBy(userDetails.getUserId());
                v.setExecuteId(null);
                v.setExecutionStatus(defaultStatusId);
            });

            bathcInsert(testCycleCaseDTOS);
            Map<Long, Long> caseIdMap = new HashMap<>();
            testCycleCaseDTOS.forEach(v -> caseIdMap.put(v.getLastExecuteId(), v.getExecuteId()));
            // 复制步骤
            testCycleCaseStepService.cloneStep(caseIdMap, olderExecuteIds);
            // 复制附件
            testCycleCaseAttachmentRelService.cloneAttach(caseIdMap, olderExecuteIds,"CYCLE_CASE");
        }
    }

    private void checkImport(Long cycleId) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setParentCycleId(cycleId);
        List<TestCycleDTO> list = cycleMapper.select(testCycleDTO);
        if (!CollectionUtils.isEmpty(list)) {
            throw new CommonException("folder.not.bottom");
        }
    }

    private TestCycleCaseVO dtoToVo(TestCycleCaseDTO testCycleCaseDTO, TestCycleDTO testCycleDTO) {
        TestCycleCaseVO testCycleCaseVO = modelMapper.map(testCycleCaseDTO, TestCycleCaseVO.class);
        List<TestCycleCaseStepDTO> cycleCaseStepDTOS = testCycleCaseStepMapper.querListByexecuteId(testCycleCaseDTO.getExecuteId());
        if (!CollectionUtils.isEmpty(cycleCaseStepDTOS)) {
            List<TestCycleCaseStepVO> cycleCaseStepVOS = modelMapper.map(cycleCaseStepDTOS, new TypeToken<List<TestCycleCaseStepVO>>() {
            }.getType());
            testCycleCaseVO.setCycleCaseStep(cycleCaseStepVOS);
        }
        List<TestCycleCaseAttachmentRelVO> testCycleCaseAttachmentRelVOS = testCycleCaseAttachmentRelService.listByExecuteId(testCycleCaseDTO.getExecuteId());
        if (!CollectionUtils.isEmpty(testCycleCaseAttachmentRelVOS)) {
            testCycleCaseVO.setCaseAttachment(testCycleCaseAttachmentRelVOS);
        }
        return testCycleCaseVO;
    }


    @Override
    public List<TestCycleCaseDTO> listByCycleIds(List<Long> cycleIds) {
        if (cycleIds.isEmpty()) {
            return new ArrayList<>();
        } else {
            return testCycleCaseMapper.listByCycleIds(cycleIds);
        }
    }

    private TestCycleCaseVO runTestCycleCase(TestCycleCaseVO testCycleCaseVO, Long projectId) {
        Assert.notNull(projectId, "error.projectId.illegal");

        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setRank(RankUtil.Operation.INSERT.getRank(testCycleCaseVO.getLastRank(), testCycleCaseVO.getNextRank()));
        testCycleCaseDTO.setProjectId(projectId);
        testCycleCaseDTO.setCycleId(testCycleCaseVO.getCycleId());
        testCycleCaseDTO.setCaseId(testCycleCaseVO.getIssueId());
        testCycleCaseDTO.setExecutionStatus(testCycleCaseVO.getExecutionStatus());
        if (testCycleCaseMapper.validateCycleCaseInCycle(testCycleCaseDTO).longValue() > 0) {
            throw new CommonException("error.cycle.case.insert.have.one.case.in.cycle");
        }
        testCycleCaseMapper.insert(testCycleCaseDTO);
        createTestCycleCaseStep(testCycleCaseDTO);
        return modelMapper.map(testCycleCaseDTO, TestCycleCaseVO.class);
    }

    @Override
    public void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO) {
        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
        testCaseStepDTO.setIssueId(testCycleCaseDTO.getCaseId());
        List<TestCaseStepDTO> testCaseStepES = testCaseStepMapper.query(testCaseStepDTO);
        Long defaultStepStatusId = testStatusMapper.getDefaultStatus(TestStatusType.STATUS_TYPE_CASE_STEP);
        testCaseStepES.forEach(v -> {
            TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
            testCycleCaseStepDTO.setStepStatus(defaultStepStatusId);
            testCycleCaseStepDTO.setStepId(v.getStepId());
            testCycleCaseStepDTO.setExecuteId(testCycleCaseDTO.getExecuteId());
            testCycleCaseStepMapper.insert(testCycleCaseStepDTO);
        });
    }

    private TestCycleCaseDTO changeStep(Long projectId, TestCycleCaseVO testCycleCaseVO) {
        TestCycleCaseDTO testCycleCaseDTO = modelMapper.map(testCycleCaseVO, TestCycleCaseDTO.class);
        testCycleCaseDTO.setProjectId(projectId);
        if (!StringUtils.isEmpty(testCycleCaseVO.getLastRank()) || !StringUtils.isEmpty(testCycleCaseVO.getNextRank())) {
            testCycleCaseDTO.setRank(RankUtil.Operation.UPDATE.getRank(testCycleCaseVO.getLastRank(), testCycleCaseVO.getNextRank()));
        }
        Assert.notNull(testCycleCaseDTO.getProjectId(), "error.projectId.illegal");
        testCycleCaseMapper.updateByPrimaryKey(testCycleCaseDTO);
        return testCycleCaseDTO;
    }


    @Override
    public TestCycleCaseDTO baseInsert(TestCycleCaseDTO testCycleCaseDTO) {
        if (ObjectUtils.isEmpty(testCycleCaseDTO)) {
            throw new CommonException("error.insert.cycle.case.is.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseMapper::insertSelective, testCycleCaseDTO, 1, "error.insert.cycle.case");
        return testCycleCaseDTO;
    }

    private void queryAllCycleIds(Long cycleId, Set<Long> folderIds, Map<Long, List<TestCycleDTO>> folderMap) {
        folderIds.add(cycleId);
        List<TestCycleDTO> testCycleDTOS = folderMap.get(cycleId);
        if (!CollectionUtils.isEmpty(testCycleDTOS)) {
            testCycleDTOS.forEach(v -> queryAllCycleIds(v.getCycleId(), folderIds, folderMap));
        }
    }

    private Set<Long> queryCycleIds(Long cycleId, Long planId) {
        Set<Long> cycleIds = new HashSet<>();
        if (!ObjectUtils.isEmpty(planId)) {
            TestCycleDTO testCycleDTO = new TestCycleDTO();
            testCycleDTO.setPlanId(planId);
            List<TestCycleDTO> cycleDTOS = testCycleMapper.select(testCycleDTO);
            cycleDTOS.stream().forEach(e -> {
                if (e.getParentCycleId() == null) {
                    e.setParentCycleId(0L);
                }
            });
            Map<Long, List<TestCycleDTO>> folderMap = cycleDTOS.stream().collect(Collectors.groupingBy(TestCycleDTO::getParentCycleId));
            queryAllCycleIds(cycleId, cycleIds, folderMap);
        }
        return cycleIds;
    }

    private List<TestCycleCaseDTO> caseToCycleCase(List<TestCaseDTO> testCaseDTOS, Map<Long, TestCycleDTO> testCycleMap, Long defaultStatusId) {
        if (CollectionUtils.isEmpty(testCaseDTOS)) {
            return new ArrayList<>();
        }
        List<TestCycleCaseDTO> testCycleCaseDTOS = new ArrayList<>();
        String preRank = null;
        for (TestCaseDTO v : testCaseDTOS
        ) {
            TestCycleDTO testCycleDTO = testCycleMap.get(v.getFolderId());
            if (!ObjectUtils.isEmpty(testCycleDTO)) {
                TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
                testCycleCaseDTO.setCycleId(testCycleDTO.getCycleId());
                testCycleCaseDTO.setCaseId(v.getCaseId());
                testCycleCaseDTO.setDescription(v.getDescription());
                testCycleCaseDTO.setProjectId(v.getProjectId());
                testCycleCaseDTO.setVersionNum(v.getVersionNum());
                testCycleCaseDTO.setExecutionStatus(defaultStatusId);
                testCycleCaseDTO.setCreatedBy(testCycleDTO.getCreatedBy());
                testCycleCaseDTO.setLastUpdatedBy(testCycleDTO.getLastUpdatedBy());
                testCycleCaseDTO.setSummary(v.getSummary());
                testCycleCaseDTO.setSource("none");
                testCycleCaseDTO.setRank(RankUtil.Operation.INSERT.getRank(preRank, null));
                preRank = testCycleCaseDTO.getRank();
                testCycleCaseDTOS.add(testCycleCaseDTO);
            }
        }
        return testCycleCaseDTOS;
    }

    private List<TestCycleCaseDTO> caseToCycleCase(List<TestCaseDTO> testCaseDTOS, TestCycleDTO testCycleDTO, Long defaultStatusId) {
        if (CollectionUtils.isEmpty(testCaseDTOS)) {
            return new ArrayList<>();
        }
        List<TestCycleCaseDTO> testCycleCaseDTOS = new ArrayList<>();
        String preRank = null;
        for (TestCaseDTO v : testCaseDTOS) {
            TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
            testCycleCaseDTO.setCycleId(testCycleDTO.getCycleId());
            testCycleCaseDTO.setCaseId(v.getCaseId());
            testCycleCaseDTO.setDescription(v.getDescription());
            testCycleCaseDTO.setProjectId(v.getProjectId());
            testCycleCaseDTO.setVersionNum(v.getVersionNum());
            testCycleCaseDTO.setExecutionStatus(defaultStatusId);
            testCycleCaseDTO.setCreatedBy(testCycleDTO.getCreatedBy());
            testCycleCaseDTO.setLastUpdatedBy(testCycleDTO.getLastUpdatedBy());
            testCycleCaseDTO.setSummary(v.getSummary());
            testCycleCaseDTO.setRank(RankUtil.Operation.INSERT.getRank(preRank, null));
            preRank = testCycleCaseDTO.getRank();
            testCycleCaseDTO.setSource("none");
            testCycleCaseDTOS.add(testCycleCaseDTO);
        }
        return testCycleCaseDTOS;
    }

    private void bathcInsert(List<TestCycleCaseDTO> testCycleCaseDTOS) {
        if (CollectionUtils.isEmpty(testCycleCaseDTOS)) {
            return;
        }
        testCycleCaseMapper.batchInsert(testCycleCaseDTOS);
    }

    private void previousNextId(int index, List<TestCycleCaseDTO> list, TestCycleCaseInfoVO testCycleCaseInfoVO) {
        Long previousExecuteId;
        Long nextExecuteId;
        if (index - 1 < 0) {
            previousExecuteId = null;
        } else {
            previousExecuteId = list.get(index - 1).getExecuteId();
        }
        if (index + 1 >= list.size()) {
            nextExecuteId = null;
        } else {
            nextExecuteId = list.get(index + 1).getExecuteId();
        }
        testCycleCaseInfoVO.setPreviousExecuteId(previousExecuteId);
        testCycleCaseInfoVO.setNextExecuteId(nextExecuteId);
    }

    private void doRank(List<TestCycleCaseStepVO> newTestCycleCaseStepVOS) {
        String preRank = null;
        for (TestCycleCaseStepVO testCycleCaseStepVO : newTestCycleCaseStepVOS) {
            testCycleCaseStepVO.setRank(RankUtil.Operation.INSERT.getRank(null, preRank));
            preRank = testCycleCaseStepVO.getRank();
        }
    }

}