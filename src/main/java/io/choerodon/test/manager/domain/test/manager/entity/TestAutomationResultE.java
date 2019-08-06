//package io.choerodon.test.manager.domain.test.manager.entity;
//
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Scope;
//import org.springframework.stereotype.Component;
//
//import io.choerodon.test.manager.domain.repository.TestAutomationResultRepository;
//
//@Component
//@Scope("prototype")
//public class TestAutomationResultE {
//
//    @Autowired
//    private TestAutomationResultRepository testAutomationResultRepository;
//
//    private Long id;
//
//    private String result;
//
//    private Long createdBy;
//
//    private Long lastUpdatedBy;
//
//    private Date creationDate;
//
//    private Date lastUpdateDate;
//
//    private Long objectVersionNumber;
//
//    public Date getCreationDate() {
//        return creationDate;
//    }
//
//    public void setCreationDate(Date creationDate) {
//        this.creationDate = creationDate;
//    }
//
//    public Date getLastUpdateDate() {
//        return lastUpdateDate;
//    }
//
//    public void setLastUpdateDate(Date lastUpdateDate) {
//        this.lastUpdateDate = lastUpdateDate;
//    }
//
//    public Long getCreatedBy() {
//        return createdBy;
//    }
//
//    public void setCreatedBy(Long createdBy) {
//        this.createdBy = createdBy;
//    }
//
//    public Long getLastUpdatedBy() {
//        return lastUpdatedBy;
//    }
//
//    public void setLastUpdatedBy(Long lastUpdatedBy) {
//        this.lastUpdatedBy = lastUpdatedBy;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getResult() {
//        return result;
//    }
//
//    public void setResult(String result) {
//        this.result = result;
//    }
//
//    public List<TestAutomationResultE> query() {
//        return testAutomationResultRepository.query(this);
//    }
//
//    public TestAutomationResultE addSelf() {
//        return testAutomationResultRepository.insert(this);
//    }
//
//    public TestAutomationResultE updateSelf() {
//        return testAutomationResultRepository.update(this);
//    }
//
//    public void deleteSelf() {
//        testAutomationResultRepository.delete(this);
//    }
//
//    public Long getObjectVersionNumber() {
//        return objectVersionNumber;
//    }
//
//    public void setObjectVersionNumber(Long objectVersionNumber) {
//        this.objectVersionNumber = objectVersionNumber;
//    }
//}
