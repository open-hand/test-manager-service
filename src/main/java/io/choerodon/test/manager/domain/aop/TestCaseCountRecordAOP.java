package io.choerodon.test.manager.domain.aop;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseHistoryEFactory;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Created by 842767365@qq.com on 8/20/18.
 */
@Aspect
@Component
public class TestCaseCountRecordAOP {

	@Autowired
	ITestCycleCaseService testCycleCaseService;

	@Autowired
	TestStatusService testStatusService;

	@Autowired
	RedisTemplate redisTemplate;

	private static final String FIELD_STATUS = "执行状态";

	private Log log = LogFactory.getLog(this.getClass());

	@Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.changeOneCase(..)) && args(testCycleCaseDTO,projectId)")
	public Object updateTestCase(ProceedingJoinPoint pjp, TestCycleCaseDTO testCycleCaseDTO, Long projectId) throws Throwable {
		TestCycleCaseE case1 = TestCycleCaseEFactory.create();
		case1.setExecuteId(testCycleCaseDTO.getExecuteId());
		TestCycleCaseE before = testCycleCaseService.queryOne(case1);
		TestCycleCaseDTO beforeCeaseDTO = ConvertHelper.convert(before, TestCycleCaseDTO.class);
		testStatusService.populateStatus(beforeCeaseDTO);
		Object o = pjp.proceed();

		if (!testCycleCaseDTO.getExecutionStatus().equals(before.getExecutionStatus())) {
			LocalDateTime time = LocalDateTime.ofInstant(((TestCycleCaseDTO) o).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
			countCaseToRedis(String.valueOf(projectId), time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), beforeCeaseDTO.getExecutionStatusName(), testCycleCaseDTO.getExecutionStatusName(), before.getExecuteId());
		}
		return o;
	}

	@Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.delete(..)) && args(cycleCaseId,projectId)")
	public Object deleteTestCase(ProceedingJoinPoint pjp, Long cycleCaseId, Long projectId) throws Throwable {
		TestCycleCaseE cycleCaseE = TestCycleCaseEFactory.create();
		cycleCaseE.setExecuteId(cycleCaseId);
		TestCycleCaseE oldCase=cycleCaseE.queryOne();
		Object o = pjp.proceed();
		countCaseToRedis(oldCase, projectId);
		return o;

	}


	private void countCaseToRedis(TestCycleCaseE testCycleCaseE, Long projectId) {
		if (!testCycleCaseE.getExecutionStatus().equals(testStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE))) {
			doDecrementRedis(testCycleCaseE.getExecuteId(), String.valueOf(projectId));
		}
	}


	private void countCaseToRedis(String projectId, String date, String oldStatus, String newStatus, Long executeId) {
		if (StringUtils.equals(oldStatus, TestStatusE.STATUS_UN_EXECUTED)) {
			String key = "summary:" + projectId + ":" + date;
			RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
			entityIdCounter.incrementAndGet();
			if (log.isDebugEnabled()) {
				log.debug("测试执行记录统计状态切面：执行Id:" + executeId + "计数+1, key:" + key);
			}
		} else if (StringUtils.equals(newStatus, TestStatusE.STATUS_UN_EXECUTED)) {
			doDecrementRedis(executeId, projectId);
		}
	}

	private void doDecrementRedis(Long executeId, String projectId) {
		TestCycleCaseHistoryE e = TestCycleCaseHistoryEFactory.create();
		e.setExecuteId(executeId);
		e.setOldValue(TestStatusE.STATUS_UN_EXECUTED);
		e.setField(FIELD_STATUS);
		PageRequest pageRequest = new PageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(1);
		pageRequest.setSort(new Sort(Sort.Direction.DESC, "id"));
		LocalDateTime time = LocalDateTime.ofInstant(e.querySelf(pageRequest).get(0).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
		String key = time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		RedisAtomicLong entityIdCounter = new RedisAtomicLong("summary:" + projectId + ":" + key, redisTemplate.getConnectionFactory());
		entityIdCounter.decrementAndGet();


		if (log.isDebugEnabled()) {
			log.debug("测试执行记录统计状态切面:执行ID：" + executeId + "计数-1, key:" + key);
		}
	}

}
