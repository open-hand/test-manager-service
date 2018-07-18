package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Sets;
import io.choerodon.agile.api.dto.IssueCommonDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseServiceImpl implements TestCycleCaseService {
    @Autowired
    ITestCycleCaseService iTestCycleCaseService;

	@Autowired
	ITestCycleService iTestCycleService;

	@Autowired
	TestCaseFeignClient testCaseFeignClient;

	@Autowired
	TestCaseService testCaseService;

	@Autowired
	ITestCycleCaseDefectRelService testCycleCaseDefectRelService;

	@Autowired
	UserService userService;


	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(Long cycleCaseId) {
		TestCycleCaseDTO dto = new TestCycleCaseDTO();
		dto.setExecuteId(cycleCaseId);
		iTestCycleCaseService.delete(ConvertHelper.convert(dto, TestCycleCaseE.class));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void batchDelete(TestCycleCaseDTO testCycleCaseDTO) {
		iTestCycleCaseService.delete(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class));
	}

    @Override
	public Page<TestCycleCaseDTO> query(TestCycleCaseDTO testCycleCaseDTO, PageRequest pageRequest, Long projectId) {
        Page<TestCycleCaseE> serviceEPage = iTestCycleCaseService.query(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), pageRequest);
		Page<TestCycleCaseDTO> dto = ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseDTO.class);
		setDefects(dto, projectId);
		return dto;
    }

    @Override
	public Page<TestCycleCaseDTO> queryByCycle(Long cycleId, PageRequest pageRequest, Long projectId) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setCycleId(cycleId);
		Page<TestCycleCaseE> serviceEPage = iTestCycleCaseService.query(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), pageRequest);
		Page<TestCycleCaseDTO> dots = ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseDTO.class);
		setDefects(dots, projectId);
		populateUsers(dots);
		populateIssue(dots, projectId);
		return dots;
    }

	@Override
	public void populateIssue(List<TestCycleCaseDTO> dots, Long projectId) {
		Long[] ids = dots.stream().map(v -> v.getIssueId()).toArray(Long[]::new);
		Map<Long, IssueInfosDTO> maps = testCaseService.getIssueInfoMap(projectId, ids);
		dots.forEach(v -> v.setIssueInfosDTO(maps.get(v.getIssueId())));
	}

	@Override
	public Page<TestCycleCaseDTO> queryByCycleWithFilterArgs(Long cycleId, PageRequest pageRequest, Long projectId, TestCycleCaseDTO searchDTO) {
		searchDTO = Optional.ofNullable(searchDTO).orElseGet(() -> new TestCycleCaseDTO());
		searchDTO.setCycleId(cycleId);
		Page<TestCycleCaseE> serviceEPage = iTestCycleCaseService.query(ConvertHelper.convert(searchDTO, TestCycleCaseE.class), pageRequest);
		Page<TestCycleCaseDTO> dots = ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseDTO.class);

		populateUsers(dots);
		setDefects(dots, projectId);
		return dots;
	}

	@Override
	public List<TestCycleCaseDTO> queryByIssuse(Long issuseId, Long projectId) {
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setIssueId(issuseId);
		List<TestCycleCaseDTO> dto = ConvertHelper.convertList(iTestCycleCaseService.queryByIssue(issuseId), TestCycleCaseDTO.class);
		setDefects(dto, projectId);
		IssueInfosDTO info = new IssueInfosDTO(testCaseService.queryIssue(projectId, issuseId).getBody());
		dto.forEach(v -> v.setIssueInfosDTO(info));
		populateUsers(dto);
		return dto;
	}

	@Override
	public TestCycleCaseDTO queryOne(Long cycleCaseId, Long projectId) {
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setExecuteId(cycleCaseId);
		TestCycleCaseDTO dto = ConvertHelper.convert(iTestCycleCaseService.queryOne(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
		setDefect(dto, projectId);
		return setUser(dto);
	}

	private TestCycleCaseDTO setUser(TestCycleCaseDTO dto) {
		boolean assigned = false;
		boolean lastAssigned = false;
		if (!(dto.getAssignedTo() == null || dto.getAssignedTo().longValue() == 0)) {
			assigned = true;
		}
		if (!(dto.getLastUpdatedBy() == null || dto.getLastUpdatedBy().longValue() == 0)) {
			lastAssigned = true;
		}
		Long assign = dto.getAssignedTo();
		Long update = dto.getLastUpdatedBy();
		Map<Long, UserDO> lists = userService.query(new Long[]{assign, update});

		if (assigned) {
			UserDO u = lists.get(assign);
			dto.setReporterRealName(u.getRealName());
			dto.setReporterJobNumber(u.getLoginName());
		}
		if (lastAssigned) {
			UserDO u = lists.get(update);
			dto.setAssignedUserRealName(u.getRealName());
			dto.setAssignedUserJobNumber(u.getLoginName());
		}
		return dto;
	}

	private void populateUsers(List<TestCycleCaseDTO> users) {
		List<Long> usersId = new ArrayList<>();
		users.stream().forEach(v -> {
			usersId.add(v.getAssignedTo());
			usersId.add(v.getLastUpdatedBy());
		});
		usersId.stream().filter(v -> !v.equals(new Long(0))).collect(Collectors.toList());
		if (usersId.size() != 0) {
			Map<Long, UserDO> userMaps = userService.query(usersId.toArray(new Long[usersId.size()]));
			users.forEach(v -> {
				Optional.ofNullable(userMaps.get(v.getAssignedTo())).ifPresent(u -> {
					v.setReporterRealName(u.getRealName());
					v.setReporterJobNumber(u.getLoginName());
				});
				Optional.ofNullable(userMaps.get(v.getLastUpdatedBy())).ifPresent(u -> {
					v.setAssignedUserRealName(u.getRealName());
					v.setAssignedUserJobNumber(u.getLoginName());
				});

			});
		}
	}

	private void setDefects(List<TestCycleCaseDTO> testCycleCase, Long projectId) {
		testCycleCase.forEach(v -> {
			v.setDefects(testCycleCaseDefectRelService.query(v.getExecuteId(), TestCycleCaseDefectRelE.CYCLE_CASE, projectId));
		});
	}

	private void setDefect(TestCycleCaseDTO testCycleCase, Long projectId) {
		testCycleCase.setDefects(testCycleCaseDefectRelService.query(testCycleCase.getExecuteId(), TestCycleCaseDefectRelE.CYCLE_CASE, projectId));

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
		if (testCycleCaseDTO.getCycleId() == null) {
			testCycleCaseDTO.setCycleId(iTestCycleService.findDefaultCycle(projectId));
		}
		TestStatusE e = TestStatusEFactory.create();
		testCycleCaseDTO.setExecutionStatus(e.getDefaultStatusId(projectId, TestStatusE.STATUS_TYPE_CASE));
		testCycleCaseDTO.setLastRank(TestCycleCaseEFactory.create().getLastedRank(testCycleCaseDTO.getCycleId()));
		iTestCycleCaseService.validateCycleCaseInCycle(testCycleCaseDTO.getCycleId(), testCycleCaseDTO.getIssueId());
		return ConvertHelper.convert(iTestCycleCaseService.runTestCycleCase(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), projectId), TestCycleCaseDTO.class);
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseDTO changeOneCase(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
		return setUser(ConvertHelper.convert(iTestCycleCaseService.changeStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class));
	}


	@Override
	public List<Long> getActiveCase(Long range, Long projectId, String day) {
		return iTestCycleCaseService.getActiveCase(range, projectId, day);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public boolean createFilteredCycleCaseInCycle(Long projectId, Long fromCycleId, Long toCycleId, Long assignee, SearchDTO searchDTO) {
		TestCycleCaseE testCycleCaseE = new TestCycleCaseEFactory().create();
		testCycleCaseE.setCycleId(fromCycleId);
		Map filterMap = new HashMap();
		Optional.ofNullable(searchDTO.getExecutionStatus()).ifPresent(v -> filterMap.put("executionStatus", v));

		List<TestCycleCaseE> testCycleCaseES = testCycleCaseE.filter(filterMap);
		ResponseEntity<Page<IssueCommonDTO>> responseEntity = testCaseFeignClient.listIssueWithoutSubToTestComponent(projectId, searchDTO, 0, 400, null);
		Set issueListDTOS = responseEntity.getBody().stream().map(v -> v.getIssueId().longValue()).collect(Collectors.toSet());

		Long defaultStatus = TestStatusEFactory.create().getDefaultStatusId(projectId, TestStatusE.STATUS_TYPE_CASE);
		final String[] lastRank = new String[1];
		lastRank[0] = testCycleCaseE.getLastedRank(testCycleCaseE.getCycleId());
		Long[] defectStatus = searchDTO.getDefectStatus();
		Set defectSets = Sets.newHashSet();
		if (defectStatus != null && defectStatus.length != 0) {
			for (Long de : defectStatus) {
				defectSets.add(de.longValue());
			}
		}
		testCycleCaseES.stream().filter(v -> issueListDTOS.contains(v.getIssueId().longValue()) && containsDefect(defectSets, v.getDefects()))
				.forEach(u -> {
					u.setExecuteId(null);
					u.setRank(RankUtil.Operation.INSERT.getRank(lastRank[0], null));
					u.setAssignedTo(assignee);
					u.setCycleId(toCycleId);
					u.setExecutionStatus(defaultStatus);
					u.setObjectVersionNumber(new Long(0));
					lastRank[0] = iTestCycleCaseService.cloneCycleCase(u, projectId).getRank();
				});

		return true;
	}

	private boolean containsDefect(Set defectSet, List<TestCycleCaseDefectRelE> defects) {
		if (defects.isEmpty()) {
			return true;
		}
		for (TestCycleCaseDefectRelE v : defects) {
			if (defectSet.contains(v.getIssueId().longValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Long countCaseNotRun(Long projectId) {
		return iTestCycleCaseService.countCaseNotRun(projectId);
	}

	@Override
	public Long countCaseNotPlain(Long projectId) {
		return iTestCycleCaseService.countCaseNotPlain(projectId);
	}

	@Override
	public Long countCaseSum(Long projectId) {
		return iTestCycleCaseService.countCaseSum(projectId);
	}


}
