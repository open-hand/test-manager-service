package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.domain.service.ITestAutomationHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ITestAutomationHistoryServiceImpl implements ITestAutomationHistoryService {

    @Autowired
    TestAutomationHistoryMapper testAutomationHistoryMapper;

    @Override
    public List<TestAutomationHistoryE> query(TestAutomationHistoryE testAutomationHistory) {
        return testAutomationHistoryMapper.select(testAutomationHistory);
    }

    @Override
    public TestAutomationHistoryE update(TestAutomationHistoryE testAutomationHistory) {
        if(testAutomationHistoryMapper.updateByPrimaryKey(testAutomationHistory)==0){
            throw new CommonException("error.ITestAutomationHistoryServiceImpl.update");
        }
        return testAutomationHistoryMapper.selectByPrimaryKey(testAutomationHistory.getId());
    }

    @Override
    public void delete(TestAutomationHistoryE testAutomationHistory) {
        testAutomationHistoryMapper.delete(testAutomationHistory);
    }

    @Override
    public TestAutomationHistoryE insert(TestAutomationHistoryE testAutomationHistory) {
        if(testAutomationHistoryMapper.insert(testAutomationHistory)==0){
            throw new CommonException("error.ITestAutomationHistoryServiceImpl.insert");
        }
        return testAutomationHistoryMapper.selectByPrimaryKey(testAutomationHistory.getId());
    }
}
