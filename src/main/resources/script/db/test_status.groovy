package script.db
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_status') {
        createTable(tableName: "test_status") {
            column(name: 'status_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'status_name', type: 'VARCHAR(30)', remarks: '状态名称') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(300)', remarks: '描述')

            column(name: 'status_color', type: 'VARCHAR(30)', remarks: "颜色") {
                constraints(nullable: false)
            }
            column(name: 'status_type', type: 'VARCHAR(30)', remarks: "状态类型") {
                constraints(nullable: false)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: "项目Id") {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(indexName: 'uk_test_status_name', tableName: 'test_status', unique: true) {
            column(name: 'project_id')
            column(name: 'status_type')
            column(name: 'status_name')
        }
        createIndex(indexName: 'uk_test_status_color', tableName: 'test_status', unique: true) {
            column(name: 'project_id')
            column(name: 'status_type')
            column(name: 'status_color')
        }
    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_status') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_status_s', startValue: "1")
        }
    }

    changeSet(id: '2019-05-10-fix-project-id', author: 'shinan.chenX@gmail') {
        sql(stripComments: true, splitStatements: false, dbms:"mysql") {
            "update test_cycle tc " +
                    "LEFT JOIN  test_issue_folder tif " +
                    "ON tif.version_id = tc.version_id " +
                    "set tc.project_id = tif.project_id"
        }
        sql(stripComments: true, splitStatements: false, dbms:"mysql") {
            "update test_cycle_case tcc " +
                    "LEFT JOIN  test_cycle tc " +
                    "ON tc.cycle_id = tcc.cycle_id " +
                    "set tcc.project_id = tc.project_id"
        }
    }
}