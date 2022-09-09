package io.choerodon.test.manager.api.vo.event;

import io.swagger.annotations.ApiModelProperty;

/**
 * 注册组织dto
 */
public class OrganizationRegisterEventPayload {

    @ApiModelProperty(value = "组织信息")
    private Organization organization;

    @ApiModelProperty(value = "管理员")
    private User user;

    @ApiModelProperty(value = "用户A")
    private User userA;

    @ApiModelProperty(value = "用户B")
    private User userB;

    @ApiModelProperty(value = "项目信息")
    private Project project;

    @ApiModelProperty(value = "瀑布项目信息")
    private Project waterfallProject;

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUserA() {
        return userA;
    }

    public void setUserA(User userA) {
        this.userA = userA;
    }

    public User getUserB() {
        return userB;
    }

    public void setUserB(User userB) {
        this.userB = userB;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Project getWaterfallProject() {
        return waterfallProject;
    }

    public void setWaterfallProject(Project waterfallProject) {
        this.waterfallProject = waterfallProject;
    }

    public static class User {
        private Long id;
        private String loginName;
        private String email;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getLoginName() {
            return loginName;
        }

        public void setLoginName(String loginName) {
            this.loginName = loginName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class Project {
        private Long id;
        private String code;
        private String name;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Organization {
        private Long id;
        private String code;
        private String name;

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

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}