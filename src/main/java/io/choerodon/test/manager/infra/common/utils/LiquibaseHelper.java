package io.choerodon.test.manager.infra.common.utils;

/**
 * Created by hailuoliu@choerodon.io on 2018/7/11.
 */
public class LiquibaseHelper {


    public static DbType dbType(String url) {
        DbType currentDbType = null;
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
        }
        return currentDbType;
    }

    public static boolean isSupportSequence(String url) {
        return dbType(url).isSupportSequence();
    }

    public static boolean isH2Base(String url) {
        return url.startsWith("jdbc:h2");
    }

    public static boolean isOracle(String url){
        return url.startsWith("jdbc:oracle");
    }

    public static boolean isMysql(String url) {
        return url.startsWith("jdbc:mysql");
    }

    public static boolean isSqlServer(String url) {
        return url.startsWith("jdbc:sqlserver");
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


}
