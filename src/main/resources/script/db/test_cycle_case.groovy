package script.db

databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_case') {
        createTable(tableName: "test_cycle_case") {
            column(name: 'execute_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'cycle_id', type: 'BIGINT UNSIGNED', remarks: '测试循环id') {
                constraints(nullable: false)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '测试issue id') {
                constraints(nullable: false)
            }
            column(name: 'rank', type: 'VARCHAR(25)', remarks: 'rank') {
                constraints(nullable: false)
            }
            column(name: 'execution_status', type: 'BIGINT UNSIGNED', remarks: '执行状态') {
                constraints(nullable: false)
            }
            column(name: 'assigned_to', type: 'BIGINT UNSIGNED', defaultValue: "0", remarks: '指定人')
            column(name: 'comment', type: 'text', remarks: '注释')


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_cycle_case", indexName: "idx_cycle_case_cycle_id") {
            column(name: "cycle_id")
        }
        createIndex(tableName: "test_cycle_case", indexName: "idx_cycle_case_issue_id") {
            column(name: "issue_id")
        }
        createIndex(indexName: 'uk_cycle_id_rank', tableName: 'test_cycle_case', unique: true) {
            column(name: 'cycle_id')
            column(name: 'rank')
        }
    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_cycle_case') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_cycle_case_s', startValue: "1")
        }
    }
}