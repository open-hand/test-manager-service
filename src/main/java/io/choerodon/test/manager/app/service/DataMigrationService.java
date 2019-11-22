package io.choerodon.test.manager.app.service;

/**
 * 重构，数据迁移专用
 */
public interface DataMigrationService {

    void fixData();
    /**
     * 迁移用例
     */
    void migrateIssue();

    /**
     * 迁移附件
     */
    void migrateAttachment();

    void migrateLink();

    void migrateLabel();

    void migrateLabelCaseRel();

    void migrateFolder();

    void migrateProject();

}
