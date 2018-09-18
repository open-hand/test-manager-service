package script.db
databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle') {
    createTable(tableName: "test_cycle") {
        column(name: 'cycle_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
            constraints(primaryKey: true)
        }
        column(name: 'parent_cycle_id', type: 'BIGINT UNSIGNED', remarks: '上级目录id')
        column(name: 'cycle_name', type: 'VARCHAR(100)', remarks: '循环名称') {
            constraints(nullable: false)
        }
        column(name: 'version_id', type: 'BIGINT UNSIGNED', remarks: '版本')
        column(name: 'description', type: 'VARCHAR(300)', remarks: '描述')
        column(name: 'build', type: 'VARCHAR(300)', remarks: 'build')
        column(name: 'environment', type: 'VARCHAR(300)', remarks: '环境')
        column(name: 'from_date', type: 'DATETIME', remarks: '起始时间')
        column(name: 'to_date', type: 'DATETIME', remarks: '结束时间')
        column(name: 'type', type: 'VARCHAR(30)', remarks: '类型')


        column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
        column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
        column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
        column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
    }
    createIndex(tableName: "test_cycle", indexName: "idx_cycle_parent_cycle_id") {
        column(name: "parent_cycle_id")
    }
    createIndex(tableName: "test_cycle", indexName: "idx_cycle_name") {
        column(name: "cycle_name")
    }
    createIndex(indexName: 'uk_cycle_name_version_id', tableName: 'test_cycle', unique: true) {
        column(name: 'version_id')
        column(name: 'parent_cycle_id')
        column(name: 'cycle_name')
    }
}

    changeSet(id:'2018-08-27-defect-add-column-folderId',author: 'zongwei.li'){
        addColumn(tableName:'test_cycle'){
            column(name:'folder_id',type:'BIGINT UNSIGNED', defaultValue: '0')
        }
    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_cycle') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_cycle_s', startValue: "1")
        }
    }
}