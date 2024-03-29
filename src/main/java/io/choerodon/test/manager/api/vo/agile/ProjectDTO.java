package io.choerodon.test.manager.api.vo.agile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;

import java.util.Date;
import java.util.List;

/**
 * @author flyleft
 * @date 2018/3/22
 */
public class ProjectDTO {

    private static final String CODE_REGULAR_EXPRESSION =
            "[a-zA-Z0-9_\\.][a-zA-Z0-9_\\-\\.]*[a-zA-Z0-9_\\-]|[a-zA-Z0-9_]";

    private Long id;

    @NotEmpty(message = "error.project.name.empty")
    @Size(min = 1, max = 32, message = "error.project.code.size")
    private String name;

    private Long organizationId;

    @NotEmpty(message = "error.project.code.empty")
    @Size(min = 1, max = 14, message = "error.project.code.size")
    @Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.project.code.illegal")
    private String code;

    private Boolean enabled;

    private Long objectVersionNumber;

    @ApiModelProperty(value = "项目类型(非开源，一对多)")
    private List<ProjectCategoryDTO> categories;

    @ApiModelProperty(value = "项目图标url")
    private String imageUrl;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<ProjectCategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<ProjectCategoryDTO> categories) {
        this.categories = categories;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void updateCheck() {
        if (StringUtils.isEmpty(this.name)) {
            throw new CommonException("error.project.name.empty");
        }
        if (this.name.length() < 1 || this.name.length() > 32) {
            throw new CommonException("error.project.code.size");
        }
        if (this.objectVersionNumber == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
    }
}
