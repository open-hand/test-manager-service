package io.choerodon.test.manager.infra.enums;

/**
 * @author zhaotianxin
 * @since 2019/11/26
 */
public enum TestPlanStatus {
    DOING("doing"),
    DONE("done"),
    TODO("todo");
    private String status;
     TestPlanStatus(String status) { this.status =status;}

    public String getStatus() {
        return status;
    }
}
