package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.ExecutionCaseStatusChangeSettingVO;
import io.choerodon.test.manager.api.vo.ExecutionUpdateIssueVO;
import io.choerodon.test.manager.api.vo.TestStatusVO;
import io.choerodon.test.manager.api.vo.agile.ProjectCategoryDTO;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.app.service.ExecutionCaseStatusChangeSettingService;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.feign.operator.AgileClientOperator;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.ConvertUtils;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhaotianxin
 * @date 2021-05-10 17:01
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class ExecutionCaseStatusChangeSettingServiceImpl implements ExecutionCaseStatusChangeSettingService {

    @Autowired
    private ExecutionStatusChangeSettingMapper executionStatusChangeSettingMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TestStatusService testStatusService;
    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;
    @Autowired
    private TestCycleMapper testCycleMapper;
    @Autowired
    private TestCaseLinkMapper testCaseLinkMapper;
    @Autowired
    private AgileClientOperator agileClientOperator;
    @Autowired
    private TestPlanMapper testPlanMapper;
    @Autowired
    private BaseFeignClient baseFeignClient;

    @Override
    public void save(Long projectId, Long organizationId, ExecutionCaseStatusChangeSettingVO executionCaseStatusChangeSettingVO) {
        if (ObjectUtils.isEmpty(executionCaseStatusChangeSettingVO.getAgileIssueTypeId())) {
            throw new CommonException("error.executionStatusChange.agileIssueTypeId.null");
        }
        if (ObjectUtils.isEmpty(executionCaseStatusChangeSettingVO.getAgileStatusId())) {
            throw new CommonException("error.executionStatusChange.agileStatusId.null");
        }
        ExecutionCaseStatusChangeSettingDTO statusChangeSettingDTO = modelMapper.map(executionCaseStatusChangeSettingVO, ExecutionCaseStatusChangeSettingDTO.class);
        statusChangeSettingDTO.setProjectId(projectId);
        statusChangeSettingDTO.setOrganizationId(organizationId);
        Long issueStatusId = statusChangeSettingDTO.getAgileStatusId();
        Long issueTypeId = statusChangeSettingDTO.getAgileIssueTypeId();
        ExecutionCaseStatusChangeSettingDTO changeSettingDTO = new ExecutionCaseStatusChangeSettingDTO(issueTypeId, issueStatusId, projectId, organizationId);
        ExecutionCaseStatusChangeSettingDTO executionCaseStatusChangeSettingDTO = executionStatusChangeSettingMapper.selectOne(changeSettingDTO);
        if (!ObjectUtils.isEmpty(executionCaseStatusChangeSettingDTO)) {
            executionStatusChangeSettingMapper.deleteByPrimaryKey(executionCaseStatusChangeSettingDTO.getId());
        }
        if (!ObjectUtils.isEmpty(statusChangeSettingDTO.getTestStatusId())) {
           baseInsert(statusChangeSettingDTO);
        }
    }

    private void baseInsert(ExecutionCaseStatusChangeSettingDTO statusChangeSettingDTO) {
        if (executionStatusChangeSettingMapper.insertSelective(statusChangeSettingDTO) != 1) {
            throw new CommonException("error.executionStatusChange.insert");
        }
    }

    @Override
    public List<ExecutionCaseStatusChangeSettingVO> listByIssueStatusIds(Long projectId, Long organizationId, Long issueTypeId, List<Long> statusIds) {
        List<ExecutionCaseStatusChangeSettingDTO> executionCaseStatusChangeSettingDTOS = executionStatusChangeSettingMapper.selectByCondition(Condition.builder(ExecutionCaseStatusChangeSettingDTO.class)
                .andWhere(Sqls.custom().andEqualTo("projectId", projectId).andEqualTo("organizationId", organizationId)
                        .andEqualTo("issueTypeId", issueTypeId).andIn("issueStatusId", statusIds)).build());
        if (CollectionUtils.isEmpty(executionCaseStatusChangeSettingDTOS)) {
            return new ArrayList<>();
        }
        List<ExecutionCaseStatusChangeSettingVO> list = new ArrayList<>();
        TestStatusVO statusVO = new TestStatusVO();
        statusVO.setStatusType("CYCLE_CASE");
        List<TestStatusVO> testStatusVOS = testStatusService.query(projectId, statusVO);
        if (CollectionUtils.isEmpty(testStatusVOS)) {
            return new ArrayList<>();
        }
        Map<Long, TestStatusVO> statusVOMap = testStatusVOS.stream().collect(Collectors.toMap(TestStatusVO::getStatusId, Function.identity()));
        executionCaseStatusChangeSettingDTOS.forEach(v -> {
            TestStatusVO testStatusVO = statusVOMap.get(v.getTestStatusId());
            if (ObjectUtils.isEmpty(testStatusVO)) {
                return;
            }
            ExecutionCaseStatusChangeSettingVO executionCaseStatusChangeSettingVO = modelMapper.map(v, ExecutionCaseStatusChangeSettingVO.class);
            executionCaseStatusChangeSettingVO.setTestStatusVO(testStatusVO);
            list.add(executionCaseStatusChangeSettingVO);
        });
        return list;
    }
    

    @Override
    @Async
    public void updateExecutionStatus(Long projectId, Long cycleCaseId, Long testStatusId) {
        Boolean result = checkAgileModule(projectId);
        if(Boolean.FALSE.equals(result)){
            return;
        }
        TestCycleCaseDTO cycleCaseDTO = testCycleCaseMapper.selectByPrimaryKey(cycleCaseId);
        // 获取冲刺Id
        TestCycleDTO testCycleDTO = testCycleMapper.selectByPrimaryKey(cycleCaseDTO.getCycleId());
        if (ObjectUtils.isEmpty(testCycleDTO)) {
            return;
        }
        TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(testCycleDTO.getPlanId());
        Long sprintId = testPlanDTO.getSprintId();
        if (ObjectUtils.isEmpty(sprintId)) {
            return;
        }
        // 用例关联的issue
        Long caseId = cycleCaseDTO.getCaseId();
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        testCaseLinkDTO.setLinkCaseId(caseId);
        testCaseLinkDTO.setProjectId(projectId);
        List<TestCaseLinkDTO> caseLinkList = testCaseLinkMapper.select(testCaseLinkDTO);
        if (CollectionUtils.isEmpty(caseLinkList)) {
            return;
        }
        Set<Long> issueIds = caseLinkList.stream().map(TestCaseLinkDTO::getIssueId).collect(Collectors.toSet());

        ExecutionCaseStatusChangeSettingDTO statusChangeSettingDTO = new ExecutionCaseStatusChangeSettingDTO(testStatusId, projectId, ConvertUtils.getOrganizationId(projectId));
        List<ExecutionCaseStatusChangeSettingDTO> statusChangeSettingDTOS = executionStatusChangeSettingMapper.select(statusChangeSettingDTO);
        if (CollectionUtils.isEmpty(statusChangeSettingDTOS)) {
            return;
        }
        Map<Long, Long> map = statusChangeSettingDTOS.stream().collect(Collectors.toMap(ExecutionCaseStatusChangeSettingDTO::getAgileIssueTypeId, ExecutionCaseStatusChangeSettingDTO::getAgileStatusId));
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        ExecutionUpdateIssueVO executionUpdateIssueVO = new ExecutionUpdateIssueVO();
        executionUpdateIssueVO.setIssueTypeStatusMap(map);
        executionUpdateIssueVO.setSprintId(sprintId);
        issueIds.forEach(v -> agileClientOperator.executionUpdateStatus(projectId, v, executionUpdateIssueVO));
    }

    private Boolean checkAgileModule(Long projectId) {
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        List<ProjectCategoryDTO> categories = projectDTO.getCategories();
        if(CollectionUtils.isEmpty(categories)){
            return false;
        }
        List<String> codes = categories.stream().map(ProjectCategoryDTO::getCode).collect(Collectors.toList());
        return codes.contains("N_AGILE");
    }

    @Override
    public ExecutionCaseStatusChangeSettingVO queryByOption(Long projectId, Long organizationId, Long issueTypeId, Long statusId) {
        ExecutionCaseStatusChangeSettingDTO executionCaseStatusChangeSettingDTO = new ExecutionCaseStatusChangeSettingDTO(issueTypeId, statusId, projectId, organizationId);
        ExecutionCaseStatusChangeSettingDTO statusChangeSettingDTO = executionStatusChangeSettingMapper.selectOne(executionCaseStatusChangeSettingDTO);
        if (ObjectUtils.isEmpty(statusChangeSettingDTO)) {
            return null;
        }
        TestStatusVO statusVO = new TestStatusVO();
        statusVO.setStatusType("CYCLE_CASE");
        List<TestStatusVO> testStatusVOS = testStatusService.query(projectId, statusVO);
        if (CollectionUtils.isEmpty(testStatusVOS)) {
            return null;
        }
        Map<Long, TestStatusVO> statusVOMap = testStatusVOS.stream().collect(Collectors.toMap(TestStatusVO::getStatusId, Function.identity()));
        TestStatusVO testStatusVO = statusVOMap.get(statusChangeSettingDTO.getTestStatusId());
        if (ObjectUtils.isEmpty(testStatusVO)) {
            return null;
        }
        ExecutionCaseStatusChangeSettingVO executionCaseStatusChangeSettingVO = modelMapper.map(statusChangeSettingDTO, ExecutionCaseStatusChangeSettingVO.class);
        executionCaseStatusChangeSettingVO.setTestStatusVO(testStatusVO);
        return executionCaseStatusChangeSettingVO;
    }
}
