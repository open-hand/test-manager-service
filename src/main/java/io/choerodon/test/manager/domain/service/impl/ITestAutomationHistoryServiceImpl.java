package io.choerodon.test.manager.domain.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.pagehelper.PageInfo;

import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.domain.service.ITestAutomationHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;
import io.choerodon.test.manager.infra.common.utils.PageUtil;
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper;

@Component
public class ITestAutomationHistoryServiceImpl implements ITestAutomationHistoryService {

    @Autowired
    TestAutomationHistoryMapper testAutomationHistoryMapper;

    @Override
    public List<TestAutomationHistoryE> query(TestAutomationHistoryE testAutomationHistory) {
        return testAutomationHistoryMapper.select(testAutomationHistory);
    }

    @Override
    public PageInfo<TestAutomationHistoryDTO> queryWithInstance(Map map, PageRequest pageRequest) {
        PageInfo<TestAutomationHistoryE> serviceDOPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> testAutomationHistoryMapper.queryWithInstance(map));
        return ConvertPageHelper.convertPageInfo(serviceDOPage, TestAutomationHistoryDTO.class);
    }

    @Override
    public TestAutomationHistoryE queryByPrimaryKey(Long historyId) {
        return testAutomationHistoryMapper.selectByPrimaryKey(historyId);
    }

    @Override
    public TestAutomationHistoryE update(TestAutomationHistoryE testAutomationHistory) {
        if (testAutomationHistoryMapper.updateByPrimaryKeySelective(testAutomationHistory) == 0) {
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
        if (testAutomationHistoryMapper.insert(testAutomationHistory) == 0) {
            throw new CommonException("error.ITestAutomationHistoryServiceImpl.insert");
        }
        return testAutomationHistoryMapper.selectByPrimaryKey(testAutomationHistory.getId());
    }

    public void shutdownInstance(Long instanceId, Long status) {
        TestAutomationHistoryE historyE = new TestAutomationHistoryE();
        historyE.setInstanceId(instanceId);
        historyE.setTestStatus(TestAutomationHistoryE.Status.NONEXECUTION);
        historyE.setLastUpdateDate(new Date());
        testAutomationHistoryMapper.shutdownInstance(historyE);
    }
}
