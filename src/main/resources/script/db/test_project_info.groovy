package script.db

/**
 * @author zhaotianxin* @since 2019/11/14
 */
databaseChangeLog(logicalFilePath : "script/db/test_data_log.groovy"){
    changeSet(author: 'zhaotianxin', id: '2019-11-14-init_table_test_data_log'){
        createTable(tableName: "test_data_log",remarks: "测试日志"){
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

}