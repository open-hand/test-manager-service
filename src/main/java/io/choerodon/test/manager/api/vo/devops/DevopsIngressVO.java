package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Creator: Runge
 * Date: 2018/4/20
 * Time: 14:56
 * Description:
 */
public class DevopsIngressVO extends DevopsResourceDataInfoVO{

    @ApiModelProperty(value = "入口id")
    private Long id;
    @ApiModelProperty(value = "应用服务id")
    private Long appServiceId;
    @ApiModelProperty(value = "域名")
    private String domain;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "环境id")
    private Long envId;
    @ApiModelProperty(value = "环境名称")
    private String envName;
    @ApiModelProperty(value = "环境状态")
    private Boolean envStatus;
    @ApiModelProperty(value = "是否可用")
    private Boolean isUsable;
    @ApiModelProperty(value = "状态")
    private String status;
    @ApiModelProperty(value = "certId")
    private Long certId;
    @ApiModelProperty(value = "certName")
    private String certName;
    @ApiModelProperty(value = "certStatus")
    private String certStatus;
    @ApiModelProperty("域名对应的path，其中是path对象")
    private List<DevopsIngressPathVO> pathList;
    @ApiModelProperty(value = "命令类型")
    private String commandType;
    @ApiModelProperty(value = "命令状态")
    private String commandStatus;
    @ApiModelProperty(value = "错误信息")
    private String error;
    @ApiModelProperty("Annotations键值对，键不是确定值")
    private Map<String, String> annotations;
    @ApiModelProperty("实例名称数组")
    private List<String> instances;

    public DevopsIngressVO() {
        this.pathList = new ArrayList<>();
    }

    /**
     * 构造函数
     */
    public DevopsIngressVO(Long id, String domain, String name,
                           Long envId, Boolean isUsable, String envName) {
        this.envId = envId;
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.pathList = new ArrayList<>();
        this.isUsable = isUsable;
        this.envName = envName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAppServiceId() {
        return appServiceId;
    }

    public void setAppServiceId(Long appServiceId) {
        this.appServiceId = appServiceId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean getEnvStatus() {
        return envStatus;
    }

    public void setEnvStatus(Boolean envStatus) {
        this.envStatus = envStatus;
    }

    public List<DevopsIngressPathVO> getPathList() {
        return pathList;
    }

    public void setPathList(List<DevopsIngressPathVO> pathList) {
        this.pathList = pathList;
    }

    public DevopsIngressPathVO queryLastDevopsIngressPathDTO() {
        Integer size = pathList.size();
        return size == 0 ? null : pathList.get(size - 1);
    }

    public void addDevopsIngressPathDTO(DevopsIngressPathVO devopsIngressPathVO) {
        this.pathList.add(devopsIngressPathVO);
    }

    public Long getEnvId() {
        return envId;
    }

    public void setEnvId(Long envId) {
        this.envId = envId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnvName() {
        return envName;
    }

    public void setEnvName(String envName) {
        this.envName = envName;
    }

    public Boolean getUsable() {
        return isUsable;
    }

    public void setUsable(Boolean usable) {
        isUsable = usable;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCertId() {
        return certId;
    }

    public void setCertId(Long certId) {
        this.certId = certId;
    }

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public String getCertStatus() {
        return certStatus;
    }

    public void setCertStatus(String certStatus) {
        this.certStatus = certStatus;
    }

    public String getCommandType() {
        return commandType;
    }

    public void setCommandType(String commandType) {
        this.commandType = commandType;
    }

    public String getCommandStatus() {
        return commandStatus;
    }

    public void setCommandStatus(String commandStatus) {
        this.commandStatus = commandStatus;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    public List<String> getInstances() {
        return instances;
    }

    public void setInstances(List<String> instances) {
        this.instances = instances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DevopsIngressVO that = (DevopsIngressVO) o;
        return Objects.equals(domain, that.domain)
                && Objects.equals(name, that.name)
                && Objects.equals(envId, that.envId)
                && Objects.equals(pathList, that.pathList)
                && Objects.equals(certId, that.certId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, name, envId, pathList);
    }
}
