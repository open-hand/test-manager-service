package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
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
public class TestCycleCaseDefectRelServiceImpl implements TestCycleCaseDefectRelService {
	@Autowired
	ITestCycleCaseDefectRelService iTestCycleCaseDefectRelService;

	@Override
	public TestCycleCaseDefectRelDTO insert(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
		return ConvertHelper.convert(iTestCycleCaseDefectRelService.insert(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class)), TestCycleCaseDefectRelDTO.class);
	}

	@Override
	public void delete(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
		iTestCycleCaseDefectRelService.delete(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class));

	}

//	@Override
//	public List<TestCycleCaseDefectRelDTO> update(List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelDTO) {
//		return ConvertHelper.convertList(iTestCycleCaseDefectRelService.update(ConvertHelper.convertList(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class)), TestCycleCaseDefectRelDTO.class);
//
//	}

	@Override
	public List<TestCycleCaseDefectRelDTO> query(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
		List<TestCycleCaseDefectRelE> serviceEPage = iTestCycleCaseDefectRelService.query(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class));
		return ConvertHelper.convertList(serviceEPage, TestCycleCaseDefectRelDTO.class);
	}
}
