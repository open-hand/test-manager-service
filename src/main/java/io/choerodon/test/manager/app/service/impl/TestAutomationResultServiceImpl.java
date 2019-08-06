package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.TestAutomationResultDTO;
import io.choerodon.test.manager.app.service.TestAutomationResultService;
import io.choerodon.test.manager.domain.service.ITestAutomationResultService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationResultE;

@Service
public class TestAutomationResultServiceImpl implements TestAutomationResultService {

    @Autowired
    private ITestAutomationResultService iTestAutomationResultService;

    @Override
    public List<TestAutomationResultDTO> query(TestAutomationResultDTO testAutomationResultDTO) {
        TestAutomationResultE testAutomationResultE = ConvertHelper.convert(testAutomationResultDTO, TestAutomationResultE.class);
        return ConvertHelper.convertList(iTestAutomationResultService.query(testAutomationResultE), TestAutomationResultDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestAutomationResultDTO changeAutomationResult(TestAutomationResultDTO testAutomationResultDTO, Long projectId) {
        Assert.notNull(testAutomationResultDTO,"error.change.testAutomationResult.param.not.null");
        TestAutomationResultE testAutomationResultE = ConvertHelper.convert(testAutomationResultDTO, TestAutomationResultE.class);
        if (testAutomationResultE.getId() == null) {
            testAutomationResultE = iTestAutomationResultService.add(testAutomationResultE);
        } else {
            testAutomationResultE = iTestAutomationResultService.update(testAutomationResultE);
        }

        return ConvertHelper.convert(testAutomationResultE, TestAutomationResultDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeAutomationResult(TestAutomationResultDTO testAutomationResultDTO) {
        iTestAutomationResultService.delete(ConvertHelper.convert(testAutomationResultDTO, TestAutomationResultE.class));
    }
}
