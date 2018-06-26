package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import io.choerodon.core.convertor.ConvertHelper;
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
//
//    @Override
//    public TestCycleCaseStepDTO createTestCycleCaseStep(TestCycleCaseStepDTO testCycleCaseStepDTO) {
//        return ConvertHelper.convert(iTestCycleCaseStepService.createTestCycleCaseStep(ConvertHelper.convert(testCycleCaseStepDTO, TestCycleCaseStepE.class)), TestCycleCaseStepDTO.class);
//    }
//
//    @Override
//    public void delete(List<TestCycleCaseStepDTO> testCycleCaseStepDTO) {
//        iTestCycleCaseStepService.delete(ConvertHelper.convertList(testCycleCaseStepDTO, TestCycleCaseStepE.class));
//    }
//
//
//    @Override
//    public Page<TestCycleCaseStepDTO> query(TestCycleCaseStepDTO testCycleCaseStepDTO, PageRequest pageRequest) {
//        Page<TestCycleCaseStepE> serviceEPage = iTestCycleCaseStepService.query(ConvertHelper.convert(testCycleCaseStepDTO, TestCycleCaseStepE.class), pageRequest);
//        return ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseStepDTO.class);
//    }

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
