package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCaseStepDTO;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCaseStepService {

	List<TestCaseStepDTO> query(TestCaseStepDTO testCaseStepDTO);

	void removeStep(TestCaseStepDTO testCaseStepDTO);

	List<TestCaseStepDTO> batchInsertStep(List<TestCaseStepDTO> testCaseStepDTO);

	TestCaseStepDTO changeStep(TestCaseStepDTO testCaseStepDTO);
}
