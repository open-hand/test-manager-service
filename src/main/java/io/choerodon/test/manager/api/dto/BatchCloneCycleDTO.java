package io.choerodon.test.manager.api.dto;

import io.swagger.annotations.ApiModelProperty;

public class BatchCloneCycleDTO {

    @ApiModelProperty(value = "要克隆的循环id")
    private Long cycleId;

    @ApiModelProperty(value = "循环中的阶段ids")
    private Long[] folderIds;

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Long[] getFolderIds() {
        return folderIds;
    }

    public void setFolderIds(Long[] folderIds) {
        this.folderIds = folderIds;
    }
}
