//package io.choerodon.test.manager.infra.repository.impl
//
//import com.google.common.collect.Lists
//import io.choerodon.core.exception.CommonException
//import io.choerodon.test.manager.IntegrationTestConfiguration
//import io.choerodon.test.manager.domain.repository.TestCaseStepRepository
//import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE
//import io.choerodon.test.manager.infra.vo.TestCaseStepDTO
//import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.Import
//import spock.lang.Specification
//
//import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
//
///**
// * Created by 842767365@qq.com on 7/27/18.
// */
//@SpringBootTest(webEnvironment = RANDOM_PORT)
//@Import(IntegrationTestConfiguration)
//class TestCaseStepRepositoryImplSpec extends Specification {
//
//
//    TestCaseStepMapper mapper
//    TestCaseStepRepository repository
//
//    def setup() {
//        mapper = Mock(TestCaseStepMapper)
//        repository = new TestCaseStepRepositoryImpl(testCaseStepMapper: mapper)
//    }
//
//
//
//    def "Insert"() {
//        when:
//        repository.insert(null)
//        then:
//        thrown(CommonException)
//        when:
//        repository.insert(new TestCaseStepE())
//        then:
//        1*mapper.insert(_)>>0
//        thrown(CommonException)
//        when:
//        repository.insert(new TestCaseStepE())
//        then:
//        1*mapper.insert(_)>>1
//    }
//
//    def "Delete"() {
//        when:
//        repository.delete(null)
//        then:
//        thrown(IllegalArgumentException)
//        when:
//        repository.delete(new TestCaseStepE())
//        then:
//        1*mapper.delete(_)
//    }
//
//    def "Update"() {
//        when:
//        repository.update(null)
//        then:
//        thrown(IllegalArgumentException)
//        when:
//        repository.update(new TestCaseStepE())
//        then:
//        1*mapper.updateByPrimaryKey(_)>>0
//        thrown(CommonException)
//        when:
//        repository.update(new TestCaseStepE())
//        then:
//        1*mapper.updateByPrimaryKey(_)>>1
//        1*mapper.query(_)>> Lists.newArrayList(new TestCaseStepDTO())
//    }
//
//    def "Query"() {
//        when:
//        repository.query(null)
//        then:
//        1 * mapper.query(_)
//    }
//
//}
