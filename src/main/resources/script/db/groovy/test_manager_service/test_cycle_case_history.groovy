package script.db.groovy.test_manager_service
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_case_history') {
        validCheckSum("7:fac1c68f7ea54867ac5e708f1db92382")

        createTable(tableName: "test_cycle_case_history") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'execute_id', type: "BIGINT UNSIGNED") {
                constraints(primaryKey: true)
            }
            column(name: 'field', type: "VARCHAR(50)", remarks: "字段") {
                constraints(nullable: false)
            }
            column(name: 'old_value', type: 'text', remarks: '旧值') {
                constraints(nullable: false)
            }
            column(name: 'new_value', type: 'text', remarks: '新值') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_cycle_case_history') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_cycle_case_history_s', startValue: "1")
        }
    }

    changeSet(author: 'kaiwen.li@hang-china.com', id: '2022-04-15-test-cycle-case-history-add-index') {
        createIndex(tableName: "test_cycle_case_history", indexName: "idx_execute_id") {
            column(name: "execute_id")
        }
        createIndex(tableName: "test_cycle_case_history", indexName: "idx_field") {
            column(name: "field")
        }
        createIndex(tableName: "test_cycle_case_history", indexName: "idx_old_value") {
            column(name: "old_value(10)")
        }
        createIndex(tableName: "test_cycle_case_history", indexName: "idx_new_value") {
            column(name: "new_value(10)")
        }
    }
}