package script.db
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_case_step') {
        createTable(tableName: "test_case_step") {
            column(name: 'step_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'rank', type: 'VARCHAR(25)', remarks: 'rank') {
                constraints(nullable: false)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '所属issue') {
                constraints(nullable: false)
            }
            column(name: 'test_step', type: 'VARCHAR(300)', remarks: '测试步骤')
            column(name: 'test_data', type: 'VARCHAR(300)', remarks: '测试数据')
            column(name: 'expected_result', type: 'VARCHAR(300)', remarks: '期待结果')


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_case_step", indexName: "idx_case_step_issue_id") {
            column(name: "issue_id")
        }
        createIndex(indexName: 'uk_issue_id_rank', tableName: 'test_case_step', unique: true) {
            column(name: 'issue_id')
            column(name: 'rank')
        }
    }
}