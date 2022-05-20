package script.db.groovy.test_manager_service

/**
 * @author zhaotianxin* @since 2019/11/14
 */
databaseChangeLog(logicalFilePath: "script/db/test_project_info.groovy") {
    changeSet(author: 'zhaotianxin', id: '2019-11-14-init_table_test_project_info') {
        createTable(tableName: "test_project_info", remarks: "项目信息") {
            column(name: 'info_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: 'id') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id') {
                constraints(nullable: false)
            }
            column(name: 'project_code', type: 'VARCHAR(255)', remarks: '项目编码')
            column(name: 'case_max_num', type: 'BIGINT UNSIGNED', remarks: '用例最大编号')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-11-29-test-project-info-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "test_project_info", indexName: "idx_project_id") {
            column(name: "project_id")
        }
    }

}