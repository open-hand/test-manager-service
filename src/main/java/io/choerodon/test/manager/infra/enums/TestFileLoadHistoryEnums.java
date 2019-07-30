package io.choerodon.test.manager.infra.enums;

import java.util.Objects;

public class TestFileLoadHistoryEnums {

    public enum Action {
        UPLOAD_ISSUE(1L), DOWNLOAD_ISSUE(2L), DOWNLOAD_CYCLE(3L), CLONE_CYCLES(4L);
        private Long actionFlag;

        public Long getTypeValue() {
            return actionFlag;
        }

        Action(Long action) {
            this.actionFlag = action;
        }
    }

    public enum Source {
        PROJECT(1L), VERSION(2L), CYCLE(3L), FOLDER(4L);
        private Long sourceFlag;

        public Long getTypeValue() {
            return sourceFlag;
        }

        Source(Long source) {
            this.sourceFlag = source;
        }
    }

    public enum Status {
        SUSPENDING(1L), SUCCESS(2L), FAILURE(3L), CANCEL(4L);
        private Long statusFlag;

        public Long getTypeValue() {
            return statusFlag;
        }

        Status(Long status) {
            this.statusFlag = status;
        }

        public static TestFileLoadHistoryEnums.Status valueOf(Long statusFlag) {
            if (statusFlag == null) {
                return null;
            }

            for (TestFileLoadHistoryEnums.Status status : values()) {
                if (Objects.equals(status.statusFlag, statusFlag)) {
                    return status;
                }
            }

            return null;
        }
    }
}
