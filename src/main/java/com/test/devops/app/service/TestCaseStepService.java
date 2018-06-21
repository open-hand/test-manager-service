package com.test.devops.app.service;

import com.test.devops.api.dto.TestCaseStepDTO;
import com.test.devops.domain.entity.TestCaseStepE;

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
