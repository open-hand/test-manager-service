package script.db.groovy.test_manager_service

databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_case') {
        createTable(tableName: "test_cycle_case") {
            column(name: 'execute_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'cycle_id', type: 'BIGINT UNSIGNED', remarks: '测试循环id') {
                constraints(nullable: false)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '测试issue id') {
                constraints(nullable: false)
            }
            column(name: 'rank', type: 'VARCHAR(25)', remarks: 'rank') {
                constraints(nullable: false)
            }
            column(name: 'execution_status', type: 'BIGINT UNSIGNED', remarks: '执行状态') {
                constraints(nullable: false)
            }
            column(name: 'assigned_to', type: 'BIGINT UNSIGNED', defaultValue: "0", remarks: '指定人')
            column(name: 'comment', type: 'text', remarks: '注释')


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_cycle_case", indexName: "idx_cycle_case_cycle_id") {
            column(name: "cycle_id")
        }
        createIndex(tableName: "test_cycle_case", indexName: "idx_cycle_case_issue_id") {
            column(name: "issue_id")
        }
        createIndex(indexName: 'uk_cycle_id_rank', tableName: 'test_cycle_case', unique: true) {
            column(name: 'cycle_id')
            column(name: 'rank')
        }
    }
    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-add_sequence_test_cycle_case') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'test_cycle_case_s', startValue: "1")
        }
    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-09-18-change-column-comment') {
        renameColumn(columnDataType:'text',newColumnName:'description',oldColumnName:'comment',tableName:"test_cycle_case")
    }

    changeSet(id: '2019-05-10-add-column-test-cycle-case-project-id', author: 'shinan.chenX@gmail') {
        addColumn(tableName: 'test_cycle_case') {
            column(name: 'project_id', type: 'BIGINT UNSIGNED', remarks: '项目id')
        }
        createIndex(tableName: "test_cycle_case", indexName: "idx_cycle_case_project_id") {
            column(name: "project_id")
        }
    }
    changeSet(author: 'zhaotianxin', id: '2019-11-27-rename-colume-version-num') {
        addColumn(tableName: 'test_cycle_case') {
            column(name: 'version_num', type: 'BIGINT UNSIGNED', remarks: 'version num')
        }
    }
    changeSet(author: 'lizhaozhong', id: '2019-11-27-add-colume-summary') {
        addColumn(tableName: 'test_cycle_case') {
            column(name: 'summary', type: 'VARCHAR(255)', remarks: '摘要')
        }
    }

    changeSet(author: 'lizhaozhong', id: '2019-11-28-rename-colume-issueId') {
        renameColumn(columnDataType:'BIGINT UNSIGNED',newColumnName:'case_id',oldColumnName:'issue_id',tableName:"test_cycle_case")
    }

    changeSet(author: 'lizhaozhong', id: '2019-11-29-add-colume-source') {
        addColumn(tableName: 'test_cycle_case') {
            column(name: 'source', type: 'VARCHAR(255)', remarks: '来源')
        }
    }
    changeSet(author: 'lizhaozhong@hang-china.com', id: '2019-12-04-add_sequence_version_step_num') {
        addColumn(tableName: 'test_cycle_case') {
            column(name: 'version_step_num', type: 'BIGINT UNSIGNED', remarks: '步骤版本号',defaultValue: "1")
        }
    }

    changeSet(id: '2019-12-11-test-cycle_drop_uk_cycle_id_rank', author: 'lizhaozhong') {
        dropIndex(tableName: 'test_cycle_case', indexName: 'uk_cycle_id_rank')
    }

    changeSet(id: '2020-08-24-test-cycle-case-add-column-priority-id', author: 'jiaxu.cui@gmail.com') {
        addColumn(tableName: 'test_cycle_case') {
            column(name: 'priority_id', type: 'BIGINT UNSIGNED', remarks: 'priority id'){
                constraints(nullable: false)
            }
        }
    }
}