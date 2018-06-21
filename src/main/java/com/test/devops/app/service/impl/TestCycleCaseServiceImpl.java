package com.test.devops.app.service.impl;

import com.test.devops.api.dto.TestCycleCaseDTO;
import com.test.devops.api.dto.TestCycleCaseHistoryDTO;
import com.test.devops.app.service.TestCycleCaseService;
import com.test.devops.domain.entity.TestCycleCaseE;
import com.test.devops.domain.entity.TestCycleCaseHistoryE;
import com.test.devops.domain.service.ITestCycleCaseService;
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
public class TestCycleCaseServiceImpl implements TestCycleCaseService {
	@Autowired
	ITestCycleCaseService iTestCycleCaseService;

	@Override
	public void delete(Long cycleCaseId) {
		TestCycleCaseDTO dto = new TestCycleCaseDTO();
		dto.setExecuteId(cycleCaseId);
		iTestCycleCaseService.delete(ConvertHelper.convert(dto, TestCycleCaseE.class));
	}


	@Override
	public Page<TestCycleCaseDTO> query(TestCycleCaseDTO testCycleCaseDTO, PageRequest pageRequest) {
		Page<TestCycleCaseE> serviceEPage = iTestCycleCaseService.query(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), pageRequest);
		return ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseDTO.class);
	}

	@Override
	public List<TestCycleCaseDTO> queryByCycle(Long cycleId) {
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setCycleId(cycleId);
		List<TestCycleCaseE> serviceEPage = iTestCycleCaseService.query(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class));
		return ConvertHelper.convertList(serviceEPage, TestCycleCaseDTO.class);
	}

	@Override
	public TestCycleCaseDTO queryOne(Long cycleCaseId) {
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setExecuteId(cycleCaseId);
		return ConvertHelper.convert(iTestCycleCaseService.queryOne(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
	}

	@Override
	public TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
		return ConvertHelper.convert(iTestCycleCaseService.runTestCycleCase(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), projectId), TestCycleCaseDTO.class);
	}


	@Override
	public void changeOneCase(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
		iTestCycleCaseService.changeStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), projectId);
	}


}
