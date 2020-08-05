package script.db.groovy.test_manager_service

/**
 * @author lihao* @since 2020/08/04
 */
databaseChangeLog(logicalFilePath: "script/db/test_api_task_result_assertion.groovy") {
    changeSet(author: 'lihao', id: '2020-08-04-init_table_test_api_task_result_info') {
        createTable(tableName: "test_api_task_result_info", remarks: "执行结果信息") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: 'result_id', type: 'BIGINT UNSIGNED', remarks: '关联执行结果id')
            column(name: 'request_header', type: 'text', remarks: '请求头信息')
            column(name: 'request_body', type: 'text', remarks: '请求体信息')
            column(name: 'response_header', type: 'text', remarks: '响应头信息')
            column(name: 'response_body', type: 'text', remarks: '响应体信息')
        }
    }

    changeSet(id: '2020-08-04-test-api-task-result-info', author: 'lihao') {
        createIndex(tableName: "test_api_task_result_info", indexName: "idx_result_id") {
            column(name: "result_id")
        }
    }
}
