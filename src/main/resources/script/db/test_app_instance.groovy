package script.db
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-11-20-init_table_test_app_instance') {
        createTable(tableName: "test_app_instance") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'code', type: 'VARCHAR(64)', remarks: '编码')
            column(name: 'app_id', type: 'BIGINT UNSIGNED', remarks: '应用Id')
            column(name: 'app_version_id', type: 'BIGINT UNSIGNED', remarks: '应用版本Id')
            column(name: 'project_version_id', type: 'BIGINT UNSIGNED', remarks: '版本Id')
            column(name: 'env_id', type: 'BIGINT UNSIGNED', remarks: '实例运行的环境ID')
            column(name: 'command_id', type: 'BIGINT UNSIGNED', remarks: 'command_id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: 'project_id')
            column(name: 'pod_status', type: 'tinyint(1)', remarks: 'pod状态')
            column(name: 'pod_name', type: 'VARCHAR(64)', remarks: '运行的Pod名')
            column(name: 'container_name', type: 'VARCHAR(64)', remarks: '运行的容器名')
            column(name: 'log_id', type: 'BIGINT UNSIGNED', remarks: 'log_id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(indexName: 'uk_code', tableName: 'test_app_instance', unique: true) {
            column(name: 'code')
        }
    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-11-21-add_sequence_test_app_instance') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_app_instance_s', startValue: "1")
        }
    }
}