package io.choerodon.test.manager.api.vo.agile;

import io.choerodon.test.manager.infra.util.StringUtil;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/7/6
 */
public class CopyConditionDTO {

    private String summary;

    private Boolean subTask;

    private Boolean issueLink;

    private Boolean sprintValues;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getSubTask() {
        return subTask;
    }

    public void setSubTask(Boolean subTask) {
        this.subTask = subTask;
    }

    public Boolean getIssueLink() {
        return issueLink;
    }

    public void setIssueLink(Boolean issueLink) {
        this.issueLink = issueLink;
    }

    public Boolean getSprintValues() {
        return sprintValues;
    }

    public void setSprintValues(Boolean sprintValues) {
        this.sprintValues = sprintValues;
    }

    @Override
    public String toString() {
        return StringUtil.getToString(this);
    }
}
