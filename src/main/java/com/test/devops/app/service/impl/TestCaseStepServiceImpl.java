package com.test.devops.app.service.impl;

import com.test.devops.api.dto.TestCaseStepDTO;
import com.test.devops.api.dto.TestCycleCaseStepDTO;
import com.test.devops.app.service.TestCaseStepService;
import com.test.devops.domain.entity.TestCaseStepE;
import com.test.devops.domain.service.ITestCaseStepService;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class TestCaseStepServiceImpl implements TestCaseStepService {
	@Autowired
	ITestCaseStepService iTestCaseStepService;


	@Override
	public void removeStep(TestCaseStepDTO testCaseStepDTO) {
		iTestCaseStepService.removeStep(ConvertHelper.convert(testCaseStepDTO, TestCaseStepE.class));
	}


	@Override
	public List<TestCaseStepDTO> query(TestCaseStepDTO testCaseStepDTO) {
		return ConvertHelper.convertList(iTestCaseStepService.query(ConvertHelper.convert(testCaseStepDTO, TestCaseStepE.class)), TestCaseStepDTO.class);
	}

	@Override
	public TestCaseStepDTO changeStep(TestCaseStepDTO testCaseStepDTO) {
		return ConvertHelper.convert(iTestCaseStepService.changeStep(ConvertHelper.convert(testCaseStepDTO, TestCaseStepE.class)), TestCaseStepDTO.class);
	}


	@Override
	public List<TestCaseStepDTO> batchInsertStep(List<TestCaseStepDTO> testCaseStepDTO) {
		return ConvertHelper.convertList(iTestCaseStepService.batchInsertStep(ConvertHelper.convertList(testCaseStepDTO, TestCaseStepE.class)), TestCaseStepDTO.class);
	}

}
