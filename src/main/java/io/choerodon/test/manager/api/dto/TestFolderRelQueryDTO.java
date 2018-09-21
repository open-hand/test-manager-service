package io.choerodon.test.manager.api.dto;

import io.choerodon.agile.api.dto.SearchDTO;

public class TestFolderRelQueryDTO {
    private Long[] versionIds;
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
