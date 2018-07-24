package io.choerodon.test.manager.infra.repository

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory
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
class TestCycleSpec extends Specification{

    def TestCycleE cycle;

    def "insert"(){
        given:
        TestCycleE cycleE= TestCycleEFactory.create();
        cycleE.setCycleName("循环999")
        cycleE.setVersionId(new Long(999))
        when:
        cycle=cycleE.addSelf();
        then:
        cycle.getCycleId()!=null
        cycle.getCycleName()=="循环999"
        cycle.getVersionId().equals(new Long(999))
    }

}