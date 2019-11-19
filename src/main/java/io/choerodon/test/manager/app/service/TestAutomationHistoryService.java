package io.choerodon.test.manager.app.service;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;

import java.util.Map;

public interface TestAutomationHistoryService {

    PageInfo<TestAutomationHistoryVO> queryWithInstance(Map map, Pageable pageable, Long projectId);

    String queryFrameworkByResultId(Long projectId, Long resultId);

    void shutdownInstance(Long instanceId,Long status);
}
