package script.db.groovy.agile_service
databaseChangeLog(logicalFilePath:'test_personal_sort.groovy') {
    changeSet(id: '2022-05-26-test-personal-sort', author: 'kaiwen.li@hand-china.com') {
        createTable(tableName: "test_personal_sort", remarks: '测试用例个人排序表') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'user_id', type: 'BIGINT UNSIGNED', remarks: '用户id') {
                constraints(nullable: false)
            }
            column(name: 'business_type', type: 'VARCHAR(128)', remarks: '业务类型') {
                constraints(nullable: false)
            }
            column(name: 'sort_json', type: 'TEXT', remarks: '排序json') {
                constraints(nullable: false)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'organization_id', type: 'BIGINT UNSIGNED', remarks: '组织id') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_personal_sort", indexName: "agile_issue_personal_sort_n1") {
            column(name: "project_id", type: "BIGINT UNSIGNED")
        }
        createIndex(tableName: "test_personal_sort", indexName: "agile_issue_personal_sort_n2") {
            column(name: "user_id", type: "BIGINT UNSIGNED")
        }
    }


}