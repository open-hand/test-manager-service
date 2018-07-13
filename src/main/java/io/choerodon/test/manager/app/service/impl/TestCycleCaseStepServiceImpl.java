package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Lists;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    public Page<TestCycleCaseStepDTO> querySubStep(Long cycleCaseId, PageRequest pageRequest, Long projectId) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setExecuteId(cycleCaseId);
        return ConvertPageHelper.convertPage(iTestCycleCaseStepService.querySubStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), pageRequest, projectId), TestCycleCaseStepDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
	public void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
		iTestCycleCaseStepService.createTestCycleCaseStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), projectId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO) {
        iTestCycleCaseStepService.deleteByTestCycleCase(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class));

    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseStepDTO updateOneCase(List<MultipartFile> files, TestCycleCaseStepDTO testCycleCaseStepDTO, List<TestCycleCaseDefectRelDTO> defects) {
        files.forEach(v -> {
            testCycleCaseAttachmentRelService.upload(TestCycleCaseAttachmentRelE.ATTACHMENT_BUCKET, v.getOriginalFilename(), v, testCycleCaseStepDTO.getStepId(), TestCycleCaseAttachmentRelE.ATTACHMENT_CASE_STEP, null);
        });
        this.update(Lists.newArrayList(testCycleCaseStepDTO));

        defects.forEach(u -> {
            if (u.getId() == null) {
                testCycleCaseDefectRelService.insert(u);
            }
        });

        return testCycleCaseStepDTO;
    }

}
