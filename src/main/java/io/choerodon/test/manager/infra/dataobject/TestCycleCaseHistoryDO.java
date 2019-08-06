package io.choerodon.test.manager.infra.dataobject;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Table(name = "test_cycle_case_history")
public class TestCycleCaseHistoryDO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long executeId;
    private String oldValue;
    private String newValue;
    private String field;

    private Long lastUpdatedBy;

    @Override
    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    @Override
    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Long getExecuteId() {
        return executeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
}
