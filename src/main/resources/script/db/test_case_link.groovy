package script.db

/**
 * @author zhaotianxin* @since 2019/11/14
 */
databaseChangeLog(logicalFilePath: "script/db/test_case_link.groovy") {
    changeSet(author: 'zhaotianxin', id: '2019-11-14-init_table_test_case_link') {
        createTable(tableName: "test_case_link", remarks: "test case link") {
            column(name: 'link_id', type: 'BIGINT UNSIGNED', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true)
            }
            column(name: 'link_case_id', type: "BIGINT UNSIGNED", remarks: 'link case 编号')
            column(name: 'case_id', type: "BIGINT UNSIGNED", remarks: '用例编号')
            column(name: 'link_type_id', type: "BIGINT UNSIGNED", remarks: 'link type 编号')
            column(name: 'project_id', type: "BIGINT UNSIGNED", remarks: '项目编号')

            column(name: "object_version_number", type: "BIGINT UNSIGNED", defaultValue: "1")
            column(name: "created_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "creation_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "BIGINT UNSIGNED", defaultValue: "0")
            column(name: "last_update_date", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}
