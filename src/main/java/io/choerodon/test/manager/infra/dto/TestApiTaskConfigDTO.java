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
@Table(name = "test_api_task_config")
public class TestApiTaskConfigDTO extends AuditDomain {
    @Encrypt
    @Id
    @GeneratedValue
    @ApiModelProperty(name = "主键")
    private Long id;
    @ApiModelProperty(name = "任务配置名称")
    private String name;
    @ApiModelProperty(name = "项目id")
    private Long projectId;
    @ApiModelProperty(name = "协议类型")
    private String protocol;

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

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
