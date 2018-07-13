package io.choerodon.test.manager.app.service.impl;

import io.choerodon.agile.api.dto.IssueCommonDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestStatusDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.UserService;
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
		dots.forEach(v -> {
			setUser(v);
			setDefect(v, projectId);
		});
		return dots;
    }

	@Override
	public List<TestCycleCaseDTO> queryByIssuse(Long issuseId, Long projectId) {
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setIssueId(issuseId);
		List<TestCycleCaseDTO> dto = ConvertHelper.convertList(iTestCycleCaseService.query(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
		setDefects(dto, projectId);
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

	private void setDefects(List<TestCycleCaseDTO> testCycleCase, Long projectId) {
		testCycleCase.forEach(v -> {
			v.setDefects(testCycleCaseDefectRelService.query(v.getCycleId(), TestCycleCaseDefectRelE.CYCLE_CASE, projectId));
		});
	}

	private void setDefect(TestCycleCaseDTO testCycleCase, Long projectId) {
		testCycleCase.setDefects(testCycleCaseDefectRelService.query(testCycleCase.getCycleId(), TestCycleCaseDefectRelE.CYCLE_CASE, projectId));

	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
		if (testCycleCaseDTO.getCycleId() == null) {
			testCycleCaseDTO.setCycleId(iTestCycleService.findDefaultCycle(projectId));
		}
		TestStatusE e = new TestStatusE();
		testCycleCaseDTO.setExecutionStatus(e.getDefaultStatusId(projectId, TestStatusE.STATUS_TYPE_CASE));

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
		testCycleCaseES.stream().filter(v -> issueListDTOS.contains(v.getIssueId().longValue()))
				.forEach(u -> {
					u.setExecuteId(null);
					u.setAssignedTo(assignee);
					u.setCycleId(toCycleId);
					u.setExecutionStatus(defaultStatus);
					u.setObjectVersionNumber(new Long(0));
					iTestCycleCaseService.cloneCycleCase(u, projectId);
				});

		return true;
	}
}
