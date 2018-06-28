package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseHistoryService {
    TestCycleCaseHistoryDTO insert(TestCycleCaseHistoryDTO testCycleCaseHistoryDTO);

//    void delete(List<TestCycleCaseHistoryDTO> testCycleCaseHistoryDTO);
//
//    List<TestCycleCaseHistoryDTO> update(List<TestCycleCaseHistoryDTO> testCycleCaseHistoryDTO);

    Page<TestCycleCaseHistoryDTO> query(Long cycleCaseId, PageRequest pageRequest);
}
