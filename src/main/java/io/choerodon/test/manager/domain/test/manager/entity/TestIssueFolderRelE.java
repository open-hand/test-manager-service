package io.choerodon.test.manager.domain.test.manager.entity;

import java.util.List;

import io.choerodon.test.manager.domain.repository.TestIssueFolderRelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@Component
@Scope("prototype")
public class TestIssueFolderRelE {

    private Long id;

    private Long folderId;

    private Long versionId;

    private Long projectId;

    private Long issueId;

    private Long objectVersionNumber;

    @Autowired
    private TestIssueFolderRelRepository testIssueFolderRelRepository;

    public List<TestIssueFolderRelE> queryAllUnderProject() {
        return testIssueFolderRelRepository.queryAllUnderProject(this);
    }

    public TestIssueFolderRelE addSelf() {
        return testIssueFolderRelRepository.insert(this);
    }

    public TestIssueFolderRelE updateSelf() {
        return testIssueFolderRelRepository.update(this);
    }

    public void deleteSelf() {
        testIssueFolderRelRepository.delete(this);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
