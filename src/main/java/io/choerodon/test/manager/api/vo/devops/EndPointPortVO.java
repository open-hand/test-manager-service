package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

public class EndPointPortVO {
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "端口")
    private int port;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
