package script.db
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
changeSet(id: '2018-08-13-add-table-saga_task_instance_record', author: 'jialong.zuo') {
    createTable(tableName: "saga_task_instance_record") {
        column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: false, remarks: '消息id') {
            constraints(primaryKey: true)
        }
        column(name: 'create_time', type: 'BIGINT UNSIGNED', remarks: '创建时间戳')
    }
}
}