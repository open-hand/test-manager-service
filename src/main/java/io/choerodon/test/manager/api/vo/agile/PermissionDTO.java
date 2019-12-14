package io.choerodon.test.manager.api.vo.agile;

/**
 * @author wuguokai
 */
public class PermissionDTO {
	private Long id;
	private String code;
	private String path;
	private String method;
	private String level;
	private String description;
	private String action;
	private String resource;
	private Boolean publicAccess;
	private Boolean loginAccess;
	private String serviceName;
	private Long objectVersionNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public Boolean getPublicAccess() {
		return publicAccess;
	}

	public void setPublicAccess(Boolean publicAccess) {
		this.publicAccess = publicAccess;
	}

	public Boolean getLoginAccess() {
		return loginAccess;
	}

	public void setLoginAccess(Boolean loginAccess) {
		this.loginAccess = loginAccess;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Long getObjectVersionNumber() {
		return objectVersionNumber;
	}

	public void setObjectVersionNumber(Long objectVersionNumber) {
		this.objectVersionNumber = objectVersionNumber;
	}
}
