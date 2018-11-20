package script.db
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-11-20-init_test_app_instance_log') {
        createTable(tableName: "test_app_instance_log") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'log', type: 'text', remarks: '编码'){
                constraints(nullable: true)
            }
        }

    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_app_instance_log') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_app_instance_log_s', startValue: "1")
        }
    }
}