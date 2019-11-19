//package io.choerodon.test.manager.domain.service;
//
//import com.github.pagehelper.PageInfo;
//import org.springframework.data.domain.Pageable;
//import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
//import io.choerodon.test.manager.infra.vo.TestAutomationHistoryDTO;
//
//import java.util.List;
//import java.util.Map;
//
//public interface ITestAutomationHistoryService {
//
//    List<TestAutomationHistoryDTO> query(TestAutomationHistoryDTO testAutomationHistory);
//
//    TestAutomationHistoryDTO queryByPrimaryKey(Long historyId);
//
//    TestAutomationHistoryDTO update(TestAutomationHistoryDTO testAutomationHistory);
//
//    void delete(TestAutomationHistoryDTO testAutomationHistory);
//
//    TestAutomationHistoryDTO insert(TestAutomationHistoryDTO testAutomationHistory);
//
//    void shutdownInstance(Long instanceId,Long status);
//
//    PageInfo<TestAutomationHistoryVO> queryWithInstance(Map map, Pageable pageable);
//}
