package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseDefectRelServiceImpl implements TestCycleCaseDefectRelService {
    @Autowired
    ITestCycleCaseDefectRelService iTestCycleCaseDefectRelService;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseDefectRelDTO insert(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
		return ConvertHelper.convert(iTestCycleCaseDefectRelService.insert(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class)), TestCycleCaseDefectRelDTO.class);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
		iTestCycleCaseDefectRelService.delete(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class));

	}

    @Override
    public List<TestCycleCaseDefectRelDTO> query(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
        List<TestCycleCaseDefectRelE> serviceEPage = iTestCycleCaseDefectRelService.query(ConvertHelper.convert(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelE.class));
        return ConvertHelper.convertList(serviceEPage, TestCycleCaseDefectRelDTO.class);
    }
}
