package io.choerodon.test.manager.domain.aop;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by jialongZuo@hand-china.com on 6/28/18.
 */
@Aspect
@Component
public class TestCycleCaseHistoryRecordAOP {

	@Autowired
	TestCycleCaseHistoryService testCycleCaseHistoryService;

	@Autowired
	TestCycleCaseService testCycleCaseService;

	@Pointcut("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.changeOneCase(..)) && args(testCycleCaseDTO)")
	public void recordHistory(TestCycleCaseDTO testCycleCaseDTO) {
	}

	@Around("recordHistory(testCycleCaseDTO)")
	public void afterTest(ProceedingJoinPoint pjp, TestCycleCaseDTO testCycleCaseDTO) throws Throwable {
		TestCycleCaseDTO before = testCycleCaseService.queryOne(testCycleCaseDTO.getExecuteId());
		pjp.proceed();
		if (!testCycleCaseDTO.getExecutionStatus().equals(before.getExecutionStatus())) {
			TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
			historyDTO.setExecuteId(before.getExecuteId());
			historyDTO.setNewValue(testCycleCaseDTO.getExecutionStatus());
			historyDTO.setOldValue(before.getExecutionStatus());
			testCycleCaseHistoryService.insert(historyDTO);
		}
	}
}
