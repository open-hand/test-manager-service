package script.db.groovy.test_manager_service

/**
 * @author lihao* @since 2020/08/04
 */
databaseChangeLog(logicalFilePath: "script/db/test_api_config.groovy") {
    changeSet(author: 'lihao', id: '2020-08-04-init_table_test_api_config') {
        createTable(tableName: "test_api_config", remarks: "接口配置表") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'type', type: "VARCHAR(32)", remarks: '配置类型,0 普通请求头 1 普通请求参数 2 任务配置请求头 3 任务配置用户自定义参数')
            column(name: 'config', type: "text", remarks: '配置内容，json格式的map')
            column(name: 'source_id', type: 'BIGINT UNSIGNED', remarks: '目标id，测试用列id或者任务配置id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2020-08-04-test-api-config-add-index', author: 'lihao') {
        createIndex(tableName: "test_api_config", indexName: "idx_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "test_api_config", indexName: "idx_source_id_type") {
            column(name: "source_id")
            column(name: "type")
        }
    }
}
