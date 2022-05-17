package io.choerodon.test.manager.api.vo.agile;

import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Created by jian_zhang02@163.com on 2018/5/14.
 */

public class ProductVersionPageDTO {
    @ApiModelProperty(value = "版本id")
    private Long versionId;
    @ApiModelProperty(value = "版本名称")
    private String name;
    @ApiModelProperty(value = "版本描述")
    private String description;
    @ApiModelProperty(value = "开始日期")
    private Date startDate;
    @ApiModelProperty(value = "发布日期")
    private Date releaseDate;
    @ApiModelProperty(value = "状态编码")
    private String statusCode;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "项目id")
    private Long projectId;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;
    @ApiModelProperty(value = "计划文件夹列表")
    @Transient
    private List<TestCycleVO> testCycleVOS;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<TestCycleVO> getTestCycleVOS() {
        return testCycleVOS;
    }

    public void setTestCycleVOS(List<TestCycleVO> testCycleVOS) {
        this.testCycleVOS = testCycleVOS;
    }
}
