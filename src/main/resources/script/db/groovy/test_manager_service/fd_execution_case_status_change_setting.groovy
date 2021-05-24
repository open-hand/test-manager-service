package script.db.groovy.agile_service

/**
 *
 * @author zhaotianxin
 * @date 2021-05-10 16:35
 **/
databaseChangeLog(logicalFilePath: 'fd_execution_case_status_change_setting.groovy') {
    changeSet(id: '2021-05-10-create-table-fd-execution-case-status-change-setting', author: 'ztxemail@163.com') {
        createTable(tableName: 'fd_execution_case_status_change_setting') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'ID,主键') {
                constraints(primaryKey: true)
            }
            column(name: 'agile_issue_type_id', type: 'BIGINT UNSIGNED', remarks: '敏捷问题类型id') {
                constraints(nullable: false)
            }
            column(name: 'agile_status_id', type: 'BIGINT UNSIGNED', remarks: '敏捷状态id') {
                constraints(nullable: false)
            }
            column(name: 'test_status_id', type: 'BIGINT UNSIGNED', remarks: '测试状态id') {
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
