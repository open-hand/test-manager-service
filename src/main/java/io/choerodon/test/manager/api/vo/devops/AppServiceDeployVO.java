package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

public class AppServiceDeployVO {
    @ApiModelProperty(value = "应用服务版本id")
    private Long appServiceVersionId;
    @ApiModelProperty(value = "环境id")
    private Long environmentId;
    @ApiModelProperty(value = "值集")
    private String values;
    @ApiModelProperty(value = "应用服务id")
    private Long appServiceId;
    @ApiModelProperty(value = "应用类型")
    private String type;
    @ApiModelProperty(value = "实例id")
    private Long instanceId;
    @ApiModelProperty(value = "命令id")
    private Long commandId;
    @ApiModelProperty(value = "实例名称")
    private String instanceName;
    @ApiModelProperty(value = "是否未变化")
    private boolean isNotChange;
    @ApiModelProperty(value = "记录id")
    private Long recordId;
    @ApiModelProperty(value = "value id")
    private Long valueId;
    @ApiModelProperty(value = "仓库")
    private DevopsServiceReqVO devopsServiceReqVO;
    @ApiModelProperty(value = "入口")
    private DevopsIngressVO devopsIngressVO;

    public AppServiceDeployVO() {
    }

    public AppServiceDeployVO(Long appServiceVersionId, Long environmentId, String values, Long appServiceId, String type, Long instanceId) {
        this.appServiceVersionId = appServiceVersionId;
        this.environmentId = environmentId;
        this.values = values;
        this.appServiceId = appServiceId;
        this.type = type;
        this.instanceId = instanceId;
        this.instanceName = "att-" + Long.toString(appServiceId, 32) + "-" + Long.toString(appServiceVersionId, 32) + "-" + Long.toString(instanceId, 32);
    }

    public AppServiceDeployVO(Long appServiceVersionId, Long environmentId, String values, Long appServiceId, String type, Long instanceId, String instanceName, Long recordId, Long valueId) {
        this.appServiceVersionId = appServiceVersionId;
        this.environmentId = environmentId;
        this.values = values;
        this.appServiceId = appServiceId;
        this.type = type;
        this.instanceId = instanceId;
        this.instanceName = instanceName;
        this.recordId = recordId;
        this.valueId = valueId;
    }

    public Long getAppServiceVersionId() {
        return appServiceVersionId;
    }

    public void setAppServiceVersionId(Long appServiceVersionId) {
        this.appServiceVersionId = appServiceVersionId;
    }

    public Long getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(Long environmentId) {
        this.environmentId = environmentId;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    public Long getAppServiceId() {
        return appServiceId;
    }

    public void setAppServiceId(Long appServiceId) {
        this.appServiceId = appServiceId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public boolean getIsNotChange() {
        return isNotChange;
    }

    public void setIsNotChange(boolean isNotChange) {
        this.isNotChange = isNotChange;
    }

    public Long getCommandId() {
        return commandId;
    }

    public void setCommandId(Long commandId) {
        this.commandId = commandId;
    }

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public DevopsServiceReqVO getDevopsServiceReqVO() {
        return devopsServiceReqVO;
    }

    public void setDevopsServiceReqVO(DevopsServiceReqVO devopsServiceReqVO) {
        this.devopsServiceReqVO = devopsServiceReqVO;
    }

    public DevopsIngressVO getDevopsIngressVO() {
        return devopsIngressVO;
    }

    public void setDevopsIngressVO(DevopsIngressVO devopsIngressVO) {
        this.devopsIngressVO = devopsIngressVO;
    }
}
