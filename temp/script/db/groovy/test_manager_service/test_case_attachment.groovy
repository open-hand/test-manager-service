package script.db.groovy.test_manager_service

databaseChangeLog(logicalFilePath: "script/db/test_case_attachment.groovy") {
    changeSet(author: 'yzj', id: '2019-11-20-init_table_test_case_attatchment.groovy') {
        createTable(tableName: "test_case_attachment", remarks: "用例") {
            column(name: 'attachment_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '附件id') {
                constraints(primaryKey: true)
            }
            column(name: 'case_id', type: "BIGINT UNSIGNED", remarks: '测试用例id')
            column(name: 'url', type: "VARCHAR(255)", remarks: '附件url')
            column(name: 'file_name', type: 'VARCHAR(255)', remarks: '附件名称')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '所属项目')
            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2019-11-29-test-case-attachment-add-index', author: 'fuqianghuang01@gmail.com') {
        createIndex(tableName: "test_case_attachment", indexName: "idx_project_id") {
            column(name: "project_id")
        }
        createIndex(tableName: "test_case_attachment", indexName: "idx_case_id") {
            column(name: "case_id")
        }
    }
}