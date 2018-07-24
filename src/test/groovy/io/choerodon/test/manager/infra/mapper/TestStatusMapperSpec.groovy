package io.choerodon.test.manager.infra.mapper

import io.choerodon.test.manager.IntegrationTestConfiguration
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory
import io.choerodon.test.manager.infra.dataobject.TestStatusDO
import org.apache.commons.lang.StringUtils
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
class TestStatusMapperSpec extends Specification{

    @Autowired
    TestStatusMapper statusMapper;

    def "insert"(){
        given:
        TestStatusDO statusDO=new TestStatusDO();
        statusDO.setStatusName("statusName1")
        statusDO.setStatusColor("pink")
        statusDO.setProjectId(new Long(1))

        when:
        TestStatusDO statusResult=statusMapper.insert(statusDO)

        then:
        statusResult.getStatusId()!=null
    }

    def "update"(){
        given:
        TestStatusDO statusDO=new TestStatusDO();
        statusDO.setStatusName("statusName1")
        statusDO.setStatusColor("pink")
        statusDO.setProjectId(new Long(1))
    }

    def "query"(){
        TestStatusDO status=new TestStatusDO();
        status.setProjectId(new Long(999))
        status.setStatusName("name1")
        status.setStatusColor("pink")
        when:
        List<TestStatusDO> result=statusMapper.queryAllUnderProject(status)
        then:
        result.size()==1
        result.get(0).getProjectId().equals(new Long(999))
        StringUtils.equals(result.get(0).getStatusName(),"name1")
        StringUtils.equals(result.get(0).getStatusColor(),"pink")
    }

}