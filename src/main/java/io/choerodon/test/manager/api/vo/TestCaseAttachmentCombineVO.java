package io.choerodon.test.manager.api.vo;


import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author chihao.ran@hand-china.com
 * 2021/01/27 14:38
 */
public class TestCaseAttachmentCombineVO {
    @ApiModelProperty("用例id")
    @Encrypt
    private Long caseId;
    @ApiModelProperty("文件名称")
    private String fileName;
    @ApiModelProperty("源文件md5编码")
    private String guid;

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }


    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}