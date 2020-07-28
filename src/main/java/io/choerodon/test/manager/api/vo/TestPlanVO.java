package io.choerodon.test.manager.api.vo;

import java.util.Date;
import java.util.Map;
import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @since 2019/11/26
 */
public class TestPlanVO {

    @ApiModelProperty(value = "计划Id")
    @Encrypt
    private Long planId;

    @ApiModelProperty(value = "计划名称")
    private String name;

    @ApiModelProperty(value = "计划描述")
    private String description;

    @ApiModelProperty(value = "管理员Id")
    @Encrypt
    private Long managerId;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "项目Id")
    private Long projectId;

    @ApiModelProperty(value = "是否自选用例")
    private Boolean custom;

    @ApiModelProperty(value = "是否自动同步")
    private Boolean isAutoSync;

    @ApiModelProperty(value = "选中的用例是否有改变")
    private Boolean caseHasChange;

    @Encrypt
    private Map<Long, CaseSelectVO> caseSelected;

    private Long objectVersionNumber;

    private UserDO managerUser;

    private String initStatus;

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


    public Boolean getAutoSync() {
        return isAutoSync;
    }

    public void setAutoSync(Boolean autoSync) {
        isAutoSync = autoSync;
    }

    public Map<Long, CaseSelectVO> getCaseSelected() {
        return caseSelected;
    }

    public void setCaseSelected(Map<Long, CaseSelectVO> caseSelected) {
        this.caseSelected = caseSelected;
    }

    public Boolean getCustom() {
        return custom;
    }

    public void setCustom(Boolean custom) {
        this.custom = custom;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Boolean getCaseHasChange() {
        return caseHasChange;
    }

    public void setCaseHasChange(Boolean caseHasChange) {
        this.caseHasChange = caseHasChange;
    }

    public UserDO getManagerUser() {
        return managerUser;
    }

    public void setManagerUser(UserDO managerUser) {
        this.managerUser = managerUser;
    }

    public String getInitStatus() {
        return initStatus;
    }

    public void setInitStatus(String initStatus) {
        this.initStatus = initStatus;
    }
}
