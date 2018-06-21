package script.db

databaseChangeLog(logicalFilePath: 'script/script/init_tables.groovy.groovy') {

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_case_step') {
        createTable(tableName: "test_case_step") {
            column(name: 'step_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'rank', type: 'VARCHAR(25)', remarks: 'rank') {
                constraints(nullable: false)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '所属issue') {
                constraints(nullable: false)
            }
            column(name: 'test_step', type: 'VARCHAR(300)', remarks: '测试步骤')
            column(name: 'test_data', type: 'VARCHAR(300)', remarks: '测试数据')
            column(name: 'expected_result', type: 'VARCHAR(300)', remarks: '期待结果')


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_case_step", indexName: "idx_case_step_issue_id") {
            column(name: "issue_id")
        }
        createIndex(indexName: 'uk_issue_id_rank', tableName: 'test_case_step', unique: true) {
            column(name: 'issue_id')
            column(name: 'rank')
        }
    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle') {
        createTable(tableName: "test_cycle") {
            column(name: 'cycle_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'parent_cycle_id', type: 'BIGINT UNSIGNED', remarks: '上级目录id')
            column(name: 'cycle_name', type: 'VARCHAR(300)', remarks: '循环名称') {
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
            column(name: 'cycle_name')
        }

    }

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
            column(name: 'execution_status', type: 'VARCHAR(30)', remarks: '执行状态') {
                constraints(nullable: false)
            }
            column(name: 'assigned_to', type: 'VARCHAR(100)', remarks: '指定人')
            column(name: 'comment', type: 'VARCHAR(300)', remarks: '注释')


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

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_case_step') {
        createTable(tableName: "test_cycle_case_step") {
            column(name: 'execute_step_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'execute_id', type: 'BIGINT UNSIGNED', remarks: '测试循环id') {
                constraints(nullable: false)
            }
            column(name: 'step_id', type: 'BIGINT UNSIGNED', remarks: '测试issue布 id') {
                constraints(nullable: false)
            }
            column(name: 'comment', type: 'VARCHAR(300)', remarks: '描述')
            column(name: 'step_status', type: 'VARCHAR(30)', remarks: '状态')


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_cycle_case_step", indexName: "idx_cycle_step_execute_id") {
            column(name: "execute_id")
        }
    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_case_history') {
        createTable(tableName: "test_cycle_case_history") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'execute_id', type: "BIGINT UNSIGNED") {
                constraints(primaryKey: true)
            }
            column(name: 'old_value', type: 'VARCHAR(300)', remarks: '旧值') {
                constraints(nullable: false)
            }
            column(name: 'new_value', type: 'VARCHAR(300)', remarks: '新值') {
                constraints(nullable: false)
            }


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

        createIndex(tableName: "test_cycle_case_history", indexName: "idx_case_history_execute_id") {
            column(name: "execute_id")
        }
    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_case_defect_rel') {
        createTable(tableName: "test_cycle_case_defect_rel") {
            column(name: 'id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'defect_type', type: 'VARCHAR(30)', remarks: '缺陷类型') {
                constraints(nullable: false)
            }
            column(name: 'defect_link_id', type: 'BIGINT UNSIGNED', remarks: '缺陷关联ID') {
                constraints(nullable: false)
            }
            column(name: 'issue_id', type: 'BIGINT UNSIGNED', remarks: '测试Id') {
                constraints(nullable: false)
            }


            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: "test_cycle_case_defect_rel", columnNames: "defect_type, issue_id,defect_link_id")

    }

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_cycle_defect_rel') {
        createTable(tableName: "test_cycle_case_attachment_rel") {
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
            column(name: 'comment', type: 'VARCHAR(300)', remarks: "comment")


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

    changeSet(author: 'jialongzuo@hang-china.com', id: '2018-06-8-init_table_test_status') {
        createTable(tableName: "test_status") {
            column(name: 'status_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'status_name', type: 'VARCHAR(30)', remarks: '状态名称') {
                constraints(nullable: false)
            }
            column(name: 'description', type: 'VARCHAR(300)', remarks: '附件关联Id')

            column(name: 'status_color', type: 'VARCHAR(30)', remarks: "颜色") {
                constraints(nullable: false)
            }
            column(name: 'status_type', type: 'VARCHAR(30)', remarks: "状态类型") {
                constraints(nullable: false)
            }

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        createIndex(tableName: "test_status", indexName: "idx_status_name") {
            column(name: "status_name")
        }

    }


}