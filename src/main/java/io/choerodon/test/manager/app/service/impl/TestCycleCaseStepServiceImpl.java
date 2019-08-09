package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.choerodon.test.manager.infra.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseStepServiceImpl implements TestCycleCaseStepService {

    @Value("${spring.datasource.url}")
    private String dsUrl;

    @Autowired
    private TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Autowired
    private TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCycleCaseStepVO> update(List<TestCycleCaseStepVO> testCycleCaseStepVO) {
        return modelMapper.map(baseUpdate(modelMapper.map(testCycleCaseStepVO, new TypeToken<List<TestCycleCaseStepDTO>>() {
        }.getType())), new TypeToken<List<TestCycleCaseStepVO>>() {
        }.getType());
    }

    @Override
    public List<TestCycleCaseStepVO> querySubStep(Long cycleCaseId, Long projectId, Long organizationId) {
        if (cycleCaseId == null) {
            throw new CommonException("error.test.cycle.case.step.caseId.not.null");
        }
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteId(cycleCaseId);
        List<TestCycleCaseStepDTO> testCycleCaseStepDTOS = testCycleCaseStepMapper.queryWithTestCaseStep(testCycleCaseStepDTO, null, null);
        if (testCycleCaseStepDTOS != null && !testCycleCaseStepDTOS.isEmpty()) {
//            List<TestCycleCaseStepVO> testCycleCaseStepVOS = modelMapper.map(testCycleCaseStepDTOS, new TypeToken<List<TestCycleCaseStepVO>>() {
//            }.getType());
            List<TestCycleCaseStepVO> testCycleCaseStepVOS = new ArrayList<>();
            testCycleCaseStepDTOS.forEach(testCycleCaseStep -> {
                TestCycleCaseStepVO testCycleCaseStepVO = modelMapper.map(testCycleCaseStep, TestCycleCaseStepVO.class);
                testCycleCaseStepVO.setDefects(modelMapper.map(testCycleCaseStep.getDefects(), new TypeToken<List<TestCycleCaseDefectRelVO>>(){}.getType()));
                testCycleCaseStepVO.setStepAttachment(modelMapper.map(testCycleCaseStep.getStepAttachment(), new TypeToken<List<TestCycleCaseAttachmentRelVO>>(){}.getType()));
                testCycleCaseStepVOS.add(testCycleCaseStepVO);
            });
            testCycleCaseDefectRelService.populateCaseStepDefectInfo(testCycleCaseStepVOS, projectId, organizationId);
            return testCycleCaseStepVOS;
        } else {
            return new ArrayList<>();
        }
    }

    private List<TestCycleCaseStepDTO> baseUpdate(List<TestCycleCaseStepDTO> list) {
        List<TestCycleCaseStepDTO> res = new ArrayList<>();
        list.forEach(v -> res.add(updateSelf(v)));

        return res;
    }

    private TestCycleCaseStepDTO updateSelf(TestCycleCaseStepDTO testCycleCaseStepDTO) {
        if (testCycleCaseStepMapper.updateByPrimaryKeySelective(testCycleCaseStepDTO) != 1) {
            throw new CommonException("error.testStepCase.update");
        }
        return testCycleCaseStepMapper.selectByPrimaryKey(testCycleCaseStepDTO.getExecuteStepId());
    }
}
