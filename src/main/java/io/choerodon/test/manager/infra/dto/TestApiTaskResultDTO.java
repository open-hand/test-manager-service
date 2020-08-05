package io.choerodon.test.manager.infra.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ModifyAudit
@VersionAudit
@Table(name = "test_api_task_result")
public class TestApiTaskResultDTO extends AuditDomain {

    @Encrypt
    @Id
    @GeneratedValue
    @ApiModelProperty(name = "主键")
    private Long id;

    @ApiModelProperty(name = "实例名称")
    private String name;
    @ApiModelProperty(name = "项目id")
    private Long projectId;
    @Encrypt
    @ApiModelProperty(name = "所属执行记录id")
    private Long recordId;
    @ApiModelProperty(name = "执行方法")
    private String method;
    @ApiModelProperty(name = "路径")
    private String path;
    @ApiModelProperty(name = "执行状态")
    private String status;

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

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
