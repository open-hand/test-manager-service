package io.choerodon.test.manager.app.service.impl

import com.google.common.collect.Lists
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService
import io.choerodon.test.manager.domain.aop.TestCaseCountRecordAOP
import io.choerodon.test.manager.domain.aop.TestCycleCaseHistoryRecordAOP
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseHistoryEFactory
import io.choerodon.test.manager.infra.common.utils.RedisTemplateUtil
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.support.atomic.RedisAtomicLong
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Method

@PrepareForTest(TestCycleCaseAttachmentRelEFactory.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class HistoryAOPServiceImplSpec extends Specification {

    @Shared
    TestCycleCaseHistoryService testCycleCaseHistoryService = Mock(TestCycleCaseHistoryService)

    @Shared
    TestCycleCaseHistoryRecordAOP recordAOP = new TestCycleCaseHistoryRecordAOP(testCycleCaseHistoryService:testCycleCaseHistoryService);

    def "recordAttachUpload"(){
        given:
        JoinPoint jp=Mock(JoinPoint)
        when:
        recordAOP.recordAttachUpload(jp)
        then:
        2*jp.getArgs()>>[1L,"eee",3L,2L]
    }

    def "recordAttachDelete"(){
        given:
        TestCycleCaseAttachmentRelE attach = Mock(TestCycleCaseAttachmentRelE)
        PowerMockito.mockStatic(TestCycleCaseAttachmentRelEFactory.class)
        PowerMockito.when(TestCycleCaseAttachmentRelEFactory.create()).thenReturn(attach)
        ProceedingJoinPoint jp=Mock(ProceedingJoinPoint )
        when:
        recordAOP.recordAttachDelete(jp,"bucketName",11L,)
        then:
        1*attach.querySelf()>> Lists.newArrayList(attach)
    }

}