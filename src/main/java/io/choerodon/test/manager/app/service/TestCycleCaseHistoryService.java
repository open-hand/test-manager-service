package io.choerodon.test.manager.app.service;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryService {
    TestCycleCaseHistoryVO insert(TestCycleCaseHistoryVO testCycleCaseHistoryVO);

    PageInfo<TestCycleCaseHistoryVO> query(Long cycleCaseId, Pageable pageable);

    void createAssignedHistory(TestCycleCaseVO afterCycleCase, TestCycleCaseVO beforeCycleCase);

    void createStatusHistory(Long executeId, String oldValue,String newValue);

    void createCommentHistory(Long executeId, String oldValue,String newValue);
}
