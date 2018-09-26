package io.choerodon.test.manager.api.dto;

import io.choerodon.agile.api.dto.IssueComponentDetailDTO;
import org.springframework.beans.BeanUtils;

/**
 * Created by zongw.lee@gmail.com on 09/05/2018
 */
public class IssueComponentDetailFolderRelDTO extends IssueComponentDetailDTO {
    private Long objectVersionNumber;

    private String folderName;

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
        BeanUtils.copyProperties(issueComponentDetailDTO,this);
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


}
