package io.choerodon.test.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryService {
    TestCycleCaseHistoryDTO insert(TestCycleCaseHistoryDTO testCycleCaseHistoryDTO);

    PageInfo<TestCycleCaseHistoryDTO> query(Long cycleCaseId, PageRequest pageRequest);

    void createAssignedHistory(TestCycleCaseDTO afterCycleCase, TestCycleCaseDTO beforeCycleCase);

    void createStatusHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase);

    void createCommentHistory(TestCycleCaseDTO afterCycleCase,TestCycleCaseDTO beforeCycleCase);
}
