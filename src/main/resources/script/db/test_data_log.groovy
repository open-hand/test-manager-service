package script.db

/**
 * @author zhaotianxin* @since 2019/11/14
 */
databaseChangeLog(logicalFilePath: "script/db/test_data_log.groovy") {
    changeSet(author: 'zhaotianxin', id: '2019-11-14-init_table_test_data_log') {
        createTable(tableName: "test_data_log", remarks: "日志记录") {
            column(name: 'log_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'log id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project id') {
                constraints(nullable: false)
            }
            column(name: 'field', type: 'VARCHAR(255)', remarks: 'field')
            column(name: 'old_value', type: 'VARCHAR(255)', remarks: 'old value')
            column(name: 'old_string', type: 'VARCHAR(255)', remarks: 'old string')
            column(name: 'new_value', type: 'VARCHAR(255)', remarks: 'new value')
            column(name: 'new_string', type: 'VARCHAR(255)', remarks: 'new string')
            column(name: 'case_id', type: 'BIGINT UNSIGNED', remarks: 'case id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
    }
    changeSet(author: 'lizhaozhong@hang-china.com', id: '2019-11-26-update-column_old_string') {
            renameColumn(columnDataType: 'text', newColumnName: 'old_value', oldColumnName: 'old_value', tableName: 'test_data_log')
            renameColumn(columnDataType: 'text', newColumnName: 'new_value', oldColumnName: 'new_value', tableName: 'test_data_log')
            renameColumn(columnDataType: 'text', newColumnName: 'old_string', oldColumnName: 'old_string', tableName: 'test_data_log')
            renameColumn(columnDataType: 'text', newColumnName: 'new_string', oldColumnName: 'new_string', tableName: 'test_data_log')
    }

}

