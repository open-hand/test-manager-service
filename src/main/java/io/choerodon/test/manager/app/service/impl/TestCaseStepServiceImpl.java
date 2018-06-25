package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.test.manager.domain.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
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
