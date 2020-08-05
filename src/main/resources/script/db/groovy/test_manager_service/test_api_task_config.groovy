package script.db.groovy.test_manager_service

/**
 * @author lihao* @since 2020/08/04
 */
databaseChangeLog(logicalFilePath: "script/db/test_api_task_config.groovy") {
    changeSet(author: 'lihao', id: '2020-08-04-init_table_test_api_task_config') {
        createTable(tableName: "test_api_task_config", remarks: "任务配置表") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: "VARCHAR(32)", remarks: '测试用例名称')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: 'protocol',type: 'VARCHAR(32)',remarks: '协议类型,0 http 1 https')
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2020-08-04-test-api-task-config-add-index', author: 'lihao') {
        createIndex(tableName: "test_api_task_config", indexName: "idx_project_id") {
            column(name: "project_id")
        }
    }
}
