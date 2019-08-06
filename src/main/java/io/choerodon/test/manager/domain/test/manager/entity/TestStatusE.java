package io.choerodon.test.manager.domain.test.manager.entity;


import io.choerodon.test.manager.domain.repository.TestStatusRepository;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@Component
@Scope("prototype")
public class TestStatusE {

    public static final String STATUS_UN_EXECUTED = "未执行";

    public static final String STATUS_TYPE_CASE = "CYCLE_CASE";

    public static final String STATUS_TYPE_CASE_STEP = "CASE_STEP";

    private Long statusId;

    private String statusName;

    private String description;

    private String statusColor;

    private String statusType;

    private Long objectVersionNumber;

    private Long projectId;


    @Autowired
    TestStatusRepository testStatusRepository;

    public TestStatusE queryOne() {
        return testStatusRepository.queryOne(statusId);
    }

    public TestStatusE queryOneSelective() {
        return testStatusRepository.query(this);
    }

    public List<TestStatusE> queryAllUnderProject() {
        return testStatusRepository.queryAllUnderProject(this);
    }

    public TestStatusE addSelf() {
        return testStatusRepository.insert(this);
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public TestStatusE updateSelf() {
        return testStatusRepository.update(this);
    }

    public void deleteSelf() {
        testStatusRepository.delete(this);
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
