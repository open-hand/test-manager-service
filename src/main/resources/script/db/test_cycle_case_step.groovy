package script.db
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_case_step') {
        createTable(tableName: "test_cycle_case_step") {
            column(name: 'execute_step_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'execute_id', type: 'BIGINT UNSIGNED', remarks: '测试循环id') {
                constraints(nullable: false)
            }
            column(name: 'step_id', type: 'BIGINT UNSIGNED', remarks: '测试issue布 id') {
                constraints(nullable: false)
            }
            column(name: 'comment', type: 'text', remarks: '描述')
            column(name: 'step_status', type: 'BIGINT UNSIGNED', remarks: '状态')


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_cycle_case_step", indexName: "idx_cycle_step_execute_id") {
            column(name: "execute_id")
        }
    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_cycle_case_step') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_cycle_case_step_s', startValue: "1")
        }
    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-change-step-column-comment') {
        renameColumn(columnDataType:'text',newColumnName:'description',oldColumnName:'comment',tableName:"test_cycle_case_step")
    }

    changeSet(id: '2019-10-21-add-index-page-id', author: 'shinan.chenX@gmail.com') {
        createIndex(tableName: "test_cycle_case_step", indexName: "idx_stepid_executeid") {
            column(name: "step_id")
            column(name: "execute_id")
        }
    }

    changeSet(id: '2019-11-27-add-column', author: 'zhaotianxin') {
        addColumn(tableName:'test_cycle_case_step'){
            column(name:'test_step',type:'varchar(300)',remarks: '测试步骤')
            column(name:'test_data',type:'varchar(300)',remarks: '测试数据')
            column(name:'expect_result',type:'varchar(300)',remarks: '预期结果')
        }
    }
}
