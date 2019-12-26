package io.choerodon.test.manager.infra.enums;

public class TestStatusType {
    private TestStatusType() {
    }

    public static final String STATUS_UN_EXECUTED = "未执行";
    public static final String STATUS_TYPE_CASE = "CYCLE_CASE";
    public static final String STATUS_TYPE_CASE_STEP = "CASE_STEP";

    public enum Status{
        CASE_PASS(2L,"通过"),
        CASE_FAIL(3L,"失败"),
        STEP_NONE(649L,"无需测试"),
        STEP_FAIL(6L,"失败"),
        STEP_PASS(5L,"通过");

        private Long statusId;
        private String statusName;

        Status(Long statusId, String statusName) {
            this.statusId = statusId;
            this.statusName = statusName;
        }

        public Long getStatusId() {
            return statusId;
        }

        public void setStatusId(Long statusId) {
            this.statusId = statusId;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }
    }
}
