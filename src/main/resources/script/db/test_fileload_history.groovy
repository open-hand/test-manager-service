package script.db

databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_fileload') {
        createTable(tableName: "test_fileload_history") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id'){
                constraints(nullable: false)
            }

            column(name: 'action_type', type: 'tinyint(1)', remarks: '操作类型') {
                constraints(nullable: false)
            }
            column(name: 'source_type', type: 'tinyint(1)', remarks: '操作节点') {
                constraints(nullable: false)
            }
            column(name: 'linked_id', type: 'BIGINT UNSIGNED', remarks: '节点id'){
                constraints(nullable: false)
            }

            column(name: 'file_url', type: 'VARCHAR(300)', remarks: 'url')

            column(name: 'status', type: 'tinyint(1)', remarks: '状态'){
                constraints(nullable: false)
            }
            column(name: 'successful_count', type: 'BIGINT UNSIGNED', remarks: '成功数')
            column(name: 'failed_count', type: 'BIGINT UNSIGNED', remarks: '失败数')
            column(name: 'file_stream', type: 'text', remarks: '失败文件')


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_fileload_history", indexName: "idx_linked_id") {
            column(name: "project_id")
            column(name: "action_type")
            column(name: "source_type")
            column(name: "linked_id")
        }
    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_table_test_fileload') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'table_test_fileload_s', startValue: "1")
        }
    }
}