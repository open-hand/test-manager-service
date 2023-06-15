package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * yaml 转换结果
 *
 * @author crockitwood
 */
public class ReplaceResult {
    @ApiModelProperty(value = "yaml")
    private String yaml;
    @ApiModelProperty(value = "高亮标记")
    private List<HighlightMarker> highlightMarkers;
    @ApiModelProperty(value = "总行数")
    private Integer totalLine;
    @ApiModelProperty(value = "错误信息")
    private String errorMsg;
    @ApiModelProperty(value = "错误行")
    private List<ErrorLineDTO> errorLines;
    @ApiModelProperty(value = "新行")
    private List<Integer> newLines;
    @ApiModelProperty(value = "deltaYaml")
    private String deltaYaml;

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

    public List<ErrorLineDTO> getErrorLines() {
        return errorLines;
    }

    public void setErrorLines(List<ErrorLineDTO> errorLines) {
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
}
