package script.db.groovy.agile_service

/**
 *
 * @author zhaotianxin
 * @date 2021-05-07 11:48
 * */
databaseChangeLog(logicalFilePath:'test_list_layout_column_rel.groovy') {
    changeSet(id: '2022-05-26-test-list-layout-column-rel', author: 'ztxemail@163.com') {
        createTable(tableName: "test_list_layout_column_rel") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'layout_id', type: 'BIGINT UNSIGNED', remarks: '布局id') {
                constraints(nullable: false)
            }

            column(name: 'field_id', type: 'BIGINT UNSIGNED', remarks: '字段id')

            column(name: 'column_code', type: 'VARCHAR(100)', remarks: '列编码') {
                constraints(nullable: false)
            }
            column(name: 'display', type: 'TINYINT UNSIGNED', remarks: '是否显示') {
                constraints(nullable: false)
            }
            column(name: 'width', type: 'int', remarks: '列的宽度') {
                constraints(nullable: false)
            }
            column(name: 'sort', type: 'int', remarks: '排序') {
                constraints(nullable: false)
            }
            column(name: 'extra_config', type: 'TINYINT UNSIGNED(1)', remarks: '额外配置')

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
