package io.choerodon.test.manager.app.service;

public interface JsonImportService {

    /**
     * 解析mocha报告
     * @param releaseName
     * @param json
     * @return
     */
    Long importMochaReport(String releaseName, String json);

    /**
     * 解析testng报告
     * @param releaseName
     * @param json
     * @return
     */
    Long importTestNgReport(String releaseName, String json);
}
