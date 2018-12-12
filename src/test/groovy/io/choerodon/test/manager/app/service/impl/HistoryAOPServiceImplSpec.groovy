package io.choerodon.test.manager.app.service.impl

import com.google.common.collect.Lists
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService
import io.choerodon.test.manager.domain.aop.TestCycleCaseHistoryRecordAOP
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Shared
import spock.lang.Specification

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