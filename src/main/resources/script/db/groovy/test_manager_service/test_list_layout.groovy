package script.db.groovy.agile_service

/**
 *
 * @author zhaotianxin
 * @date 2021-05-07 11:43
 */
databaseChangeLog(logicalFilePath:'test_list_layout.groovy') {
    changeSet(id: '2022-05-26-test-list-layout', author: 'ztxemail@163.com') {
        createTable(tableName: "test_list_layout", remarks: '测试用例/测试计划执行列配置表') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'apply_type', type: 'VARCHAR(30)', remarks: '应用类型') {
                constraints(nullable: false)
            }
            column(name: 'user_id', type: 'BIGINT UNSIGNED', remarks: '用户id') {
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
    }
}
