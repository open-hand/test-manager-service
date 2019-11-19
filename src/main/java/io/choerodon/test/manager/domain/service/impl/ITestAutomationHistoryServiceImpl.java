//package io.choerodon.test.manager.domain.service.impl;
//
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
//import com.github.pagehelper.PageHelper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import com.github.pagehelper.PageInfo;
//
//import io.choerodon.core.convertor.ConvertPageHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.data.domain.Pageable;
//import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
//import io.choerodon.test.manager.domain.service.ITestAutomationHistoryService;
//import io.choerodon.test.manager.infra.vo.TestAutomationHistoryDTO;
//import io.choerodon.test.manager.infra.util.PageUtil;
//import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper;
//
//@Component
//public class ITestAutomationHistoryServiceImpl implements ITestAutomationHistoryService {
//
//    @Autowired
//    TestAutomationHistoryMapper testAutomationHistoryMapper;
//
//    @Override
//    public List<TestAutomationHistoryDTO> query(TestAutomationHistoryDTO testAutomationHistory) {
//        return testAutomationHistoryMapper.select(testAutomationHistory);
//    }
//
//    @Override
//    public PageInfo<TestAutomationHistoryVO> queryWithInstance(Map map, Pageable pageable) {
//        PageInfo<TestAutomationHistoryDTO> serviceDOPage = PageHelper.startPage(pageable.getPageNumber(),
//                pageable.getPageSize(), PageUtil.sortToSql(pageable.getSort())).doSelectPageInfo(() -> testAutomationHistoryMapper.queryWithInstance(map));
//        return ConvertPageHelper.convertPageInfo(serviceDOPage, TestAutomationHistoryVO.class);
//    }
//
//    @Override
//    public TestAutomationHistoryDTO queryByPrimaryKey(Long historyId) {
//        return testAutomationHistoryMapper.selectByPrimaryKey(historyId);
//    }
//
//    @Override
//    public TestAutomationHistoryDTO update(TestAutomationHistoryDTO testAutomationHistory) {
//        if (testAutomationHistoryMapper.updateByPrimaryKeySelective(testAutomationHistory) == 0) {
//            throw new CommonException("error.ITestAutomationHistoryServiceImpl.update");
//        }
//        return testAutomationHistoryMapper.selectByPrimaryKey(testAutomationHistory.getId());
//    }
//
//    @Override
//    public void delete(TestAutomationHistoryDTO testAutomationHistory) {
//        testAutomationHistoryMapper.delete(testAutomationHistory);
//    }
//
//    @Override
//    public TestAutomationHistoryDTO insert(TestAutomationHistoryDTO testAutomationHistory) {
//        if (testAutomationHistoryMapper.insert(testAutomationHistory) == 0) {
//            throw new CommonException("error.ITestAutomationHistoryServiceImpl.insert");
//        }
//        return testAutomationHistoryMapper.selectByPrimaryKey(testAutomationHistory.getId());
//    }
//
//    public void shutdownInstance(Long instanceId, Long status) {
//        TestAutomationHistoryDTO historyE = new TestAutomationHistoryDTO();
//        historyE.setInstanceId(instanceId);
//        historyE.setTestStatus(TestAutomationHistoryDTO.Status.NONEXECUTION);
//        historyE.setLastUpdateDate(new Date());
//        testAutomationHistoryMapper.shutdownInstance(historyE);
//    }
//}
