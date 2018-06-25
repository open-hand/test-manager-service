package com.test.devops.app.service.impl;

import com.test.devops.api.dto.TestStatusDTO;
import com.test.devops.app.service.TestStatusService;
import com.test.devops.domain.entity.TestStatusE;
import com.test.devops.domain.factory.TestStatusEFactory;
import com.test.devops.domain.service.ITestStatusService;
import com.test.devops.infra.dataobject.TestStatusDO;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
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

	@Override
	public TestStatusDTO insert(TestStatusDTO testStatusDTO) {
		return ConvertHelper.convert(iTestStatusService.insert(ConvertHelper
				.convert(testStatusDTO, TestStatusE.class)), TestStatusDTO.class);
	}

	@Override
	public void delete(TestStatusDTO testStatusDTO) {
		iTestStatusService.delete(ConvertHelper
				.convert(testStatusDTO, TestStatusE.class));
	}

	@Override
	public TestStatusDTO update(TestStatusDTO testStatusDTO) {
		return ConvertHelper.convert(iTestStatusService.update(ConvertHelper
				.convert(testStatusDTO, TestStatusE.class)), TestStatusDTO.class);
	}
}
