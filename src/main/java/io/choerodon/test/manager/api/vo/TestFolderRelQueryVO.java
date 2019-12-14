package io.choerodon.test.manager.api.vo;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.test.manager.api.vo.agile.SearchDTO;

public class TestFolderRelQueryVO {

    @ApiModelProperty(value = "版本ids")
    private Long[] versionIds;

    @ApiModelProperty(value = "searchDTO")
    private SearchDTO searchDTO;

    public Long[] getVersionIds() {
        return versionIds;
    }

    public void setVersionIds(Long[] versionIds) {
        this.versionIds = versionIds;
    }

    public SearchDTO getSearchDTO() {
        return searchDTO;
    }

    public void setSearchDTO(SearchDTO searchDTO) {
        this.searchDTO = searchDTO;
    }
}
