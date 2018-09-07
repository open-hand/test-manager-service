package io.choerodon.test.manager.api.dto;

/**
 * Created by zongw.lee@gmail.com on 09/06/2018
 */
public class TestIssueFolderComposeRelDTO {
    private TestIssueFolderDTO testIssueFolderDTO;

    private TestIssueFolderRelDTO testIssueFolderRelDTO;

    public TestIssueFolderDTO getTestIssueFolderDTO() {
        return testIssueFolderDTO;
    }

    public void setTestIssueFolderDTO(TestIssueFolderDTO testIssueFolderDTO) {
        this.testIssueFolderDTO = testIssueFolderDTO;
    }

    public TestIssueFolderRelDTO getTestIssueFolderRelDTO() {
        return testIssueFolderRelDTO;
    }

    public void setTestIssueFolderRelDTO(TestIssueFolderRelDTO testIssueFolderRelDTO) {
        this.testIssueFolderRelDTO = testIssueFolderRelDTO;
    }
}
