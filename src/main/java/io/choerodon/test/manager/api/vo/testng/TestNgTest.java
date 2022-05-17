package io.choerodon.test.manager.api.vo.testng;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/*
 * @author shinan.chen
 * @since 2019/1/21
 */
public class TestNgTest {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "开始时间")
    private String startedAt;
    @ApiModelProperty(value = "结束时间")
    private String finishedAt;
    @ApiModelProperty(value = "持续时间")
    private Long durationMs;
    @ApiModelProperty(value = "用例")
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
