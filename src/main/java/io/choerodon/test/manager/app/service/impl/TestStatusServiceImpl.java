package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestStatusDTO;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@Component
public class TestStatusServiceImpl implements TestStatusService {

	@Autowired
	ITestStatusService iTestStatusService;

	@Override
	public List<TestStatusDTO> query(TestStatusDTO testStatusDTO) {
		return ConvertHelper.convertList(iTestStatusService.query(ConvertHelper
				.convert(testStatusDTO, TestStatusE.class)), TestStatusDTO.class);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestStatusDTO insert(TestStatusDTO testStatusDTO) {
		return ConvertHelper.convert(iTestStatusService.insert(ConvertHelper
				.convert(testStatusDTO, TestStatusE.class)), TestStatusDTO.class);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(TestStatusDTO testStatusDTO) {
		iTestStatusService.delete(ConvertHelper
				.convert(testStatusDTO, TestStatusE.class));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestStatusDTO update(TestStatusDTO testStatusDTO) {
		return ConvertHelper.convert(iTestStatusService.update(ConvertHelper
				.convert(testStatusDTO, TestStatusE.class)), TestStatusDTO.class);
	}

	public void populateStatus(TestCycleCaseDTO testCycleCaseDTO) {
		Assert.notNull(testCycleCaseDTO, "error.populateCycleCase.param.not.null");
		TestStatusE statusE = TestStatusEFactory.create();
		statusE.setStatusId(testCycleCaseDTO.getExecutionStatus());
		TestStatusE testStatusE = statusE.queryOne();
		if (!ObjectUtils.isEmpty(testStatusE)){
			testCycleCaseDTO.setExecutionStatusName(testStatusE.getStatusName());
		}
	}

	@Override
	public Long getDefaultStatusId(String type) {
		Assert.notNull(type, "error.get.default.id.param.not.");
		return iTestStatusService.getDefaultStatusId(type);
	}
}
