package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * Created by Zenger on 2018/4/13.
 */
public class DevopsServiceReqVO {

    @ApiModelProperty(value = "环境id")
    @NotNull
    private Long envId;
    @ApiModelProperty(value = "应用服务id")
    private Long appServiceId;
    @NotNull
    @Size(min = 1, max = 64, message = "error.name.size")
    @ApiModelProperty(value = "仓库名称")
    private String name;
    @ApiModelProperty(value = "外部ip")
    private String externalIp;
    @NotNull
    @ApiModelProperty(value = "类型")
    private String type;
    @NotNull
    @ApiModelProperty(value = "端口")
    private List<PortMapVO> ports;

    @ApiModelProperty(value = "结束节点")
    private Map<String, List<EndPointPortVO>> endPoints;

    @ApiModelProperty("实例code")
    private List<String> instances;

    @ApiModelProperty(value = "标签")
    private Map<String, String> label;

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public Long getAppServiceId() {
        return appServiceId;
    }

    public void setAppServiceId(Long appServiceId) {
        this.appServiceId = appServiceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExternalIp() {
        return externalIp;
    }

    public void setExternalIp(String externalIp) {
        this.externalIp = externalIp;
    }

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }

    public List<PortMapVO> getPorts() {
        return ports;
    }

    public void setPorts(List<PortMapVO> ports) {
        this.ports = ports;
    }

    public Map<String, String> getLabel() {
        return label;
    }

    public void setLabel(Map<String, String> labels) {
        this.label = labels;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, List<EndPointPortVO>> getEndPoints() {
        return endPoints;
    }

    public void setEndPoints(Map<String, List<EndPointPortVO>> endPoints) {
        this.endPoints = endPoints;
    }
}
