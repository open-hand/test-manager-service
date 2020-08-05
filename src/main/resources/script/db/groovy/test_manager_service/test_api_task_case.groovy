package script.db.groovy.test_manager_service

/**
 * @author lihao* @since 2020/08/04
 */
databaseChangeLog(logicalFilePath: "script/db/test_api_task_case.groovy") {
    changeSet(author: 'lihao', id: '2020-08-04-init_table_test_api_task_case') {
        createTable(tableName: "test_api_task_case  ", remarks: "测试任务与测试用例关联表") {
            column(name: 'task_id', type: 'BIGINT UNSIGNED', remarks: '测试任务id')
            column(name: 'case_id', type: "BIGINT UNSIGNED", remarks: '接口用例id')
        }

        addUniqueConstraint(tableName: 'test_api_task_case', constraintName: 'uk_task_id_case_id',
                columnNames: 'task_id,case_id')
    }
}
