package script.db.groovy.test_manager_service

/**
 * @author lihao* @since 2020/08/04
 */
databaseChangeLog(logicalFilePath: "script/db/test_api_assertion.groovy") {
    changeSet(author: 'lihao', id: '2020-08-04-init_table_test_api_assertion') {
        createTable(tableName: "test_api_assertion", remarks: "断言配置表") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: "VARCHAR(32)", remarks: '断言名称')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: 'test_api_case_id', type: "BIGINT UNSIGNED", remarks: '关联测试用例id')
            column(name: 'type', type: 'TINYINT(1)', remarks: '断言类型')
            column(name: 'symbol', type: 'TINYINT(1)', remarks: '比较符')
            column(name: 'comparison_key', type: 'VARCHAR(32)', remarks: '比较字段',defaultValue: "")
            column(name: 'expect_value', type: 'VARCHAR(32)', remarks: '期望值')
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2020-08-04-test-api-assertion-add-index', author: 'lihao') {
        createIndex(tableName: "test_api_assertion", indexName: "idx_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "test_api_assertion", indexName: "idx_test_api_case_id") {
            column(name: "test_api_case_id")
        }
    }
}
