package io.choerodon.test.manager.api.vo.event;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

/**
 * @author huaxin.deng@hand-china.com
 * @since 2022/6/22
 */
public class CloneIssueEvent {

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("key:newIssueId, value:oldIssueId")
    private Map<Long, Long> newIssueIdMap;

    @ApiModelProperty("关联内容")
    private List<String> linkContents;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Map<Long, Long> getNewIssueIdMap() {
        return newIssueIdMap;
    }

    public void setNewIssueIdMap(Map<Long, Long> newIssueIdMap) {
        this.newIssueIdMap = newIssueIdMap;
    }

    public List<String> getLinkContents() {
        return linkContents;
    }

    public void setLinkContents(List<String> linkContents) {
        this.linkContents = linkContents;
    }
}
