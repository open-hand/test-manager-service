package io.choerodon.test.manager.app.service.impl

import com.github.pagehelper.PageInfo
import io.choerodon.core.exception.CommonException
import io.choerodon.test.manager.domain.aop.TestCaseCountRecordAOP
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseHistoryEFactory
import io.choerodon.test.manager.infra.common.utils.DBValidateUtil
import io.choerodon.test.manager.infra.common.utils.RedisTemplateUtil
import org.assertj.core.util.Lists
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.support.atomic.RedisAtomicLong
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Method
import java.time.LocalDateTime
import java.util.function.Function
import java.util.function.Supplier

@PrepareForTest(TestCycleCaseHistoryEFactory.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class RdisAOPServiceImplSpec extends Specification {

    def "countCaseToRedis"(){
        TestCycleCaseHistoryE historyE=Mock(TestCycleCaseHistoryE)
        PowerMockito.mockStatic(TestCycleCaseHistoryEFactory.class)
        PowerMockito.when(TestCycleCaseHistoryEFactory.create()).thenReturn(historyE)
        RedisTemplateUtil redisTemplateUtil = Mock(RedisTemplateUtil)

        TestCaseCountRecordAOP recordAOP=new TestCaseCountRecordAOP(redisTemplate:new RedisTemplate(),redisTemplateUtil: redisTemplateUtil);

        RedisAtomicLong redisAtomicLong= Mock(RedisAtomicLong)
        Method method=recordAOP.getClass().getDeclaredMethod("countCaseToRedis",String.class,String.class,String.class,String.class,Long.class, LocalDateTime.class)
        method.setAccessible(true)
        when:
        method.invoke(recordAOP,"144","2018-02-12", TestStatusE.STATUS_UN_EXECUTED,"oldValue",11L,LocalDateTime.now())
        then:
        1*redisAtomicLong.incrementAndGet()
        1*redisTemplateUtil.getRedisAtomicLong(_,_)>>redisAtomicLong
        when:
        method.invoke(recordAOP,"144","2018-02-12", "oldValue",TestStatusE.STATUS_UN_EXECUTED,11L,LocalDateTime.now())
        then:
        1*redisAtomicLong.decrementAndGet()
        1*redisTemplateUtil.getRedisAtomicLong(_,_)>>redisAtomicLong
        1*historyE.querySelf(_)>>new PageInfo(content: Lists.newArrayList(new TestCycleCaseHistoryE(lastUpdateDate:new Date() )))
    }

    def "dbValidateUtil"(){
        given:
        Supplier function= Mock(Supplier)
        when:
        DBValidateUtil.executeAndvalidateUpdateNum(function,1,"11")
        then:
        1*function.get()>>2
        thrown(CommonException)
    }
}