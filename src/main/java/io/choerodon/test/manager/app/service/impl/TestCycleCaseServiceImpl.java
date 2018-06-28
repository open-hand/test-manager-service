package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseServiceImpl implements TestCycleCaseService {
    @Autowired
    ITestCycleCaseService iTestCycleCaseService;

	@Autowired
	ITestCycleService iTestCycleService;

	@Transactional(rollbackFor = Exception.class)
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
	public Page<TestCycleCaseDTO> queryByCycle(Long cycleId, PageRequest pageRequest) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setCycleId(cycleId);
		Page<TestCycleCaseE> serviceEPage = iTestCycleCaseService.query(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), pageRequest);
		return ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseDTO.class);
    }

    @Override
    public TestCycleCaseDTO queryOne(Long cycleCaseId) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setExecuteId(cycleCaseId);
        return ConvertHelper.convert(iTestCycleCaseService.queryOne(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
    }

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
		if (testCycleCaseDTO.getCycleId() == null) {
			testCycleCaseDTO.setCycleId(iTestCycleService.findDefaultCycle(projectId));
		}
		return ConvertHelper.convert(iTestCycleCaseService.runTestCycleCase(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseDTO changeOneCase(TestCycleCaseDTO testCycleCaseDTO) {
		return ConvertHelper.convert(iTestCycleCaseService.changeStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
	}


}
