package io.choerodon.test.manager.infra.repository

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.repository.TestStatusRepository
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory
import org.apache.commons.lang.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by jialongZuo@hand-china.com on 7/23/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class TestStatusRepositorySpec extends Specification{

    @Autowired
    TestStatusRepository repository;

    @Shared
    TestStatusE statusQuery;

    def "insert"(){
        given:
        TestStatusE statusE=TestStatusEFactory.create()
        statusE.setProjectId(new Long(999))
        statusE.setStatusName("name1")
        statusE.setStatusColor("pink")
        when:
        TestStatusE result=repository.insert(statusE)
        then:
        result.getStatusId()!=null
    }

    def "query"(){
        given:
        TestStatusE statusE=TestStatusEFactory.create()
        statusE.setProjectId(new Long(999))
        statusE.setStatusName("name1")
        statusE.setStatusColor("pink")
        when:
        List<TestStatusE> result=repository.queryAllUnderProject(statusE)
        then:
        result.size()==1
        result.get(0).getProjectId().equals(new Long(999))
        StringUtils.equals(result.get(0).getStatusName(),"name1")
        StringUtils.equals(result.get(0).getStatusColor(),"pink")

    }

    def "update"(){
        given:
        TestStatusE statusE=TestStatusEFactory.create()
        statusE.setProjectId(new Long(999))
        statusE.setStatusName("name1")
        statusE.setStatusColor("pink")
        statusQuery=repository.queryAllUnderProject(statusE).get(0)
        statusQuery.setStatusColor("yellow")
        statusQuery.setStatusName("name2")

        when:
        TestStatusE result=repository.update(statusQuery)
        then:"验证数据"
        result.getProjectId().equals(new Long(999))
        StringUtils.equals(result.getStatusName(),"name2")
        StringUtils.equals(result.getStatusColor(),"yellow")
        result.getStatusId().equals(statusQuery.getStatusId())
        result.getObjectVersionNumber().longValue()==statusQuery.getObjectVersionNumber()+1
    }

    def "delete"(){
        when:
        repository.delete(statusQuery)
        then:
        repository.queryOne(statusQuery.getStatusId())==null
    }

    def "getDefaultStatus"(){
        given:
        def caseType="CYCLE_CASE"
        def stepType="CASE_STEP"
        when:
        def statusId=repository.getDefaultStatus(caseType)
        def statusId1=repository.getDefaultStatus(stepType)
        then:
        repository.queryOne(statusId).getStatusName().equals("未执行")
        repository.queryOne(statusId1).getStatusName().equals("未执行")
    }
}