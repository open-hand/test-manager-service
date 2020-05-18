package io.choerodon.test.manager.infra.aspect;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.core.domain.Page;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.infra.enums.TestStatusType;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseHistoryMapper;
import io.choerodon.test.manager.infra.util.PageUtil;
import io.choerodon.test.manager.infra.util.RedisTemplateUtil;
import io.choerodon.mybatis.pagehelper.domain.Sort;

/**
 * Created by 842767365@qq.com on 8/20/18.
 */
@Aspect
@Component
public class TestCaseCountRecordAspect {

    @Autowired
    private TestStatusService testStatusService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    TestCycleCaseHistoryMapper testCycleCaseHistoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisTemplateUtil redisTemplateUtil;

    @Autowired
    private ModelMapper modelMapper;

    private static final String FIELD_STATUS = "执行状态";

    private static final String DATE_FORMATTER = "yyyy-MM-dd";
    private static final String REDIS_COUNT_KEY = "summary:";

    private Log log = LogFactory.getLog(this.getClass());

    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.changeOneCase(..)) && args(testCycleCaseDTO,projectId)")
    public Object updateTestCase(ProceedingJoinPoint pjp, TestCycleCaseDTO testCycleCaseDTO, Long projectId) throws Throwable {
        TestCycleCaseDTO case1 = new TestCycleCaseDTO();
        case1.setExecuteId(testCycleCaseDTO.getExecuteId());
        TestCycleCaseDTO before = testCycleCaseService.queryWithAttachAndDefect(case1, new PageRequest(1, 1)).get(0);
        TestCycleCaseVO beforeCeaseDTO = modelMapper.map(before, TestCycleCaseVO.class);
        testStatusService.populateStatus(beforeCeaseDTO);
        Object o = pjp.proceed();

        if (!testCycleCaseDTO.getExecutionStatus().equals(before.getExecutionStatus())) {
            LocalDateTime time = LocalDateTime.ofInstant(((TestCycleCaseDTO) o).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
            countCaseToRedis(String.valueOf(projectId), time.format(DateTimeFormatter.ofPattern(DATE_FORMATTER)), beforeCeaseDTO.getExecutionStatusName(), testCycleCaseDTO.getExecutionStatusName(), before.getExecuteId(),
                    LocalDateTime.ofInstant(before.getCreationDate().toInstant(), ZoneId.systemDefault()));
        }
        return o;
    }


    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.create(..)) && args(testCycleCaseVO,projectId)")
    public Object createTestCase(ProceedingJoinPoint pjp, TestCycleCaseVO testCycleCaseVO, Long projectId) throws Throwable {
        Object o = pjp.proceed();
        if (!ObjectUtils.isEmpty(testCycleCaseVO.getExecutionStatus()) && !testCycleCaseVO.getExecutionStatus().equals(testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE))) {
            LocalDateTime time = LocalDateTime.ofInstant(((TestCycleCaseVO) o).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
            countCaseToRedis(String.valueOf(projectId), time.format(DateTimeFormatter.ofPattern(DATE_FORMATTER)), TestStatusType.STATUS_UN_EXECUTED, null, null,
                    null);
        }
        return o;
    }


    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.batchCreateForAutoTest(..)) && args(testCycleCaseVO,projectId)")
    public Object batchCreateForAutoTest(ProceedingJoinPoint pjp, List<TestCycleCaseVO> testCycleCaseVO, Long projectId) throws Throwable {
        Object o = pjp.proceed();
        String key = REDIS_COUNT_KEY + projectId + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
        RedisAtomicLong entityIdCounter = redisTemplateUtil.getRedisAtomicLong(key, redisTemplate);
        entityIdCounter.addAndGet(testCycleCaseVO.size());
        return o;
    }


    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.delete(..)) && args(cycleCaseId,projectId)")
    public Object deleteTestCase(ProceedingJoinPoint pjp, Long cycleCaseId, Long projectId) throws Throwable {
        TestCycleCaseDTO cycleCaseE = new TestCycleCaseDTO();
        cycleCaseE.setExecuteId(cycleCaseId);
        TestCycleCaseDTO oldCase = testCycleCaseService.queryWithAttachAndDefect(cycleCaseE, new PageRequest(1, 1)).get(0);
        Object o = pjp.proceed();
        countCaseToRedis(oldCase, projectId);
        return o;

    }


    public void countCaseToRedis(TestCycleCaseDTO testCycleCaseE, Long projectId) {
        if (!testCycleCaseE.getExecutionStatus().equals(testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE))) {
            doDecrementRedis(testCycleCaseE.getExecuteId(), String.valueOf(projectId), LocalDateTime.ofInstant(testCycleCaseE.getCreationDate().toInstant(), ZoneId.systemDefault()));
        }
    }


    public void countCaseToRedis(String projectId, String date, String oldStatus, String newStatus, Long executeId, LocalDateTime oldCreateTime) {
        if (StringUtils.equals(oldStatus, TestStatusType.STATUS_UN_EXECUTED)) {
            String key = REDIS_COUNT_KEY + projectId + ":" + date;
            RedisAtomicLong entityIdCounter = redisTemplateUtil.getRedisAtomicLong(key, redisTemplate);
            entityIdCounter.incrementAndGet();
            if (log.isDebugEnabled()) {
                log.debug("测试执行记录统计状态切面：执行Id:" + executeId + "计数+1, key:" + key);
            }
        } else if (StringUtils.equals(newStatus, TestStatusType.STATUS_UN_EXECUTED)) {
            doDecrementRedis(executeId, projectId, oldCreateTime);
        }
    }

    private void doDecrementRedis(Long executeId, String projectId, LocalDateTime time) {
        TestCycleCaseHistoryDTO e = new TestCycleCaseHistoryDTO();
        e.setExecuteId(executeId);
        e.setOldValue(TestStatusType.STATUS_UN_EXECUTED);
        e.setField(FIELD_STATUS);
        PageRequest pageRequest =  new PageRequest(1, 1,new Sort(Sort.Direction.DESC, "id"));
        Page<TestCycleCaseHistoryDTO> page = PageHelper.doPageAndSort(pageRequest,() -> testCycleCaseHistoryMapper.query(e));
        if (page != null && !page.getContent().isEmpty()) {
            time = LocalDateTime.ofInstant(page.getContent().get(0).getLastUpdateDate().toInstant(), ZoneId.systemDefault());
        }
        String key = REDIS_COUNT_KEY + projectId + ":" + time.format(DateTimeFormatter.ofPattern(DATE_FORMATTER));
        RedisAtomicLong entityIdCounter = redisTemplateUtil.getRedisAtomicLong(key, redisTemplate);
        entityIdCounter.decrementAndGet();

        if (log.isDebugEnabled()) {
            log.debug("测试执行记录统计状态切面:执行ID：" + executeId + "计数-1, key:" + key);
        }
    }

}
