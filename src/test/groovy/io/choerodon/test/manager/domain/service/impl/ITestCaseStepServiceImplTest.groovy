package io.choerodon.test.manager.domain.service.impl

import com.google.common.collect.Lists
import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService
import io.choerodon.test.manager.app.service.TestCycleCaseStepService
import io.choerodon.test.manager.app.service.impl.TestCycleCaseAttachmentRelServiceImpl
import io.choerodon.test.manager.domain.repository.TestCaseStepRepository
import io.choerodon.test.manager.domain.service.ITestCaseStepService
import io.choerodon.test.manager.domain.service.ITestCycleCaseAttachmentRelService
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE
import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper
import io.choerodon.test.manager.infra.repository.impl.TestCaseStepRepositoryImpl
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by 842767365@qq.com on 7/27/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class ITestCaseStepServiceImplTest extends Specification {

    def "Query"() {
        given:
        TestCaseStepRepository repository = Mock(TestCaseStepRepository)
        TestCaseStepE stepE = new TestCaseStepE(testCaseStepRepository: repository)
        ITestCaseStepService service = new ITestCaseStepServiceImpl()
        when:
        service.query(stepE)
        then:
        1 * repository.query(_)
    }

    def "RemoveStep"() {
        given:
        TestCaseStepRepository repository = Mock(TestCaseStepRepository)
        TestCaseStepE stepE = new TestCaseStepE(testCaseStepRepository: repository)

        ITestCycleCaseStepService caseStepService = Mock(ITestCycleCaseStepService)
        TestCycleCaseAttachmentRelService attachmentRelService = Mock(TestCycleCaseAttachmentRelService)
        ITestCaseStepService service = new ITestCaseStepServiceImpl(testCycleCaseStepService: caseStepService, attachmentRelService: attachmentRelService)

        when:
        service.removeStep(null)
        then:
        thrown(IllegalArgumentException)
        when:
        service.removeStep(stepE)
        then:
        1 * repository.query(_) >> new ArrayList<>()
        0 * attachmentRelService.delete(_,_)
        0 * caseStepService.deleteStep(_)
        1 * repository.delete(_)
        when:
        service.removeStep(stepE)
        then:
        1 * repository.query(_) >> Lists.newArrayList(new TestCaseStepE(stepId: 1))
        1 * caseStepService.deleteStep(_)
        1 * attachmentRelService.delete(_,_)
        1 * repository.delete(_)
    }
}
