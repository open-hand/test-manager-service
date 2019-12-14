package io.choerodon.test.manager.api.vo.event;

/**
 * Created by WangZhe@choerodon.io on 2018/5/22.
 * Email: ettwz@hotmail.com
 */
public class VersionEvent {

    private Long versionId;

    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }
}
