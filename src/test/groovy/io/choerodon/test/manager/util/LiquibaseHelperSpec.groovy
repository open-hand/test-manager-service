package io.choerodon.test.manager.util

import io.choerodon.test.manager.infra.common.utils.LiquibaseHelper
import io.choerodon.test.manager.infra.exception.DBTypeException
import io.choerodon.test.manager.infra.exception.TestCycleCaseException
import spock.lang.Specification

import java.util.function.Function

class LiquibaseHelperSpec extends Specification {

    def "DbType"() {
        when:
        LiquibaseHelper.dbType('error_url')
        then:
        thrown(DBTypeException)

        expect:
        result==LiquibaseHelper.dbType(url)
        where:
        url             |   result
        'jdbc:h2'       |   LiquibaseHelper.DbType.H2
        'jdbc:oracle'   |   LiquibaseHelper.DbType.ORACLE
        'jdbc:mysql'    |   LiquibaseHelper.DbType.MYSQL
        'jdbc:sqlserver'  | LiquibaseHelper.DbType.SQLSERVER
        'jdbc:sap'      |   LiquibaseHelper.DbType.HANA

    }


    def "ExecuteFunctionByMysqlOrOracle"() {
        Function f1={Long param-> return param+1}
        when:
        def res=LiquibaseHelper.executeFunctionByMysqlOrOracle(f1,{param->return param-1},'jdbc:oracle',2L)
        then:
        res==1

        when:
        LiquibaseHelper.executeFunctionByMysqlOrOracle(f1,{param->return param-1},'jdbc:sap',2L)
        then:
        thrown(TestCycleCaseException)
    }

    def "ExecuteBiFunctionByMysqlOrOracle"() {
        when:
        def res= LiquibaseHelper.executeBiFunctionByMysqlOrOracle({pa1,pa2->return pa1+pa2},{pa1,pa2->return pa1-pa2},'jdbc:oracle',2,1)
        then:
        res==1

        when:
        LiquibaseHelper.executeBiFunctionByMysqlOrOracle({pa1,pa2->return pa1+pa2},{pa1,pa2->return pa1-pa2},'jdbc:sap',2,1)
        then:
        thrown(TestCycleCaseException)
    }
}
