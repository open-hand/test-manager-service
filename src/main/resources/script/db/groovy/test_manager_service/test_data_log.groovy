package script.db.groovy.test_manager_service

/**
 * @author zhaotianxin* @since 2019/11/14
 */
databaseChangeLog(logicalFilePath: "script/db/test_data_log.groovy") {
    changeSet(author: 'zhaotianxin', id: '2019-11-14-init_table_test_data_log') {
        createTable(tableName: "test_data_log", remarks: "日志记录") {
            column(name: 'log_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '日志id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'field', type: 'VARCHAR(255)', remarks: '字段')
            column(name: 'old_value', type: 'text', remarks: '旧值id')
            column(name: 'old_string', type: 'text', remarks: '旧值')
            column(name: 'new_value', type: 'text', remarks: '新值id')
            column(name: 'new_string', type: 'text', remarks: '新值')
            column(name: 'case_id', type: 'BIGINT UNSIGNED', remarks: '用例id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")

        }
    }

    changeSet(id: '2019-11-29-test-data-log-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "test_data_log", indexName: "idx_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "test_data_log", indexName: "idx_case_id") {
            column(name: "case_id")
        }
        createIndex(tableName: "test_data_log", indexName: "idx_field") {
            column(name: "field")
        }
    }

}

