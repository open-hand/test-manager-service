package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * yaml 转换结果
 *
 * @author crockitwood
 */
public class InstanceValueVO {
    @ApiModelProperty(value = "yaml")
    private String yaml;
    @ApiModelProperty(value = "高亮标记")
    private List<HighlightMarker> highlightMarkers;
    @ApiModelProperty(value = "总行数")
    private Integer totalLine;
    @ApiModelProperty(value = "错误信息")
    private String errorMsg;
    @ApiModelProperty(value = "错误行")
    private List<ErrorLineVO> errorLines;
    @ApiModelProperty(value = "新行")
    private List<Integer> newLines;
    @ApiModelProperty(value = "deltaYaml")
    private String deltaYaml;
    @ApiModelProperty(value = "名称")
    private String name;
    @ApiModelProperty(value = "id")
    @Encrypt
    private Long id;
    @ApiModelProperty(value = "乐观锁")
    private Long objectVersionNumber;

    public String getYaml() {
        return yaml;
    }

    public void setYaml(String yaml) {
        this.yaml = yaml;
    }

    public List<HighlightMarker> getHighlightMarkers() {
        return highlightMarkers;
    }

    public void setHighlightMarkers(List<HighlightMarker> highlightMarkers) {
        this.highlightMarkers = highlightMarkers;
    }

    public Integer getTotalLine() {
        return totalLine;
    }

    public void setTotalLine(Integer totalLine) {
        this.totalLine = totalLine;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<ErrorLineVO> getErrorLines() {
        return errorLines;
    }

    public void setErrorLines(List<ErrorLineVO> errorLines) {
        this.errorLines = errorLines;
    }

    public List<Integer> getNewLines() {
        return newLines;
    }

    public void setNewLines(List<Integer> newLines) {
        this.newLines = newLines;
    }

    public String getDeltaYaml() {
        return deltaYaml;
    }

    public void setDeltaYaml(String deltaYaml) {
        this.deltaYaml = deltaYaml;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
