package io.choerodon.test.manager.infra.dto;

import javax.persistence.*;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@VersionAudit
@ModifyAudit
@Table(name = "test_status")
public class TestStatusDTO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long statusId;

    private String statusName;

    private String description;

    private String statusColor;

    private String statusType;

    private Long projectId;
    @Transient
    private Long count;

    public TestStatusDTO() {
    }

    public TestStatusDTO(String statusColor, String statusType, Long projectId, String statusName) {
        this.statusColor = statusColor;
        this.statusType = statusType;
        this.projectId = projectId;
        this.statusName = statusName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatusColor() {
        return statusColor;
    }

    public void setStatusColor(String statusColor) {
        this.statusColor = statusColor;
    }

    public String getStatusType() {
        return statusType;
    }

    public void setStatusType(String statusType) {
        this.statusType = statusType;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
