package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by zongw.lee@gmail.com on 20/11/2018
 */
@ModifyAudit
@VersionAudit
@Table(name = "test_env_command")
public class TestEnvCommand extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;

    private String commandType;

    private Long valueId;

    private Long instanceId;

    public enum CommandType {
        CREATE("create"), RESTART("restart");
        private String type;

        public String getTypeValue() {
            return type;
        }

        CommandType(String type) {
            this.type = type;
        }
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public TestEnvCommand(CommandType commandType, Long valueId) {
        this.commandType = commandType.type;
        this.valueId = valueId;
    }

    public TestEnvCommand() {
    }
}
