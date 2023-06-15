package script.db.groovy.test_manager_service

/**
 * @author lizhaozhong* @since 2019/11/26
 */
databaseChangeLog(logicalFilePath: "script/db/test_data_log.groovy") {
    changeSet(author: 'lizhaozhong', id: '2019-11-26-init_table_test_plan') {
        createTable(tableName: "test_plan", remarks: "测试计划表") {
            column(name: 'plan_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '计划id') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(255)', remarks: '名称')
            column(name: 'description', type: 'text', remarks: '描述')
            column(name: 'manager_id', type: 'BIGINT UNSIGNED', remarks: '管理人id')
            column(name: 'start_date', type: 'DATETIME', remarks: '开始日期')
            column(name: 'end_date', type: 'DATETIME', remarks: '结束日期')
            column(name: 'status_code', type: 'VARCHAR(255)', remarks: '状态编码')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: "is_auto_sync", type: "tinyint", remarks: '自动同步')
            column(name: "init_status", type: "VARCHAR(255)", remarks: '初始化状态')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-11-29-test-plan-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "test_plan", indexName: "idx_project_id") {
            column(name: "project_id")
        }
    }

    changeSet(id: '2021-04-15-test-plan-add-column', author: 'chihao.ran@hand-china.com') {
        addColumn(tableName: 'test_plan') {
            column(name: 'sprint_id', type: 'BIGINT UNSIGNED', remarks: '冲刺id')
            column(name: 'product_version_id', type: 'BIGINT UNSIGNED', remarks: '产品版本id')
        }
    }
}

