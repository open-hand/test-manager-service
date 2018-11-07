package io.choerodon.test.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryService {
    TestCycleCaseHistoryDTO insert(TestCycleCaseHistoryDTO testCycleCaseHistoryDTO);

    Page<TestCycleCaseHistoryDTO> query(Long cycleCaseId, PageRequest pageRequest);

    void createAssignedHistory(TestCycleCaseDTO afterCycleCase, TestCycleCaseDTO beforeCycleCase);

    void createStatusHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase);

    void createCommentHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase);
}
