package com.test.devops.app.service;

import com.test.devops.api.dto.TestCycleCaseDTO;
import com.test.devops.api.dto.TestCycleCaseStepDTO;
import com.test.devops.domain.entity.TestCycleCaseE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseStepService {

	/**
	 * 更新循环步骤
	 *
	 * @param testCycleCaseStepDTO
	 * @return
	 */
	List<TestCycleCaseStepDTO> update(List<TestCycleCaseStepDTO> testCycleCaseStepDTO);


	/**
	 * 查询循环测试步骤
	 *
	 * @param testCycleCaseDTO
	 * @return
	 */
	List<TestCycleCaseStepDTO> querySubStep(Long CycleCaseId);

	/**
	 * 启动循环测试下所有步骤
	 *
	 * @param testCycleCaseDTO
	 */
	void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO);

	/**
	 * 删除CycleCase下所有Step
	 *
	 * @param testCycleCaseDTO
	 */
	void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO);
}
