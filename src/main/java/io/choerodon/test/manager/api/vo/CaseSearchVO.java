package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author superlee
 * @since 2020-06-18
 */
public class CaseSearchVO {

    @ApiModelProperty(value = "模糊查询内容")
    private List<String> contents;

    @ApiModelProperty(value = "查询参数")
    private SearchArgs searchArgs;

    public List<String> getContents() {
        return contents;
    }

    public void setContents(List<String> contents) {
        this.contents = contents;
    }

    public SearchArgs getSearchArgs() {
        return searchArgs;
    }

    public void setSearchArgs(SearchArgs searchArgs) {
        this.searchArgs = searchArgs;
    }

    public static class SearchArgs {

        private String summary;

        @Encrypt
        private Long executionStatus;

        @Encrypt
        private List<Long> executionStatusList;

        @Encrypt
        private Long assignUser;

        @Encrypt
        private Long previousExecuteId;

        @Encrypt
        private Long nextExecuteId;

        @Encrypt
        private Long priorityId;

        @Encrypt
        private List<Long> priorityIdList;

        private String customNum;

        public Long getPriorityId() {
            return priorityId;
        }

        public void setPriorityId(Long priorityId) {
            this.priorityId = priorityId;
        }

        public List<Long> getPriorityIdList() {
            return priorityIdList;
        }

        public void setPriorityIdList(List<Long> priorityIdList) {
            this.priorityIdList = priorityIdList;
        }

        public Long getPreviousExecuteId() {
            return previousExecuteId;
        }

        public void setPreviousExecuteId(Long previousExecuteId) {
            this.previousExecuteId = previousExecuteId;
        }

        public Long getNextExecuteId() {
            return nextExecuteId;
        }

        public void setNextExecuteId(Long nextExecuteId) {
            this.nextExecuteId = nextExecuteId;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public Long getExecutionStatus() {
            return executionStatus;
        }

        public void setExecutionStatus(Long executionStatus) {
            this.executionStatus = executionStatus;
        }

        public List<Long> getExecutionStatusList() {
            return executionStatusList;
        }

        public void setExecutionStatusList(List<Long> executionStatusList) {
            this.executionStatusList = executionStatusList;
        }

        public Long getAssignUser() {
            return assignUser;
        }

        public void setAssignUser(Long assignUser) {
            this.assignUser = assignUser;
        }

        public String getCustomNum() {
            return customNum;
        }

        public void setCustomNum(String customNum) {
            this.customNum = customNum;
        }
    }
}
