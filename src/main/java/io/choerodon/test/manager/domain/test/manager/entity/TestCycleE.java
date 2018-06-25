package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
@Scope("prototype")
public class TestCycleE {
	private Long cycleId;

	private Long parentCycleId;

	private String cycleName;

	private Long versionId;

	private String description;

	private String build;

	private String environment;

	private Date fromDate;

	private Date toDate;

	private String type;

	private Long objectVersionNumber;

	@Autowired
    TestCycleRepository testCycleRepository;

	public Page<TestCycleE> querySelf(PageRequest pageRequest) {
		return testCycleRepository.query(this, pageRequest);
	}

	public List<TestCycleE> querySelf() {
		return testCycleRepository.query(this);
	}

	public TestCycleE addSelf() {
		return testCycleRepository.insert(this);
	}

	public TestCycleE updateSelf() {
		return testCycleRepository.update(this);
	}

	public void deleteSelf() {
		testCycleRepository.delete(this);
	}

	public Long getCycleId() {
		return cycleId;
	}

	public Long getParentCycleId() {
		return parentCycleId;
	}

	public String getCycleName() {
		return cycleName;
	}

	public Long getVersionId() {
		return versionId;
	}

	public void setVersionId(Long versionId) {
		this.versionId = versionId;
	}

	public String getDescription() {
		return description;
	}

	public String getBuild() {
		return build;
	}

	public String getEnvironment() {
		return environment;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public String getType() {
		return type;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}


	public void setCycleId(Long cycleId) {
		this.cycleId = cycleId;
	}

	public void setParentCycleId(Long parentCycleId) {
		this.parentCycleId = parentCycleId;
	}

	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}


	public void setDescription(String description) {
		this.description = description;
	}

	public void setBuild(String build) {
		this.build = build;
	}

	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}

	public void setTestCycleRepository(TestCycleRepository testCycleRepository) {
		this.testCycleRepository = testCycleRepository;
	}
}
