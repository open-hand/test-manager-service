package io.choerodon.test.manager.api.vo;

import java.util.Date;
import java.util.List;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhaotianxin
 * @since 2019/11/27
 */
public class TestPlanTreeVO {
    @ApiModelProperty(value = "计划Id")
    private Long planId;

    @ApiModelProperty(value = "计划名称")
    private String name;

    @ApiModelProperty(value = "计划描述")
    private String description;

    @ApiModelProperty(value = "管理员Id")
    private Long managerId;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "项目Id")
    private Long projectId;

    @ApiModelProperty(value = "是否自选用例")
    private Boolean isOptional;

    @ApiModelProperty(value = "是否自动同步")
    private Boolean isAutoSync;

    @ApiModelProperty(value = "初始状态")
    private String initStatus;

    @ApiModelProperty(value = "文件夹树")
    private TestTreeIssueFolderVO testTreeIssueFolderVO;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Boolean getOptional() {
        return isOptional;
    }

    public void setOptional(Boolean optional) {
        isOptional = optional;
    }

    public Boolean getAutoSync() {
        return isAutoSync;
    }

    public void setAutoSync(Boolean autoSync) {
        isAutoSync = autoSync;
    }

    public TestTreeIssueFolderVO getTestTreeIssueFolderVO() {
        return testTreeIssueFolderVO;
    }

    public void setTestTreeIssueFolderVO(TestTreeIssueFolderVO testTreeIssueFolderVO) {
        this.testTreeIssueFolderVO = testTreeIssueFolderVO;
    }

    public String getInitStatus() {
        return initStatus;
    }

    public void setInitStatus(String initStatus) {
        this.initStatus = initStatus;
    }
}
