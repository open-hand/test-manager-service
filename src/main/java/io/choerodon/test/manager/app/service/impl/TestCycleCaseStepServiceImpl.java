package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseStepServiceImpl implements TestCycleCaseStepService {
    @Autowired
    ITestCycleCaseStepService iTestCycleCaseStepService;

    @Autowired
    TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Autowired
    TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCycleCaseStepDTO> update(List<TestCycleCaseStepDTO> testCycleCaseStepDTO) {
        return ConvertHelper.convertList(iTestCycleCaseStepService.update(ConvertHelper.convertList(testCycleCaseStepDTO, TestCycleCaseStepE.class)), TestCycleCaseStepDTO.class);

    }

    @Override
    public Page<TestCycleCaseStepDTO> querySubStep(Long cycleCaseId, PageRequest pageRequest, Long projectId, Long organizationId) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setExecuteId(cycleCaseId);
        Page<TestCycleCaseStepDTO> dto=ConvertPageHelper.convertPage(iTestCycleCaseStepService.querySubStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), pageRequest, projectId), TestCycleCaseStepDTO.class);
        testCycleCaseDefectRelService.populateCaseStepDefectInfo(dto,projectId,organizationId);
        return dto;
    }

}
