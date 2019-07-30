package io.choerodon.test.manager.api.vo.testng;

import java.util.List;

/*
 * @author shinan.chen
 * @since 2019/1/21
 */
public class TestNgSuite {
    private String name;
    private String startedAt;
    private String finishedAt;
    private Long durationMs;
    private List<TestNgTest> tests;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(String finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public List<TestNgTest> getTests() {
        return tests;
    }

    public void setTests(List<TestNgTest> tests) {
        this.tests = tests;
    }
}
