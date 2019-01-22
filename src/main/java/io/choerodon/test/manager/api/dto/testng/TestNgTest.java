package io.choerodon.test.manager.api.dto.testng;

import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/1/21
 */
public class TestNgTest {
    private String name;
    private String startedAt;
    private String finishedAt;
    private Long durationMs;
    private List<TestNgCase> cases;

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

    public List<TestNgCase> getCases() {
        return cases;
    }

    public void setCases(List<TestNgCase> cases) {
        this.cases = cases;
    }
}
