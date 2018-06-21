package com.test.devops.domain.entity;

import com.test.devops.domain.repository.TestCycleCaseDefectRelRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Table;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
@Scope("prototype")
public class TestCycleCaseDefectRelE {
	private String defectType;
	private Long defectLinkId;
	private Long issueId;
	private Long objectVersionNumber;

	@Autowired
	private TestCycleCaseDefectRelRepository testCycleCaseDefectRelRepository;

	public Page<TestCycleCaseDefectRelE> querySelf(PageRequest pageRequest) {
		return testCycleCaseDefectRelRepository.query(this, pageRequest);
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
}
