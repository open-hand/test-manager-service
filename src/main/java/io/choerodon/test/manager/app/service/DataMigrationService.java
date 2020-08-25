package io.choerodon.test.manager.app.service;

/**
 * 重构，数据迁移专用
 */
public interface DataMigrationService {

    void fixData();

    /**
     * 修复测试用例优先级
     */
    void fixDataTestCasePriority();

}
