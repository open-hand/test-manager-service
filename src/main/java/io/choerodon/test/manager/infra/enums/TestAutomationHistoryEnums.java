package io.choerodon.test.manager.infra.enums;

public class TestAutomationHistoryEnums {
    private TestAutomationHistoryEnums() {
    }

    public enum Status {
        NONEXECUTION(0L), COMPLETE(1L), PARTIALEXECUTION(2L);
        private Long testStatus;

        public Long getStatus() {
            return testStatus;
        }

        Status(Long status) {
            this.testStatus = status;
        }
    }

}
