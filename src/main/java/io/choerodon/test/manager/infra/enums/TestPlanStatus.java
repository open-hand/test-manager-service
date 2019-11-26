package io.choerodon.test.manager.infra.enums;

/**
 * @author zhaotianxin
 * @since 2019/11/26
 */
public enum TestPlanStatus {
    PROCESSING("processing"),
    COMPLETED("completed"),
    NOTSTARTED("not_started");
    private String status;
     TestPlanStatus(String status) { this.status =status;}

    public String getStatus() {
        return status;
    }
}
