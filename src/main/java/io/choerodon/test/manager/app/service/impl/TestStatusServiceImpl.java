package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestStatusDTO;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.core.convertor.ConvertHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
		// if(iTestStatusService.query(ConvertHelper.convert(testStatusDTO, TestStatusE.class)));
        iTestStatusService.delete(ConvertHelper
                .convert(testStatusDTO, TestStatusE.class));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestStatusDTO update(TestStatusDTO testStatusDTO) {
        return ConvertHelper.convert(iTestStatusService.update(ConvertHelper
                .convert(testStatusDTO, TestStatusE.class)), TestStatusDTO.class);
    }
}
