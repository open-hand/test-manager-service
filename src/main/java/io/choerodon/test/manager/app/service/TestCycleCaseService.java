package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseService {

    void delete(Long cycleCaseId);

//    List<TestCycleCaseDTO> update(List<TestCycleCaseDTO> testCycleCaseDTO);

    Page<TestCycleCaseDTO> query(TestCycleCaseDTO testCycleCaseDTO, PageRequest pageRequest);

    List<TestCycleCaseDTO> queryByCycle(Long cycleId);

    TestCycleCaseDTO queryOne(Long cycleCaseId);

    /**
     * 启动一个测试例
     *
     * @param testCycleCaseDTO
     * @return
     */
    TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId);


//    List<TestCycleCaseDTO> changeCycleCase(List<TestCycleCaseDTO> testCycleCaseES);

    /**
     * 增加|修改一个case
     *
     * @param testCycleCaseDTO
     */
    void changeOneCase(TestCycleCaseDTO testCycleCaseDTO, Long projectId);

}
