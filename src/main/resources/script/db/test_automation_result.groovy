package script.db

databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(author: 'haoranpan@hand-china.com', id: '2018-11-23-init_table_test_automation_result') {
        createTable(tableName: 'test_automation_result') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'result', type: 'TEXT', remarks: 'json报告内容') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(author: 'haoranpan@hand-china.com', id: '2018-11-29-add_column_test_automation_result') {
        addColumn(tableName: 'test_automation_result') {
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-03-13-modify-data-type', author: 'ettwz@hotmail.com') {
        modifyDataType(tableName: 'test_automation_result', columnName: 'result', newDataType: "LONGTEXT")
    }
}