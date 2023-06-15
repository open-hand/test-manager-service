package io.choerodon.test.manager.api.vo.devops;

import io.swagger.annotations.ApiModelProperty;

/**
 * 标记高亮
 *
 * @author crockitwood
 */
public class HighlightMarker {
    @ApiModelProperty(value = "行号")
    private int line;
    @ApiModelProperty(value = "结束行号")
    private int endLine;
    @ApiModelProperty(value = "起始位")
    private int startIndex;
    @ApiModelProperty(value = "结束位")
    private int endIndex;
    @ApiModelProperty(value = "开始列")
    private int startColumn;
    @ApiModelProperty(value = "结束列")
    private int endColumn;

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(int startColumn) {
        this.startColumn = startColumn;
    }

    public int getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(int endColumn) {
        this.endColumn = endColumn;
    }

    @Override
    public String toString() {
        return "HighlightMarker{" +
                "line=" + line +
                ", startIndex=" + startIndex +
                ", endIndex=" + endIndex +
                ", startColumn=" + startColumn +
                ", endColumn=" + endColumn +
                '}';
    }
}
