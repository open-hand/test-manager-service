package io.choerodon.test.manager.app.service;

/**
 * 重构，数据迁移专用
 */
public interface DataMigrationService {

    /**
     * 迁移用例
     */
    void migrateIssue();

    /**
     * 迁移附件
     */
    void migrateAttachment();

    void migrateLink();
}
