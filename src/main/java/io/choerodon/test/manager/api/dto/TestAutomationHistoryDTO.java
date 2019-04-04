package io.choerodon.test.manager.api.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.mybatis.domain.AuditDomain;

public class TestAutomationHistoryDTO extends AuditDomain {

    public enum Status {
        NONEXECUTION(0L), COMPLETE(1L), PARTIALEXECUTION(2L);
        private Long testStatus;

        public Long getStatus() {
            return testStatus;
        }

        Status(Long status) {
            this.testStatus = status;
        }
    }

    @Id
    @GeneratedValue
    @ApiModelProperty(value = "主键id")
    private Long id;

    @ApiModelProperty(value = "测试框架")
    private String framework;

    @ApiModelProperty(value = "测试状态")
    private Long testStatus;

    @ApiModelProperty(value = "实例ID")
    private Long instanceId;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "映射的循环IDs")
    private String cycleIds;

    @ApiModelProperty(value = "测试结果日志ID")
    private Long resultId;

    @ApiModelProperty(value = "实例DTO")
    private TestAppInstanceDTO testAppInstanceDTO;

    @ApiModelProperty(value = "创建人详情")
    private UserDO createUser;

    @ApiModelProperty(value = "是否映射了多循环")
    private Boolean isMoreCycle;

    @ApiModelProperty(value = "循环详情DTOList")
    private List<TestCycleDTO> cycleDTOS;

    public Boolean getMoreCycle() {
        return isMoreCycle;
    }

    public void setMoreCycle(Boolean moreCycle) {
        isMoreCycle = moreCycle;
    }

    public List<TestCycleDTO> getCycleDTOS() {
        return cycleDTOS;
    }

    public void setCycleDTOS(List<TestCycleDTO> cycleDTOS) {
        this.cycleDTOS = cycleDTOS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getCycleIds() {
        return cycleIds;
    }

    public void setCycleIds(String cycleIds) {
        this.cycleIds = cycleIds;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(Status status) {
        this.testStatus = status.getStatus();
    }

    public void setTestStatus(Long testStatus) {
        this.testStatus = testStatus;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public UserDO getCreateUser() {
        return createUser;
    }

    public void setCreateUser(UserDO createUser) {
        this.createUser = createUser;
    }

    public TestAppInstanceDTO getTestAppInstanceDTO() {
        return testAppInstanceDTO;
    }

    public void setTestAppInstanceDTO(TestAppInstanceDTO testAppInstanceDTO) {
        this.testAppInstanceDTO = testAppInstanceDTO;
    }
}
