package io.choerodon.test.manager.infra.repository

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 7/23/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestCycleCaseRepositorySpec extends Specification{
    @Autowired
    TestCycleCaseRepository repository;

    def "insert"(){
        TestCycleCaseE caseE=TestCycleCaseEFactory.create();
//        caseE.set

        repository.insert()
    }

}