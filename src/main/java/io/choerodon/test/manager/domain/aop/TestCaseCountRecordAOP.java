package io.choerodon.test.manager.domain.aop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

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
import org.springframework.util.ObjectUtils;
import com.github.pagehelper.PageInfo;

import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseHistoryEFactory;
import io.choerodon.test.manager.infra.common.utils.RedisTemplateUtil;
import io.choerodon.base.domain.Sort;
import io.choerodon.core.convertor.ConvertHelper;

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

    @Autowired
    RedisTemplateUtil redisTemplateUtil;

    private static final String FIELD_STATUS = "执行状态";

    private static final String DATE_FORMATTER = "yyyy-MM-dd";
    private static final String REDIS_COUNT_KEY = "summary:";

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
            countCaseToRedis(String.valueOf(projectId), time.format(DateTimeFormatter.ofPattern(DATE_FORMATTER)), beforeCeaseDTO.getExecutionStatusName(), testCycleCaseDTO.getExecutionStatusName(), before.getExecuteId(),
                    LocalDateTime.ofInstant(before.getCreationDate().toInstant(), ZoneId.systemDefault()));
        }
        return o;
    }

    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.create(..)) && args(testCycleCaseDTO,projectId)")
    public Object createTestCase(ProceedingJoinPoint pjp, TestCycleCaseDTO testCycleCaseDTO, Long projectId) throws Throwable {
        Object o = pjp.proceed();
        if (!ObjectUtils.isEmpty(testCycleCaseDTO.getExecutionStatus()) && !testCycleCaseDTO.getExecutionStatus().equals(testStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE))) {
            LocalDateTime time = LocalDateTime.ofInstant(((TestCycleCaseDTO) o).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
            countCaseToRedis(String.valueOf(projectId), time.format(DateTimeFormatter.ofPattern(DATE_FORMATTER)), TestStatusE.STATUS_UN_EXECUTED, null, null,
                    null);
        }
        return o;
    }


    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.batchCreateForAutoTest(..)) && args(testCycleCaseDTO,projectId)")
    public Object batchCreateForAutoTest(ProceedingJoinPoint pjp, List<TestCycleCaseDTO> testCycleCaseDTO, Long projectId) throws Throwable {
        Object o = pjp.proceed();
        String key = REDIS_COUNT_KEY + projectId + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
        RedisAtomicLong entityIdCounter = redisTemplateUtil.getRedisAtomicLong(key, redisTemplate);
        entityIdCounter.addAndGet(testCycleCaseDTO.size());
        return o;
    }


    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.delete(..)) && args(cycleCaseId,projectId)")
    public Object deleteTestCase(ProceedingJoinPoint pjp, Long cycleCaseId, Long projectId) throws Throwable {
        TestCycleCaseE cycleCaseE = TestCycleCaseEFactory.create();
        cycleCaseE.setExecuteId(cycleCaseId);
        TestCycleCaseE oldCase = cycleCaseE.queryOne();
        Object o = pjp.proceed();
        countCaseToRedis(oldCase, projectId);
        return o;

    }


    public void countCaseToRedis(TestCycleCaseE testCycleCaseE, Long projectId) {
        if (!testCycleCaseE.getExecutionStatus().equals(testStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE))) {
            doDecrementRedis(testCycleCaseE.getExecuteId(), String.valueOf(projectId), LocalDateTime.ofInstant(testCycleCaseE.getCreationDate().toInstant(), ZoneId.systemDefault()));
        }
    }


    public void countCaseToRedis(String projectId, String date, String oldStatus, String newStatus, Long executeId, LocalDateTime oldCreateTime) {
        if (StringUtils.equals(oldStatus, TestStatusE.STATUS_UN_EXECUTED)) {
            String key = REDIS_COUNT_KEY + projectId + ":" + date;
            RedisAtomicLong entityIdCounter = redisTemplateUtil.getRedisAtomicLong(key, redisTemplate);
            entityIdCounter.incrementAndGet();
            if (log.isDebugEnabled()) {
                log.debug("测试执行记录统计状态切面：执行Id:" + executeId + "计数+1, key:" + key);
            }
        } else if (StringUtils.equals(newStatus, TestStatusE.STATUS_UN_EXECUTED)) {
            doDecrementRedis(executeId, projectId, oldCreateTime);
        }
    }

    private void doDecrementRedis(Long executeId, String projectId, LocalDateTime time) {
        TestCycleCaseHistoryE e = TestCycleCaseHistoryEFactory.create();
        e.setExecuteId(executeId);
        e.setOldValue(TestStatusE.STATUS_UN_EXECUTED);
        e.setField(FIELD_STATUS);
        PageRequest pageRequest = new PageRequest(1, 1);
        pageRequest.setSort(new Sort(Sort.Direction.DESC, "id"));
        PageInfo<TestCycleCaseHistoryE> page = e.querySelf(pageRequest);
        if (page != null && !page.getList().isEmpty()) {
            time = LocalDateTime.ofInstant(page.getList().get(0).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
        }
        String key = REDIS_COUNT_KEY + projectId + ":" + time.format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
        RedisAtomicLong entityIdCounter = redisTemplateUtil.getRedisAtomicLong(key, redisTemplate);
        entityIdCounter.decrementAndGet();

        if (log.isDebugEnabled()) {
            log.debug("测试执行记录统计状态切面:执行ID：" + executeId + "计数-1, key:" + key);
        }
    }

}
