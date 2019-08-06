package io.choerodon.test.manager.api.vo;

import java.util.List;

import io.choerodon.agile.api.vo.*;

/**
 * Created by zongw.lee@gmail.com on 18/10/2018
 */
public class ExcelLookupCaseVO {
    //优先级
    private List<LookupValueDTO> lookupValueDTOS;

    private List<UserDTO> userDTOS;

    private List<ProductVersionDTO> productVersionDTOS;

    private List<TestIssueFolderVO> testIssueFolderVOS;

    private List<IssueStatusDTO> issueStatusDTOS;

    public ExcelLookupCaseVO(List<LookupValueDTO> lookupValueDTOS, List<UserDTO> userDTOS, List<ProductVersionDTO> productVersionDTOS,
                             List<TestIssueFolderVO> testIssueFolderVOS, List<IssueStatusDTO> issueStatusDTOS) {
        this.lookupValueDTOS = lookupValueDTOS;
        this.userDTOS = userDTOS;
        this.productVersionDTOS = productVersionDTOS;
        this.testIssueFolderVOS = testIssueFolderVOS;
        this.issueStatusDTOS = issueStatusDTOS;
    }

    public List<LookupValueDTO> getLookupValueDTOS() {
        return lookupValueDTOS;
    }

    public void setLookupValueDTOS(List<LookupValueDTO> lookupValueDTOS) {
        this.lookupValueDTOS = lookupValueDTOS;
    }

    public List<UserDTO> getUserDTOS() {
        return userDTOS;
    }

    public void setUserDTOS(List<UserDTO> userDTOS) {
        this.userDTOS = userDTOS;
    }

    public List<ProductVersionDTO> getProductVersionDTOS() {
        return productVersionDTOS;
    }

    public void setProductVersionDTOS(List<ProductVersionDTO> productVersionDTOS) {
        this.productVersionDTOS = productVersionDTOS;
    }

    public List<TestIssueFolderVO> getTestIssueFolderVOS() {
        return testIssueFolderVOS;
    }

    public void setTestIssueFolderVOS(List<TestIssueFolderVO> testIssueFolderVOS) {
        this.testIssueFolderVOS = testIssueFolderVOS;
    }

    public List<IssueStatusDTO> getIssueStatusDTOS() {
        return issueStatusDTOS;
    }

    public void setIssueStatusDTOS(List<IssueStatusDTO> issueStatusDTOS) {
        this.issueStatusDTOS = issueStatusDTOS;
    }
}
