package io.choerodon.test.manager.infra.dto;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@ModifyAudit
@VersionAudit
@Table(name = "test_api_assertion")
public class TestApiAssertionDTO extends AuditDomain {
    @Encrypt
    @Id
    @GeneratedValue
    @ApiModelProperty(name = "主键")
    private Long id;
    @ApiModelProperty(name = "断言名称")
    private String name;
    @ApiModelProperty(name = "项目id")
    private Long projectId;
    @Encrypt
    @ApiModelProperty(name = "关联的接口测试用例id")
    private Long testApiCaseId;
    @ApiModelProperty(name = "断言类型")
    private String type;
    @ApiModelProperty(name = "比较符")
    private String symbol;
    @ApiModelProperty(name = "比较key")
    private String comparisonKey;
    @ApiModelProperty(name = "比较值")
    private String expectValue;

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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getTestApiCaseId() {
        return testApiCaseId;
    }

    public void setTestApiCaseId(Long testApiCaseId) {
        this.testApiCaseId = testApiCaseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getComparisonKey() {
        return comparisonKey;
    }

    public void setComparisonKey(String comparisonKey) {
        this.comparisonKey = comparisonKey;
    }

    public String getExpectValue() {
        return expectValue;
    }

    public void setExpectValue(String expectValue) {
        this.expectValue = expectValue;
    }
}
