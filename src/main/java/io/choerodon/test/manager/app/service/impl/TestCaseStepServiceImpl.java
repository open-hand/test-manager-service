package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ch.qos.logback.core.pattern.ConverterUtil;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCaseStepVO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.enums.TestCycleCaseDefectCode;
import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import org.springframework.util.CollectionUtils;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCaseStepServiceImpl implements TestCaseStepService {

    private static final String ERROR_STEP_ID_NOT_NULL = "error.case.step.insert.stepId.should.be.null";

    @Autowired
    private TestCycleCaseAttachmentRelService attachmentRelService;

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCaseService testCaseService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeStep(TestCaseStepVO testCaseStepVO) {
        Assert.notNull(modelMapper.map(testCaseStepVO, TestCaseStepDTO.class), "error.case.step.remove.param.not.null");
        Optional.ofNullable(testCaseStepMapper.query(modelMapper.map(testCaseStepVO, TestCaseStepDTO.class))).ifPresent(m ->
                m.forEach(v -> {
                    deleteCycleCaseStep(v);
                    attachmentRelService.delete(v.getStepId(), TestAttachmentCode.ATTACHMENT_CASE_STEP);
                })
        );
        testCaseStepMapper.delete(modelMapper.map(testCaseStepVO, TestCaseStepDTO.class));
        testCaseService.updateVersionNum(testCaseStepVO.getIssueId());
    }


    @Override
    public List<TestCaseStepVO> query(TestCaseStepVO testCaseStepVO) {
        return modelMapper.map(testCaseStepMapper.query(modelMapper
                .map(testCaseStepVO, TestCaseStepDTO.class)), new TypeToken<List<TestCaseStepVO>>() {
        }.getType());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCaseStepVO changeStep(TestCaseStepVO testCaseStepVO, Long projectId,Boolean changeVersionNum) {
        Assert.notNull(testCaseStepVO, "error.case.change.step.param.not.null");
        TestCaseStepProDTO testCaseStepProDTO = modelMapper.map(testCaseStepVO, TestCaseStepProDTO.class);
        TestCaseStepDTO testCaseStepDTO;
        if (testCaseStepProDTO.getStepId() == null) {
            testCaseStepDTO = createOneStep(testCaseStepProDTO);
        } else {
            testCaseStepDTO = changeOneStep(testCaseStepProDTO);
        }
        if (changeVersionNum) {
           testCaseService.updateVersionNum(testCaseStepVO.getIssueId());
        }
        testCaseService.updateVersionNum(testCaseStepVO.getIssueId());
        return modelMapper.map(testCaseStepDTO, TestCaseStepVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCaseStepVO clone(TestCaseStepVO testCaseStepVO, Long projectId) {
        List<TestCaseStepDTO> steps = testCaseStepMapper.query(modelMapper.map(testCaseStepVO, TestCaseStepDTO.class));
        if (steps.size() != 1) {
            throw new CommonException("error.clone.case.step");
        }
        TestCaseStepProDTO testCaseStepProDTO = modelMapper.map(steps.get(0), TestCaseStepProDTO.class);
        testCaseStepProDTO.setStepId(null);
        testCaseStepProDTO.setObjectVersionNumber(null);
        testCaseStepProDTO.setLastRank(testCaseStepVO.getLastRank());
        testCaseStepProDTO.setNextRank(testCaseStepVO.getNextRank());
        return changeStep(modelMapper.map(testCaseStepProDTO, TestCaseStepVO.class), projectId,false);
    }

    /**
     * @param testCaseStepVO 要查找的casstep
     * @param issueId        要被插入数据的issueid
     * @param projectId
     */
    @Transactional
    @Override
    public List<TestCaseStepVO> batchClone(TestCaseStepVO testCaseStepVO, Long issueId, Long projectId) {
        List<TestCaseStepDTO> steps = testCaseStepMapper.select(modelMapper.map(testCaseStepVO, TestCaseStepDTO.class));
        List<TestCaseStepVO> testCaseStepVOS = new ArrayList<>();
        steps.forEach(v -> {
            v.setStepId(null);
            v.setIssueId(issueId);
            v.setObjectVersionNumber(null);
            TestCaseStepVO resCaseStepDTO = changeStep(modelMapper.map(v, TestCaseStepVO.class), projectId,false);
            testCaseStepVOS.add(resCaseStepDTO);
        });
        return testCaseStepVOS;
    }

    private void deleteCycleCaseStep(TestCaseStepDTO testCaseStepDTO) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setStepId(testCaseStepDTO.getStepId());
        Optional.ofNullable(testCycleCaseStepMapper.select(testCycleCaseStepDTO)).ifPresent(
                m -> m.forEach(v -> {
                    attachmentRelService.delete(v.getExecuteStepId(), TestAttachmentCode.ATTACHMENT_CYCLE_STEP);
                    deleteLinkedDefect(v.getExecuteStepId());
                })
        );
        testCycleCaseStepMapper.delete(testCycleCaseStepDTO);
    }

    private void deleteLinkedDefect(Long stepId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setDefectLinkId(stepId);
        testCycleCaseDefectRelDTO.setDefectType(TestCycleCaseDefectCode.CASE_STEP);
        testCycleCaseDefectRelMapper.select(testCycleCaseDefectRelDTO).forEach(v -> {
            TestCycleCaseDefectRelDTO convert = modelMapper.map(v, TestCycleCaseDefectRelDTO.class);
            testCycleCaseDefectRelMapper.delete(convert);
        });
    }

    @Override
    public TestCaseStepDTO createOneStep(TestCaseStepProDTO testCaseStepProDTO) {
        if (testCaseStepProDTO.getLastRank() == null) {
            testCaseStepProDTO.setLastRank(getLastedStepRank(testCaseStepProDTO.getIssueId()));
        }
        testCaseStepProDTO.setRank(RankUtil.Operation.INSERT.getRank(testCaseStepProDTO.getLastRank(), testCaseStepProDTO.getNextRank()));
        return baseInsert(testCaseStepProDTO);
    }

    @Override
    public void removeStepByIssueId(Long caseId) {
        // 查询是否含有步骤，又步骤再删除
        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
        testCaseStepDTO.setIssueId(caseId);
        List<TestCaseStepDTO> list = testCaseStepMapper.query(testCaseStepDTO);
        if(CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(v -> {
            removeStep(modelMapper.map(v,TestCaseStepVO.class));
        });
    }

    private String getLastedStepRank(Long issueId) {
        Assert.notNull(issueId, "error.case.step.insert.issueId.not.null");
        return testCaseStepMapper.getLastedRank(issueId);
    }

    private TestCaseStepDTO baseInsert(TestCaseStepProDTO testCaseStepProDTO) {
        if (testCaseStepProDTO == null || testCaseStepProDTO.getStepId() != null) {
            throw new CommonException(ERROR_STEP_ID_NOT_NULL);
        }
        TestCaseStepDTO testCaseStepDTO = modelMapper.map(testCaseStepProDTO, TestCaseStepDTO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseStepMapper::insert, testCaseStepDTO, 1, "error.testStepCase.insert");
        return testCaseStepDTO;
    }

    private TestCaseStepDTO changeOneStep(TestCaseStepProDTO testCaseStepProDTO) {
        if (!StringUtils.isEmpty(testCaseStepProDTO.getLastRank()) || !StringUtils.isEmpty(testCaseStepProDTO.getNextRank())) {
            testCaseStepProDTO.setLastRank(RankUtil.Operation.UPDATE.getRank(testCaseStepProDTO.getLastRank(), testCaseStepProDTO.getNextRank()));
        }
        TestCaseStepDTO testCaseStepDTO = modelMapper.map(testCaseStepProDTO, TestCaseStepDTO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseStepMapper::updateByPrimaryKey, testCaseStepDTO, 1, "error.testStepCase.update");
        return testCaseStepMapper.query(testCaseStepDTO).get(0);
    }

}
