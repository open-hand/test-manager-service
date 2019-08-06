package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseStepServiceImpl implements TestCycleCaseStepService {

    @Value("${spring.datasource.url}")
    private String dsUrl;

    @Autowired
    TestCycleCaseStepMapper testCycleCaseStepMapper;
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
    public List<TestCycleCaseStepDTO> querySubStep(Long cycleCaseId, Long projectId, Long organizationId) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setExecuteId(cycleCaseId);
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        testCycleCaseStepE.setExecuteId(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class).getExecuteId());
        if (testCycleCaseStepE.getExecuteId() == null) {
            throw new CommonException("error.test.cycle.case.step.caseId.not.null");
        }
        List<TestCycleCaseStepDO> testCycleCaseStepDOS = testCycleCaseStepMapper.queryWithTestCaseStep(ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class), null, null);
        if (testCycleCaseStepDOS != null && !testCycleCaseStepDOS.isEmpty()) {
            List<TestCycleCaseStepDTO> testCycleCaseStepDTOS = ConvertHelper.convertList(ConvertHelper.convertList(testCycleCaseStepDOS, TestCycleCaseStepE.class), TestCycleCaseStepDTO.class);
            testCycleCaseDefectRelService.populateCaseStepDefectInfo(testCycleCaseStepDTOS, projectId, organizationId);
            return testCycleCaseStepDTOS;
        } else {
            return new ArrayList<>();
        }
    }

}
