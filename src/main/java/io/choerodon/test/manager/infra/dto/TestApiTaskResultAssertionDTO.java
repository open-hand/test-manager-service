package io.choerodon.test.manager.infra.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ModifyAudit
@VersionAudit
@Table(name = "test_api_task_result_assertion")
public class
TestApiTaskResultAssertionDTO extends AuditDomain {
    @Encrypt
    @Id
    @GeneratedValue
    @ApiModelProperty(name = "主键")
    private Long id;

    @ApiModelProperty(name = "断言名称")
    private String name;
    @ApiModelProperty(name = "项目id")
    private Long projectId;
    @Encrypt
    @ApiModelProperty(name = "所属执行结果id")
    private Long resultId;
    @ApiModelProperty(name = "断言执行状态")
    private String status;
    @ApiModelProperty(name = "断言失败原因")
    private String reason;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
