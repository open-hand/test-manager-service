package io.choerodon.test.manager.api.vo;

/**
 * @author: 25499
 * @date: 2019/11/28 8:49
 * @description:
 */
public class ExecutionStatusVO {
    private String StatusName;
    private Long count;

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
