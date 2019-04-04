package io.choerodon.test.manager.api.dto.testng;

import java.util.List;

/*
 * @author shinan.chen
 * @since 2019/1/21
 */
public class TestNgResult {
    private Long skipped;
    private Long failed;
    private Long ignored;
    private Long total;
    private Long passed;
    private List<TestNgSuite> suites;

    public Long getSkipped() {
        return skipped;
    }

    public void setSkipped(Long skipped) {
        this.skipped = skipped;
    }

    public Long getFailed() {
        return failed;
    }

    public void setFailed(Long failed) {
        this.failed = failed;
    }

    public Long getIgnored() {
        return ignored;
    }

    public void setIgnored(Long ignored) {
        this.ignored = ignored;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPassed() {
        return passed;
    }

    public void setPassed(Long passed) {
        this.passed = passed;
    }

    public List<TestNgSuite> getSuites() {
        return suites;
    }

    public void setSuites(List<TestNgSuite> suites) {
        this.suites = suites;
    }
}
