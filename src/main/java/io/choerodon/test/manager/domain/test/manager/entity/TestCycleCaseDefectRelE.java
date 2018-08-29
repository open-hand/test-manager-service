package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.test.manager.domain.repository.TestCycleCaseDefectRelRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
@Scope("prototype")
public class TestCycleCaseDefectRelE {

    public static String CASE_STEP = "CASE_STEP";
    public static String CYCLE_CASE = "CYCLE_CASE";

    private Long id;
    private String defectType;
    private Long defectLinkId;
    private Long issueId;
    private String defectName;
    private String defectStatus;
    private String defectColor;
    private Long objectVersionNumber;
    private Long projectId;

    @Autowired
    private TestCycleCaseDefectRelRepository testCycleCaseDefectRelRepository;

    public List<TestCycleCaseDefectRelE> querySelf() {
        return testCycleCaseDefectRelRepository.query(this);
    }

	public Page<TestCycleCaseDefectRelE> querySelf(PageRequest pageRequest) {
		return testCycleCaseDefectRelRepository.query(this, pageRequest);
	}

    public TestCycleCaseDefectRelE addSelf() {
        return testCycleCaseDefectRelRepository.insert(this);
    }

    public TestCycleCaseDefectRelE updateSelf() {
        return testCycleCaseDefectRelRepository.update(this);
    }

    public Boolean updateProjectIdByIssueId(){
        return testCycleCaseDefectRelRepository.updateProjectIdByIssueId(this);
    }

    public void deleteSelf() {
        testCycleCaseDefectRelRepository.delete(this);
    }

    public List<Long> queryAllIssueIds() {
        return testCycleCaseDefectRelRepository.queryAllIssueIds();
    }

<<<<<<< HEAD
=======
    public List<Long>  queryIssueIdAndDefectId(Long projectId){
        return testCycleCaseDefectRelRepository.queryIssueIdAndDefectId(projectId);
    }

>>>>>>> 报表修复
    public String getDefectType() {
        return defectType;
    }

    public Long getDefectLinkId() {
        return defectLinkId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    public void setDefectLinkId(Long defectLinkId) {
        this.defectLinkId = defectLinkId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

    public String getDefectName() {
        return defectName;
    }

    public String getDefectStatus() {
        return defectStatus;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setDefectStatus(String defectStatus) {
        this.defectStatus = defectStatus;
    }

    public void setDefectName(String defectName) {
        this.defectName = defectName;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public TestCycleCaseDefectRelRepository getTestCycleCaseDefectRelRepository() {
        return testCycleCaseDefectRelRepository;
    }

    public void setTestCycleCaseDefectRelRepository(TestCycleCaseDefectRelRepository testCycleCaseDefectRelRepository) {
        this.testCycleCaseDefectRelRepository = testCycleCaseDefectRelRepository;
    }

    public String getDefectColor() {
        return defectColor;
    }

    public void setDefectColor(String defectColor) {
        this.defectColor = defectColor;
    }
}
