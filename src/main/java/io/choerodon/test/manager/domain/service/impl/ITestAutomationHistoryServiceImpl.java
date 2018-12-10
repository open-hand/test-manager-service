package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.domain.service.ITestAutomationHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ITestAutomationHistoryServiceImpl implements ITestAutomationHistoryService {

    @Autowired
    TestAutomationHistoryMapper testAutomationHistoryMapper;

    @Override
    public List<TestAutomationHistoryE> query(TestAutomationHistoryE testAutomationHistory) {
        return testAutomationHistoryMapper.select(testAutomationHistory);
    }

    @Override
    public Page<TestAutomationHistoryDTO> queryWithInstance(Map map, PageRequest pageRequest) {
        Page<TestAutomationHistoryE> serviceDOPage = PageHelper.doPageAndSort(pageRequest,
                () -> testAutomationHistoryMapper.queryWithInstance(map));
        return ConvertPageHelper.convertPage(serviceDOPage, TestAutomationHistoryDTO.class);
    }

    @Override
    public TestAutomationHistoryE queryByPrimaryKey(Long historyId) {
        return testAutomationHistoryMapper.selectByPrimaryKey(historyId);
    }

    @Override
    public TestAutomationHistoryE update(TestAutomationHistoryE testAutomationHistory) {
        if(testAutomationHistoryMapper.updateByPrimaryKeySelective(testAutomationHistory)==0){
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

    public void shutdownInstance(Long instanceId,Long status){
        TestAutomationHistoryE historyE=new TestAutomationHistoryE();
        historyE.setInstanceId(instanceId);
        TestAutomationHistoryE historyE1=testAutomationHistoryMapper.selectOne(historyE);
        historyE.setObjectVersionNumber(historyE1.getObjectVersionNumber());
        historyE.setTestStatus(TestAutomationHistoryE.Status.NONEXECUTION);
        historyE.setId(historyE1.getId());
        testAutomationHistoryMapper.updateByPrimaryKey(historyE);
    }
}
