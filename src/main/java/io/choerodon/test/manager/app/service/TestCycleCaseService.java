package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseService {

    void delete(Long cycleCaseId);

//    List<TestCycleCaseDTO> update(List<TestCycleCaseDTO> testCycleCaseDTO);

    Page<TestCycleCaseDTO> query(TestCycleCaseDTO testCycleCaseDTO, PageRequest pageRequest);

	Page<TestCycleCaseDTO> queryByCycle(Long cycleId, PageRequest pageRequest);

    TestCycleCaseDTO queryOne(Long cycleCaseId);

    /**
     * 启动一个测试例
     *
     * @param testCycleCaseDTO
     * @return
     */
    TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId);



    /**
	 * 修改一个case
     *
     * @param testCycleCaseDTO
     */
	TestCycleCaseDTO changeOneCase(TestCycleCaseDTO testCycleCaseDTO);

}
