package script.db

/**
 * @author zhaotianxin* @since 2019/11/27
 */
databaseChangeLog(logicalFilePath: 'script/script/test_cycle_case_label_rel.groovy') {
    changeSet(author: 'zhaotainxin', id: '2019-11-27-init_table_test_cycle_case_label_rel') {
        createTable(tableName: "test_cycle_case_label_rel") {

            column(name: 'execute_id', type: 'BIGINT UNSIGNED', remarks: '测试循环id')
            column(name: 'case_id', type: 'BIGINT UNSIGNED', remarks: '测试用例ID')
            column(name: 'label_id', type: 'BIGINT UNSIGNED', remarks: '标签的Id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目ID')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}
