package io.choerodon.test.manager.domain.entity;

import io.choerodon.test.manager.domain.repository.TestCycleCaseDefectRelRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Table;
import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
@Scope("prototype")
public class TestCycleCaseDefectRelE {
	private Long id;
	private String defectType;
	private Long defectLinkId;
	private Long issueId;
	private String defectName;
	private Long objectVersionNumber;

	@Autowired
	private TestCycleCaseDefectRelRepository testCycleCaseDefectRelRepository;

	public List<TestCycleCaseDefectRelE> querySelf() {
		return testCycleCaseDefectRelRepository.query(this);
	}

	public TestCycleCaseDefectRelE addSelf() {
		return testCycleCaseDefectRelRepository.insert(this);
	}

	public TestCycleCaseDefectRelE updateSelf() {
		return testCycleCaseDefectRelRepository.update(this);
	}

	public void deleteSelf() {
		testCycleCaseDefectRelRepository.delete(this);
	}

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
}
