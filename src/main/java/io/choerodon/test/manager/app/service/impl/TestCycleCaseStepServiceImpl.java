package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
import io.choerodon.test.manager.api.vo.TestStatusVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.enums.TestStatusType;
import io.choerodon.test.manager.infra.mapper.*;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;
import io.choerodon.test.manager.infra.util.PageUtil;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCycleCaseStepServiceImpl implements TestCycleCaseStepService {
    private static final  double AVG_NUM = 500.00;

    @Value("${spring.datasource.url}")
    private String dsUrl;


    @Autowired
    private TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestStatusService testStatusService;

    @Autowired
    private TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;
    @Override
    public void update(TestCycleCaseStepVO testCycleCaseStepVO) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = modelMapper.map(testCycleCaseStepVO, TestCycleCaseStepDTO.class);
        baseUpdate(testCycleCaseStepDTO);
        TestStatusVO defaultStatus = testStatusService.queryDefaultStatus(TestStatusType.STATUS_TYPE_CASE_STEP, "通过");
        if (defaultStatus.getStatusId().equals(testCycleCaseStepVO.getStepStatus())) {
            List<TestCycleCaseStepDTO> testCycleCaseStepDTOS = testCycleCaseStepMapper.queryStepByExecuteId(testCycleCaseStepDTO.getExecuteId());
            if (!CollectionUtils.isEmpty(testCycleCaseStepDTOS)) {
                //步骤-全部通过->用例通过
                boolean result = testCycleCaseStepDTOS.stream().allMatch(e -> e.getStepStatus().equals(defaultStatus.getStatusId()));
               if(result){
                    if(testCycleCaseMapper.updateExecuteStatus(testCycleCaseStepVO.getExecuteId())!=1){
                        throw new CommonException("error.update.cycle.case.status");
                    }
                }

            }
        }
    }

    @Override
    public void baseUpdate(TestCycleCaseStepDTO testCycleCaseStepDTO) {
        if (testCycleCaseStepMapper.updateByPrimaryKeySelective(testCycleCaseStepDTO) != 1) {
            throw new CommonException("error.update.cycle.case.step");
        }
    }

    @Override
    public Page<TestCycleCaseStepVO> queryCaseStep(Long cycleCaseId, Long projectId, PageRequest pageRequest) {
        Page<TestCycleCaseStepDTO> cycleCaseStepDTOPageInfo = PageHelper.doPageAndSort(pageRequest,() ->
                testCycleCaseStepMapper.querListByexecuteId(cycleCaseId));
        Page<TestCycleCaseStepVO> testCycleCaseStepVOList = modelMapper.map(cycleCaseStepDTOPageInfo, Page.class);
        return testCycleCaseStepVOList;
    }

    @Override
    public Page<TestCycleCaseStepVO> querySubStep(Long cycleCaseId, Long projectId, Long organizationId,PageRequest pageRequest) {
        if (cycleCaseId == null) {
            throw new CommonException("error.test.cycle.case.step.caseId.not.null");
        }
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteId(cycleCaseId);
        List<TestCycleCaseStepDTO> testCycleCaseStepDTOS = testCycleCaseStepMapper.queryWithTestCaseStep(testCycleCaseStepDTO, null, null);
        if (testCycleCaseStepDTOS != null && !testCycleCaseStepDTOS.isEmpty()) {
            List<TestCycleCaseStepVO> testCycleCaseStepVOS = new ArrayList<>();
            testCycleCaseStepDTOS.forEach(testCycleCaseStep -> {
                TestCycleCaseStepVO testCycleCaseStepVO = modelMapper.map(testCycleCaseStep, TestCycleCaseStepVO.class);
                testCycleCaseStepVO.setDefects(modelMapper.map(testCycleCaseStep.getDefects(), new TypeToken<List<TestCycleCaseDefectRelVO>>() {
                }.getType()));
                testCycleCaseStepVO.setStepAttachment(modelMapper.map(testCycleCaseStep.getStepAttachment(), new TypeToken<List<TestCycleCaseAttachmentRelVO>>() {
                }.getType()));
                testCycleCaseStepVOS.add(testCycleCaseStepVO);
            });
            testCycleCaseDefectRelService.populateCaseStepDefectInfo(testCycleCaseStepVOS, projectId, organizationId);
            Page<TestCycleCaseStepVO> pageFromList = PageUtil.createPageFromList(testCycleCaseStepVOS, pageRequest);
            return pageFromList;
        } else {
            Page<TestCycleCaseStepVO> emptyPage = new Page<>();
            emptyPage.setContent(new ArrayList<>());
            return emptyPage;
        }
    }

    @Override
    public void snycByCase(TestCycleCaseDTO testCycleCaseDTO, TestCaseDTO testCaseDTO) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        testCycleCaseDTO.setLastUpdatedBy(userDetails.getUserId());
        testCycleCaseDTO.setCreatedBy(userDetails.getUserId());
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteId(testCycleCaseDTO.getExecuteId());
        List<TestCaseStepDTO> testCaseStepDTOS = testCaseStepMapper.listByCaseIds(Arrays.asList(testCaseDTO.getCaseId()));
        Map<Long, TestCaseStepDTO> caseStepMap = testCaseStepDTOS.stream().collect(Collectors.toMap(TestCaseStepDTO::getStepId, Function.identity()));
        List<Long> caseStepIds = testCaseStepDTOS.stream().map(TestCaseStepDTO::getStepId).collect(Collectors.toList());
        List<TestCycleCaseStepDTO> list = testCycleCaseStepMapper.listByexecuteIds(Arrays.asList(testCycleCaseDTO.getExecuteId()));
        List<Long> cycleCaseStepIds = list.stream().map(TestCycleCaseStepDTO::getStepId).collect(Collectors.toList());
        Map<Long, List<Long>> cycleStepIdsMap = list.stream().collect(Collectors.groupingBy(TestCycleCaseStepDTO::getStepId, Collectors.mapping(TestCycleCaseStepDTO::getExecuteStepId, Collectors.toList())));

        // 获取哪些是需要删除的，那些是要增加,那些是要去对比的
        List<Long> needAdd = new ArrayList<>();
        List<Long> needDelete = new ArrayList<>();
        needAdd.addAll(caseStepIds);
        needDelete.addAll(cycleCaseStepIds);
        // 需要新增的
        needAdd.removeAll(cycleCaseStepIds);
        // 需要删除的
        needDelete.removeAll(caseStepIds);
        // 需要比较
        caseStepIds.retainAll(cycleCaseStepIds);

        // 去新增
        List<TestCaseStepDTO> testCaseStepS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(needAdd)) {
            needAdd.forEach(v -> testCaseStepS.add(caseStepMap.get(v)));
            Map<Long, List<TestCaseStepDTO>> caseStepMapToAdd = testCaseStepS.stream().collect(Collectors.groupingBy(TestCaseStepDTO::getIssueId));
            batchInsert(Arrays.asList(testCycleCaseDTO), caseStepMapToAdd);
        }
        // 直接删除的测试执行步骤
        List<Long> needDeleteExecutedStepIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(needDelete)) {
            needDelete.forEach(v -> needDeleteExecutedStepIds.addAll(cycleStepIdsMap.get(v)));
            testCycleCaseStepMapper.batchDeleteTestCycleCaseSteps(needDeleteExecutedStepIds);
            testCycleCaseAttachmentRelMapper.batchDeleteByLinkIdsAndType(needDeleteExecutedStepIds, TestAttachmentCode.ATTACHMENT_CASE_STEP);
            testCycleCaseDefectRelMapper.batchDeleteByLinkIdsAndType(needDeleteExecutedStepIds, TestAttachmentCode.ATTACHMENT_CASE_STEP);
        }
        // 直接比较需要对比更新
        if (!CollectionUtils.isEmpty(caseStepIds)) {
            list.stream().filter(v -> caseStepIds.contains(v.getStepId())).forEach(cycleCaseStep -> {
                TestCaseStepDTO testCaseStepDTO = caseStepMap.get(cycleCaseStep.getStepId());
                String caseStepInfo = String.format("%s,%s,%s,%s", testCaseStepDTO.getExpectedResult(), testCaseStepDTO.getTestData(), testCaseStepDTO.getTestStep(), testCaseStepDTO.getRank());
                String cycleCaseStepInfo = String.format("%s,%s,%s,%s", cycleCaseStep.getExpectedResult(), cycleCaseStep.getTestData(), cycleCaseStep.getTestStep(), cycleCaseStep.getRank());
                if (!Objects.equals(caseStepInfo, cycleCaseStepInfo)) {
                    cycleCaseStep.setTestStep(testCaseStepDTO.getTestStep());
                    cycleCaseStep.setTestData(testCaseStepDTO.getTestData());
                    cycleCaseStep.setRank(testCaseStepDTO.getRank());
                    cycleCaseStep.setExpectedResult(testCaseStepDTO.getExpectedResult());
                    cycleCaseStep.setLastUpdatedBy(userDetails.getUserId());
                    baseUpdate(cycleCaseStep);
                }
            });
        }
    }

    @Override
    public void cloneStep(Map<Long, Long> caseIdMap, List<Long> olderExecuteIds) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Long defaultStatusId = testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE_STEP);
        int count = testCycleCaseStepMapper.countByExecuteIds(olderExecuteIds);
        int ceil = (int) Math.ceil(count / AVG_NUM == 0 ? 0 : count / AVG_NUM);
        for(int page = 0;page < ceil;page++){
            Page<TestCycleCaseStepDTO> stepDTOPageInfo = PageHelper.doPageAndSort(new PageRequest(page, (int) AVG_NUM),() -> testCycleCaseStepMapper.listByexecuteIds(olderExecuteIds));
            List<TestCycleCaseStepDTO> list = stepDTOPageInfo.getContent();
            if (CollectionUtils.isEmpty(list)) {
                return;
            }
            List<Long> stepIds = new ArrayList<>();
            list.forEach(v -> {
                v.setExecuteId(caseIdMap.get(v.getExecuteId()));
                v.setExecuteStepId(null);
                v.setCreatedBy(userDetails.getUserId());
                v.setLastUpdatedBy(userDetails.getUserId());
                v.setCaseId(v.getExecuteStepId());
                v.setStepStatus(defaultStatusId);
                stepIds.add(v.getExecuteStepId());
            });
            testCycleCaseStepMapper.batchInsertTestCycleCaseSteps(list);
            Map<Long,Long> valueMapping = new HashMap<>();
            list.forEach(v -> valueMapping.put(v.getCaseId(),v.getExecuteStepId()));
            //克隆步骤关联的附件
            testCycleCaseAttachmentRelService.cloneAttach(valueMapping,stepIds,"CASE_STEP");
            // 克隆步骤的缺陷
            testCycleCaseDefectRelService.cloneDefect(valueMapping,stepIds,"CASE_STEP");
        }
    }

    @Override
    public void batchInsert(List<TestCycleCaseDTO> testCycleCaseDTOList, Map<Long, List<TestCaseStepDTO>> caseStepMap) {
        if (CollectionUtils.isEmpty(testCycleCaseDTOList)) {
            return;
        }
        Long defaultStatusId = testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE_STEP);
        List<TestCycleCaseStepDTO> list = new ArrayList<>();
        testCycleCaseDTOList.forEach(v -> {
            List<TestCaseStepDTO> testCaseStepDTOS = caseStepMap.get(v.getCaseId());
            if (CollectionUtils.isEmpty(testCaseStepDTOS)) {
                return;
            }
            testCaseStepDTOS.forEach(testCaseStepDTO -> {
                TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO(v.getExecuteId(), testCaseStepDTO.getStepId()
                        , v.getCreatedBy(), v.getLastUpdatedBy(), testCaseStepDTO.getTestStep(), testCaseStepDTO.getTestData(), testCaseStepDTO.getExpectedResult());
                testCycleCaseStepDTO.setRank(testCaseStepDTO.getRank());
                testCycleCaseStepDTO.setStepStatus(defaultStatusId);
                list.add(testCycleCaseStepDTO);
            });
        });
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        testCycleCaseStepMapper.batchInsertTestCycleCaseSteps(list);
    }

    @Override
    public void delete(Long executeStepId) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteStepId(executeStepId);
        if (testCycleCaseStepMapper.delete(testCycleCaseStepDTO) != 1) {
            throw new CommonException("error.delete.cycle.step");
        }
    }

    @Override
    public void create(TestCycleCaseStepVO testCycleCaseStepVO) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = modelMapper.map(testCycleCaseStepVO, TestCycleCaseStepDTO.class);
        Long defaultStatusId = testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE_STEP);
        testCycleCaseStepDTO.setStepStatus(defaultStatusId);
        if (testCycleCaseStepMapper.insert(testCycleCaseStepDTO) != 1) {
            throw new CommonException("error.insert.cycle.step");
        }
    }

}
