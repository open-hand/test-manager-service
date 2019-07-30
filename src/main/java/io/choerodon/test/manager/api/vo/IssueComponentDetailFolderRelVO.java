package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;

import io.choerodon.agile.api.vo.IssueComponentDetailVO;

/**
 * Created by zongw.lee@gmail.com on 09/05/2018
 */
public class IssueComponentDetailFolderRelVO extends IssueComponentDetailVO {

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

    public IssueComponentDetailFolderRelVO(IssueInfosVO issueComponentDetailDTO) {
        BeanUtils.copyProperties(issueComponentDetailDTO, this);
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


}
