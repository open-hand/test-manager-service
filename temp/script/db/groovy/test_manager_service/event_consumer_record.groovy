package script.db.groovy.test_manager_service

databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(id: '2018-07-18-add-table-event-consumer-record', author: 'WangZhe') {
        createTable(tableName: "event_consumer_record") {
            column(name: 'uuid', type: 'VARCHAR(50)', autoIncrement: false, remarks: 'uuid') {
                constraints(primaryKey: true)
            }
            column(name: 'create_time', type: 'DATETIME', remarks: '创建时间')
        }
    }

    changeSet(id: '2018-07-18-add-table-event-producer-record', author: 'WangZhe') {
        createTable(tableName: "event_producer_record") {
            column(name: 'uuid', type: 'VARCHAR(50)', autoIncrement: false, remarks: 'uuid') {
                constraints(primaryKey: true)
            }
            column(name: 'type', type: 'VARCHAR(50)', remarks: '业务类型') {
                constraints(nullable: false)
            }
            column(name: 'create_time', type: 'DATETIME', remarks: '创建时间')
        }
    }
}