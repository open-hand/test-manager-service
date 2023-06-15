package script.db.groovy.test_manager_service

databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_defect_rel') {
        createTable(tableName: "test_cycle_case_attachment_rel", remarks: '循环测试用例附件表') {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'attachment_type', type: 'VARCHAR(30)', remarks: '附件类型') {
                constraints(nullable: false)
            }
            column(name: 'attachment_link_id', type: 'BIGINT UNSIGNED', remarks: '附件关联Id') {
                constraints(nullable: false)
            }
            column(name: 'attachment_name', type: 'VARCHAR(30)', remarks: "附件名") {
                constraints(nullable: false)
            }
            column(name: 'url', type: 'VARCHAR(300)', remarks: "附件url") {
                constraints(nullable: false)
            }
            column(name: 'comment', type: 'text', remarks: "评论")


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(tableName: "test_cycle_case_attachment_rel", indexName: "idx_attachment_link_id") {
            column(name: "attachment_link_id")
        }
    }

    changeSet(id:'2018-07-23-add-attach-key',author: 'jialong.zuo'){
        createIndex(indexName: 'uk_test_attachment', tableName: 'test_cycle_case_attachment_rel', unique: true) {
            column(name: 'attachment_type')
            column(name: 'attachment_link_id')
            column(name: 'attachment_name')
        }
    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_cycle_case_attachment_rel') {

        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_cycle_case_attach_rel_s', startValue: "1")
        }
    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-update-table-name') {
        renameTable(newTableName:'test_cycle_case_attach_rel',oldTableName:'test_cycle_case_attachment_rel')

    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-change-attach-column-comment') {
        renameColumn(columnDataType:'text',newColumnName:'description',oldColumnName:'comment',tableName:"test_cycle_case_attach_rel")
    }

    changeSet(id: '2019-08-30-modify-column-type', author: 'fuqianghuang01@gmail.com') {
        modifyDataType(tableName: 'test_cycle_case_attach_rel', columnName: 'attachment_name', newDataType: "VARCHAR(255)")
    }

    changeSet(id: '2019-11-29-test-cycle-case-attach-rel-drop-index', author: 'zhaotianxin') {
        dropIndex(tableName: 'test_cycle_case_attach_rel', indexName: 'uk_test_attachment')
    }

    changeSet(id:'2019-12-2-add-attach-key',author: 'zhaotianxin'){
        createIndex(indexName: 'idx_attachment_type', tableName: 'test_cycle_case_attach_rel') {
            column(name: 'attachment_type')
        }
    }

}