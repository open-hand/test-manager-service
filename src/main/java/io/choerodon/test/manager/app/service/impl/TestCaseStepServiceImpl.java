package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import io.choerodon.test.manager.app.assembler.TestCaseAssembler;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.mapper.*;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.test.manager.infra.util.RankUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCaseStepVO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.infra.enums.TestCycleCaseDefectCode;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import org.springframework.util.CollectionUtils;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class TestCaseStepServiceImpl implements TestCaseStepService {

    private static final String ERROR_STEP_ID_NOT_NULL = "error.case.step.insert.stepId.should.be.null";

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCaseAssembler testCaseAssembler;

    @Override
    public void removeStep(Long projectId,TestCaseStepVO testCaseStepVO) {
        Assert.notNull(modelMapper.map(testCaseStepVO, TestCaseStepDTO.class), "error.case.step.remove.param.not.null");
        testCaseStepMapper.delete(modelMapper.map(testCaseStepVO, TestCaseStepDTO.class));
        testCaseService.updateVersionNum(testCaseStepVO.getIssueId());
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(projectId,testCaseStepVO.getIssueId());
        if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
            testCaseAssembler.AutoAsyncCase(testCycleCaseDTOS,false,true,false);
        }
    }


    @Override
    public List<TestCaseStepVO> query(TestCaseStepVO testCaseStepVO) {
        return modelMapper.map(testCaseStepMapper.query(modelMapper
                .map(testCaseStepVO, TestCaseStepDTO.class)), new TypeToken<List<TestCaseStepVO>>() {
        }.getType());
    }

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
           List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(projectId,testCaseStepVO.getIssueId());
           if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
                testCaseAssembler.AutoAsyncCase(testCycleCaseDTOS,false,true,false);
           }
        }
        return modelMapper.map(testCaseStepDTO, TestCaseStepVO.class);
    }

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
        return changeStep(modelMapper.map(testCaseStepProDTO, TestCaseStepVO.class), projectId,true);
    }

    /**
     * @param testCaseStepVO 要查找的casstep
     * @param issueId        要被插入数据的issueid
     * @param projectId
     */
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
        testCaseService.updateVersionNum(issueId);
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(projectId,testCaseStepVO.getIssueId());
        if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
            testCaseAssembler.AutoAsyncCase(testCycleCaseDTOS,false,true,false);
        }
        return testCaseStepVOS;
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
    public void removeStepByIssueId(Long projectId,Long caseId) {
        // 查询是否含有步骤，又步骤再删除
        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
        testCaseStepDTO.setIssueId(caseId);
        List<TestCaseStepDTO> list = testCaseStepMapper.query(testCaseStepDTO);
        if(CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(v -> {
            removeStep(projectId,modelMapper.map(v,TestCaseStepVO.class));
        });
    }

    @Override
    public List<TestCaseStepDTO> listByCaseIds(List<Long> caseIds) {
        return testCaseStepMapper.listByCaseIds(caseIds);
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
            testCaseStepProDTO.setRank(RankUtil.Operation.UPDATE.getRank(testCaseStepProDTO.getLastRank(), testCaseStepProDTO.getNextRank()));
        }
        TestCaseStepDTO testCaseStepDTO = modelMapper.map(testCaseStepProDTO, TestCaseStepDTO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseStepMapper::updateByPrimaryKeySelective, testCaseStepDTO, 1, "error.testStepCase.update");
        return testCaseStepMapper.query(testCaseStepDTO).get(0);
    }

}
