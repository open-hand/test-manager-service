package script.db.groovy.test_manager_service
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-11-20-init_table_test_env_command_value') {
        createTable(tableName: "test_env_command_value") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'value', type: 'text', remarks: '内容') {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_env_command_value') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_env_command_value_s', startValue: "1")
        }
    }
}