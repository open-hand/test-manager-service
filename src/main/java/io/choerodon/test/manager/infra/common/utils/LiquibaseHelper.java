package io.choerodon.test.manager.infra.common.utils;

import io.choerodon.test.manager.infra.exception.DBTypeException;
import io.choerodon.test.manager.infra.exception.TestCycleCaseException;
import org.apache.poi.ss.formula.functions.T;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by hailuoliu@choerodon.io on 2018/7/11.
 */
public class LiquibaseHelper {


    public static DbType dbType(String url) {
        DbType currentDbType;
        if (url.startsWith("jdbc:h2")) {
            currentDbType = DbType.H2;
        } else if (url.startsWith("jdbc:oracle")) {
            currentDbType = DbType.ORACLE;
        } else if (url.startsWith("jdbc:mysql")) {
            currentDbType = DbType.MYSQL;
        } else if (url.startsWith("jdbc:sqlserver")) {
            currentDbType = DbType.SQLSERVER;
        } else if (url.startsWith("jdbc:sap")) {
            currentDbType = DbType.HANA;
        }else {
            throw new DBTypeException(DBTypeException.UNKNOWN_DB_TYPE);
        }
        return currentDbType;
    }

    public enum DbType {
        MYSQL(true, false), ORACLE(false, true), HANA(false, true), SQLSERVER(true, false), H2(true, false), DB2(false, true);

        private boolean supportAutoIncrement;

        private boolean supportSequence;


        DbType(boolean supportAutoIncrement, boolean supportSequence) {
            this.supportAutoIncrement = supportAutoIncrement;
            this.supportSequence = supportSequence;
        }

        public boolean isSupportAutoIncrement() {
            return supportAutoIncrement;
        }

        public boolean isSupportSequence() {
            return supportSequence;
        }
    }

    public static<U,R> R executeFunctionByMysqlOrOracle(Function<U,R> mysqlFunction,Function<U,R> oracleFunction,String dsUrl,U param){
        switch (dbType(dsUrl)){
            case MYSQL:
            case H2:
                return mysqlFunction.apply(param);
            case ORACLE:
                return oracleFunction.apply(param);
            default:
                throw new TestCycleCaseException(TestCycleCaseException.ERROR_UN_SUPPORT_DB_TYPE+",need mysql or oracle but now is:"+dsUrl);
        }
    }

    public static<T,U,R> R executeBiFunctionByMysqlOrOracle(BiFunction<T,U,R> mysqlFunction, BiFunction<T,U,R> oracleFunction, String dsUrl,T param1, U param2){
        switch (dbType(dsUrl)){
            case MYSQL:
            case H2:
                return mysqlFunction.apply(param1,param2);
            case ORACLE:
                return oracleFunction.apply(param1,param2);
            default:
                throw new TestCycleCaseException(TestCycleCaseException.ERROR_UN_SUPPORT_DB_TYPE+",need mysql or oracle but now is:"+dsUrl);
        }
    }

}
