package script.db.groovy.test_manager_service


databaseChangeLog(logicalFilePath: 'test_priority.groovy') {
    changeSet(id: '2020-08-19-test_priority', author: 'jiaxu.cui@hand-china.com') {
        createTable(tableName: "test_priority", remarks: "优先级") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'name', type: 'VARCHAR(32)', remarks: '名称') {
                constraints(nullable: 'false')
            }
            column(name: 'description', type: 'VARCHAR(255)', remarks: '描述')
            column(name: 'colour', type: 'VARCHAR(20)', remarks: '颜色') {
                constraints(nullable: 'false')
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: 'false')
            }
            column(name: 'is_default', type: 'TINYINT UNSIGNED', defaultValue: "0", remarks: '是否默认') {
                constraints(nullable: 'false')
            }
            column(name: 'is_enable', type: 'TINYINT UNSIGNED', defaultValue: "1", remarks: '是否启用') {
                constraints(nullable: 'false')
            }
            column(name: 'sequence', type: 'DECIMAL', defaultValue: "0", remarks: '排序') {
                constraints(nullable: 'false')
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}