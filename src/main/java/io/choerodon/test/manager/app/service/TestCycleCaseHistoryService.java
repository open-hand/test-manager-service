package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryService {
    TestCycleCaseHistoryDTO insert(TestCycleCaseHistoryDTO testCycleCaseHistoryDTO);

    Page<TestCycleCaseHistoryDTO> query(Long cycleCaseId, PageRequest pageRequest);

    TestCycleCaseHistoryDTO createAssignedHistory(TestCycleCaseDTO afterCycleCase, TestCycleCaseDTO beforeCycleCase);

    TestCycleCaseHistoryDTO createStatusHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase);

    TestCycleCaseHistoryDTO createCommentHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase);
}
