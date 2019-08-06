package io.choerodon.test.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;

import io.choerodon.agile.api.dto.IssueComponentDetailDTO;

/**
 * Created by zongw.lee@gmail.com on 09/05/2018
 */
public class IssueComponentDetailFolderRelDTO extends IssueComponentDetailDTO {

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    @ApiModelProperty(value = "文件夹名称")
    private String folderName;

    @ApiModelProperty(value = "文件夹id")
    private Long folderId;

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public IssueComponentDetailFolderRelDTO(IssueInfosDTO issueComponentDetailDTO) {
        BeanUtils.copyProperties(issueComponentDetailDTO, this);
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


}
