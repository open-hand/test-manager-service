package script.db
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(author: 'lizhaozhong@hang-china.com', id: '2019-11-20-init_table_test_case_label_rel') {
        createTable(tableName: "test_case_label_rel") {
            column(name: 'case_id', type: 'BIGINT UNSIGNED', remarks: "项目Id") {
                constraints(nullable: false)
            }
            column(name: 'label_name', type: 'VARCHAR(30)', remarks: '标签名称') {
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

    }
}