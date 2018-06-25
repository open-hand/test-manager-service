package io.choerodon.test.manager.api.dto;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */

public class TestCycleCaseHistoryDTO {

	private Long executeId;
	private String oldValue;
	private String newValue;
	private Long objectVersionNumber;

	public Long getExecuteId() {
		return executeId;
	}

	public void setExecuteId(Long executeId) {
		this.executeId = executeId;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}
}
