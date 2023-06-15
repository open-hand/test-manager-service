package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author: 25499
 * @date: 2019/11/28 8:49
 * @description:
 */
public class ExecutionStatusVO {
    @ApiModelProperty(value = "总数")
    private Long total;

    @ApiModelProperty(value = "用例状态列表")
    private List<TestStatusVO> statusVOList;

    public ExecutionStatusVO(Long total, List<TestStatusVO> statusVOList) {
        this.total = total;
        this.statusVOList = statusVOList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<TestStatusVO> getStatusVOList() {
        return statusVOList;
    }

    public void setStatusVOList(List<TestStatusVO> statusVOList) {
        this.statusVOList = statusVOList;
    }
}
