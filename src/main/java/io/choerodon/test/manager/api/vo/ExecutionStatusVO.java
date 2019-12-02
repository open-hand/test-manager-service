package io.choerodon.test.manager.api.vo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: 25499
 * @date: 2019/11/28 8:49
 * @description:
 */
public class ExecutionStatusVO {
    private AtomicInteger total;

    private List<TestStatusVO> statusVOList;

    public ExecutionStatusVO(AtomicInteger total, List<TestStatusVO> statusVOList) {
        this.total = total;
        this.statusVOList = statusVOList;
    }

    public AtomicInteger getTotal() {
        return total;
    }

    public void setTotal(AtomicInteger total) {
        this.total = total;
    }

    public List<TestStatusVO> getStatusVOList() {
        return statusVOList;
    }

    public void setStatusVOList(List<TestStatusVO> statusVOList) {
        this.statusVOList = statusVOList;
    }
}
