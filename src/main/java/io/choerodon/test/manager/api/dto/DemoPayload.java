package io.choerodon.test.manager.api.dto;

import java.util.Date;
import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/2/14.
 * Email: fuqianghuang01@gmail.com
 */
public class DemoPayload {

    private List<Long> testIssueIds;
    private long versionId;
    private long projectId;
    private long userId;
    private long organizationId;
    private Date dateOne;   //第一个迭代第六个工作日
    private Date dateTwo;   //第一个迭代第八个工作日
    private Date dateThree; //第一个迭代第十个工作日
    private Date dateFour;  //第二个迭代第一个工作日
    private Date dateFive;  //第二个迭代第三个工作日
    private Date dateSix;   //第二个迭代第五个工作日

    public void setTestIssueIds(List<Long> testIssueIds) {
        this.testIssueIds = testIssueIds;
    }

    public List<Long> getTestIssueIds() {
        return testIssueIds;
    }

    public long getVersionId() {
        return versionId;
    }

    public void setVersionId(long versionId) {
        this.versionId = versionId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(long organizationId) {
        this.organizationId = organizationId;
    }

    public Date getDateOne() {
        return dateOne;
    }

    public void setDateOne(Date dateOne) {
        this.dateOne = dateOne;
    }

    public Date getDateTwo() {
        return dateTwo;
    }

    public void setDateTwo(Date dateTwo) {
        this.dateTwo = dateTwo;
    }

    public Date getDateThree() {
        return dateThree;
    }

    public void setDateThree(Date dateThree) {
        this.dateThree = dateThree;
    }

    public Date getDateFour() {
        return dateFour;
    }

    public void setDateFour(Date dateFour) {
        this.dateFour = dateFour;
    }

    public Date getDateFive() {
        return dateFive;
    }

    public void setDateFive(Date dateFive) {
        this.dateFive = dateFive;
    }

    public Date getDateSix() {
        return dateSix;
    }

    public void setDateSix(Date dateSix) {
        this.dateSix = dateSix;
    }
}
