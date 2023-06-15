package script.db.groovy.test_manager_service

databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-11-20-init_table_test_automation_history') {
        createTable(tableName: "test_automation_history") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'framework', type: 'VARCHAR(60)', remarks: '使用框架')
            column(name: 'test_status', type: 'tinyint(1)', remarks: '状态')
            column(name: 'instance_id', type: 'BIGINT UNSIGNED', remarks: '实例id')
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
            column(name: 'cycle_id', type: 'BIGINT UNSIGNED', remarks: '循环id')
            column(name: 'result_id', type: 'BIGINT UNSIGNED', remarks: '结果id')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_app_instance') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_automation_history_s', startValue: "1")
        }
    }

    changeSet(id: '2019-01-25-modify-data-type', author: 'shinan.chenX@gmail.com') {
        modifyDataType(tableName: 'test_automation_history', columnName: 'cycle_id', newDataType: "VARCHAR(255)")
        renameColumn(columnDataType: 'VARCHAR(255)', newColumnName: 'cycle_ids', oldColumnName: 'cycle_id', tableName: 'test_automation_history')
    }
}