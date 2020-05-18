package script.db.groovy.test_manager_service
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_case_defect_rel') {
        createTable(tableName: "test_cycle_case_defect_rel") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'defect_type', type: 'VARCHAR(30)', remarks: '缺陷类型') {
                constraints(nullable: false)
            }
            column(name: 'defect_link_id', type: 'BIGINT UNSIGNED', remarks: '缺陷关联ID') {
                constraints(nullable: false)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '测试Id') {
                constraints(nullable: false)
            }


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: "test_cycle_case_defect_rel", columnNames: "defect_type,defect_link_id,issue_id")

    }

    changeSet(id:'2018-08-27-defect-add-column-projectId',author: 'zongwei.li'){
        addColumn(tableName:'test_cycle_case_defect_rel'){
            column(name:'project_id',type:'BIGINT UNSIGNED', defaultValue: '0'){
                constraints(nullable: false)
            }
        }
    }

    changeSet(author: 'zongwei.li@hang-china.com', id: '2018-08-30-add-idx_project_id_issue_id') {
        createIndex(indexName: 'idx_project_id_issue_id', tableName: 'test_cycle_case_defect_rel', unique: false) {
            column(name: 'project_id')
            column(name: 'issue_id')
        }
    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_cycle_case_defect_rel') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_cycle_case_defect_rel_s', startValue: "1")
        }
    }
}