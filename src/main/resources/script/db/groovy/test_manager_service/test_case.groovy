package script.db.groovy.test_manager_service

/**
 * @author zhaotianxin* @since 2019/11/14
 */
databaseChangeLog(logicalFilePath: "script/db/test_case.groovy") {
    changeSet(author: 'zhaotianxin', id: '2019-11-14-init_table_test_case') {
        createTable(tableName: "test_case", remarks: "用例") {
            column(name: 'case_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'case_num', type: "VARCHAR(255)", remarks: 'case编号')
            column(name: 'summary', type: "VARCHAR(255)", remarks: '概要')
            column(name: 'description', type: 'text', remarks: '描述')
            column(name: 'rank', type: 'VARCHAR(765)', remarks: 'rank')
            column(name: 'folder_id', type: 'BIGINT UNSIGNED', remarks: '所属文件夹')
            column(name: 'version_num', type: 'BIGINT UNSIGNED', remarks: '版本Id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '所属项目')
            column(name: 'version_step_num', type: 'BIGINT UNSIGNED', remarks: '步骤版本号',defaultValue: "1")

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-11-29-test-case-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "test_case", indexName: "idx_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "test_case", indexName: "idx_folder_id") {
            column(name: "folder_id")
        }
    }

    changeSet(id: '2020-08-24-test-case-add-column-priority-id', author: 'jiaxu.cui@gmail.com') {
        addColumn(tableName: 'test_case') {
            column(name: 'priority_id', type: 'BIGINT UNSIGNED', remarks: 'priority id')
        }
    }
}
