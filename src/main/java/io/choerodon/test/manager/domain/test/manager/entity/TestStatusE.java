package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.test.manager.domain.repository.TestStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
@Component
@Scope("prototype")
public class TestStatusE {
	private Long statusId;

	private String statusName;

	private String description;

	private String statusColor;

	private String statusType;

	private Long objectVersionNumber;

	@Autowired
	TestStatusRepository testStatusRepository;

	public List<TestStatusE> querySelf() {
		return testStatusRepository.query(this);
	}

	public TestStatusE addSelf() {
		return testStatusRepository.insert(this);
	}

	public TestStatusE updateSelf() {
		return testStatusRepository.update(this);
	}

	public void deleteSelf() {
		testStatusRepository.delete(this);
	}


	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatusColor() {
		return statusColor;
	}

	public void setStatusColor(String statusColor) {
		this.statusColor = statusColor;
	}

	public String getStatusType() {
		return statusType;
	}

	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}
}
