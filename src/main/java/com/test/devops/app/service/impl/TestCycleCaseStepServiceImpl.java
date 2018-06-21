package com.test.devops.app.service.impl;

import com.test.devops.api.dto.TestCycleCaseDTO;
import com.test.devops.api.dto.TestCycleCaseStepDTO;
import com.test.devops.app.service.TestCycleCaseStepService;
import com.test.devops.domain.entity.TestCycleCaseE;
import com.test.devops.domain.entity.TestCycleCaseStepE;
import com.test.devops.domain.service.ITestCycleCaseStepService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class TestCycleCaseStepServiceImpl implements TestCycleCaseStepService {
	@Autowired
	ITestCycleCaseStepService iTestCycleCaseStepService;
//	@Override
//	public TestCycleCaseStepDTO createTestCycleCaseStep(TestCycleCaseStepDTO testCycleCaseStepDTO) {
//		return ConvertHelper.convert(iTestCycleCaseStepService.createTestCycleCaseStep(ConvertHelper.convert(testCycleCaseStepDTO,TestCycleCaseStepE.class)),TestCycleCaseStepDTO.class);
//	}

//	@Override
//	public void delete(List<TestCycleCaseStepDTO> testCycleCaseStepDTO) {
//		iTestCycleCaseStepService.delete(ConvertHelper.convertList(testCycleCaseStepDTO,TestCycleCaseStepE.class));
//	}


//	@Override
//	public Page<TestCycleCaseStepDTO> query(TestCycleCaseStepDTO testCycleCaseStepDTO, PageRequest pageRequest) {
//		Page<TestCycleCaseStepE> serviceEPage =  iTestCycleCaseStepService.query(ConvertHelper.convert(testCycleCaseStepDTO,TestCycleCaseStepE.class),pageRequest);
//		return ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseStepDTO.class);
//	}

	@Override
	public List<TestCycleCaseStepDTO> update(List<TestCycleCaseStepDTO> testCycleCaseStepDTO) {
		return ConvertHelper.convertList(iTestCycleCaseStepService.update(ConvertHelper.convertList(testCycleCaseStepDTO, TestCycleCaseStepE.class)), TestCycleCaseStepDTO.class);

	}

	@Override
	public List<TestCycleCaseStepDTO> querySubStep(Long cycleCaseId) {
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setCycleId(cycleCaseId);
		return ConvertHelper.convertList(iTestCycleCaseStepService.querySubStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseStepDTO.class);
	}

	@Override
	public void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO) {
		iTestCycleCaseStepService.createTestCycleCaseStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class));
	}

	@Override
	public void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO) {
		iTestCycleCaseStepService.deleteByTestCycleCase(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class));

	}
}
