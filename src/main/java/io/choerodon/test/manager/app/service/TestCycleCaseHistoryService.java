package io.choerodon.test.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryService {
    TestCycleCaseHistoryVO insert(TestCycleCaseHistoryVO testCycleCaseHistoryVO);

    Page<TestCycleCaseHistoryVO> query(Long cycleCaseId, PageRequest pageRequest);

    void createAssignedHistory(TestCycleCaseVO afterCycleCase, TestCycleCaseVO beforeCycleCase);

    void createStatusHistory(Long executeId, String oldValue,String newValue);

    void createCommentHistory(Long executeId, String oldValue,String newValue);
}
