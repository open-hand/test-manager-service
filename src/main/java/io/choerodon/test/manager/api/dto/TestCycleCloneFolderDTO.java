package io.choerodon.test.manager.api.dto;

/**
 * Created by WangZhe@choerodon.io on 2018/6/26.
 * Email: ettwz@hotmail.com
 */
public class TestCycleCloneFolderDTO {
    private Long cycleId;
    private String cycleName;

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public String getCycleName() {
        return cycleName;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }
}
