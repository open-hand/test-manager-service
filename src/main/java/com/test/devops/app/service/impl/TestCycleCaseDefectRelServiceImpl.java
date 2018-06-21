package com.test.devops.app.service.impl;

import com.test.devops.api.dto.TestCycleCaseDefectRelDTO;
import com.test.devops.app.service.TestCycleCaseDefectRelService;
import com.test.devops.domain.entity.TestCycleCaseAttachmentRelE;
import com.test.devops.domain.entity.TestCycleCaseDefectRelE;
import com.test.devops.domain.service.ITestCycleCaseDefectRelService;
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
	public void delete(List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelDTO) {
		iTestCycleCaseDefectRelService.delete(ConvertHelper.convertList(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class));

	}

	@Override
	public List<TestCycleCaseDefectRelDTO> update(List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelDTO) {
		return ConvertHelper.convertList(iTestCycleCaseDefectRelService.update(ConvertHelper.convertList(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class)), TestCycleCaseDefectRelDTO.class);

	}

	@Override
	public Page<TestCycleCaseDefectRelDTO> query(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, PageRequest pageRequest) {
		Page<TestCycleCaseDefectRelE> serviceEPage = iTestCycleCaseDefectRelService.query(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class), pageRequest);
		return ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseDefectRelDTO.class);
	}
}
