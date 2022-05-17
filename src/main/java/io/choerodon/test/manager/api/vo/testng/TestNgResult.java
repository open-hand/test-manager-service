package io.choerodon.test.manager.api.vo.testng;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/*
 * @author shinan.chen
 * @since 2019/1/21
 */
public class TestNgResult {
    @ApiModelProperty(value = "跳过个数")
    private Long skipped;
    @ApiModelProperty(value = "失败个数")
    private Long failed;
    @ApiModelProperty(value = "忽略个数")
    private Long ignored;
    @ApiModelProperty(value = "总数")
    private Long total;
    @ApiModelProperty(value = "通过个数")
    private Long passed;
    @ApiModelProperty(value = "suites")
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
