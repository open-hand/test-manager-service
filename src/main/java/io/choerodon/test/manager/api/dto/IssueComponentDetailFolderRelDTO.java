package io.choerodon.test.manager.api.dto;

import io.choerodon.agile.api.dto.IssueComponentDetailDTO;
import org.springframework.beans.BeanUtils;

/**
 * Created by zongw.lee@gmail.com on 09/05/2018
 */
public class IssueComponentDetailFolderRelDTO extends IssueComponentDetailDTO {
    private Long objectVersionNumber;

    public IssueComponentDetailFolderRelDTO(Long objectVersionNumber, IssueInfosDTO issueComponentDetailDTO) {
        this.objectVersionNumber = objectVersionNumber;
        BeanUtils.copyProperties(issueComponentDetailDTO,this);
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }


}
