package io.choerodon.test.manager.api.vo.testng;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/*
 * @author shinan.chen
 * @since 2019/1/21
 */
public class TestNgSuite {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "开始时间")
    private String startedAt;
    @ApiModelProperty(value = "结束时间")
    private String finishedAt;
    @ApiModelProperty(value = "持续时间")
    private Long durationMs;
    @ApiModelProperty(value = "测试")
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
