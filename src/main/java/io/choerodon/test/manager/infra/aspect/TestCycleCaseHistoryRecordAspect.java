package io.choerodon.test.manager.infra.aspect;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestCycleCaseHistoryType;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.DBValidateUtil;

/**
 * Created by 842767365@qq.com on 6/28/18.
 */
@Aspect
@Component
public class TestCycleCaseHistoryRecordAspect {

    @Autowired
    private TestCycleCaseHistoryService testCycleCaseHistoryService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Autowired
    TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Autowired
    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    //修改用例
    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.update(..)) && args(testCycleCaseVO)")
    public Object afterTest(ProceedingJoinPoint pjp, TestCycleCaseVO testCycleCaseVO) throws Throwable {
        //执行历史
        TestCycleCaseDTO before = testCycleCaseMapper.selectByExecuteId(testCycleCaseVO.getExecuteId());
        TestCycleCaseVO beforeCeaseDTO = modelMapper.map(before, TestCycleCaseVO.class);
        Object o = pjp.proceed();
        if (!ObjectUtils.isEmpty(testCycleCaseVO.getExecutionStatus())&& testCycleCaseVO.getExecutionStatus().longValue() != beforeCeaseDTO.getExecutionStatus().longValue()) {
            testCycleCaseHistoryService.createStatusHistory(beforeCeaseDTO.getExecuteId(),beforeCeaseDTO.getExecutionStatusName(),testCycleCaseVO.getExecutionStatusName());
        }
        if (!ObjectUtils.isEmpty(testCycleCaseVO.getAssignedTo())&&!testCycleCaseVO.getAssignedTo().equals(before.getAssignedTo())) {
            testCycleCaseHistoryService.createAssignedHistory(testCycleCaseVO, beforeCeaseDTO);
        }
        if (!ObjectUtils.isEmpty(testCycleCaseVO.getDescription())&&!StringUtils.equals(testCycleCaseVO.getDescription(), before.getDescription())) {
            testCycleCaseHistoryService.createCommentHistory(beforeCeaseDTO.getExecuteId(),beforeCeaseDTO.getDescription(),testCycleCaseVO.getDescription());
        }
        return o;
    }

    //修改步骤
    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseStepService.update(..))&& args(testCycleCaseStepVO)")
    public Object afterTest(ProceedingJoinPoint pjp, TestCycleCaseStepVO testCycleCaseStepVO) throws Throwable {
        //执行历史
        TestCycleCaseStepDTO beforeCycleCaseStep = testCycleCaseStepMapper.selectByPrimaryKey(testCycleCaseStepVO.getExecuteStepId());
        TestStatusDTO testStatusDTO = testStatusMapper.selectByPrimaryKey(beforeCycleCaseStep.getStepStatus());
        Object[] args = pjp.getArgs();
        Object o = pjp.proceed();
        if (!ObjectUtils.isEmpty(testCycleCaseStepVO.getStepStatus())&& testCycleCaseStepVO.getStepStatus().longValue() != beforeCycleCaseStep.getStepStatus().longValue()) {
            testCycleCaseHistoryService.createStatusHistory(beforeCycleCaseStep.getExecuteId(),testStatusDTO.getStatusName(),testCycleCaseStepVO.getStatusName());
        }
        if (!ObjectUtils.isEmpty(testCycleCaseStepVO.getDescription())&&!StringUtils.equals(testCycleCaseStepVO.getDescription(), beforeCycleCaseStep.getDescription())) {
            testCycleCaseHistoryService.createCommentHistory(beforeCycleCaseStep.getExecuteId(),beforeCycleCaseStep.getDescription(), testCycleCaseStepVO.getDescription());
        }
        return o;
    }

    @After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.uploadMultipartFile(..))")
    public void recordAttachUpload(JoinPoint jp) {
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_ATTACHMENT);
        historyDTO.setExecuteId((Long) jp.getArgs()[1]);
        historyDTO.setOldValue(TestCycleCaseHistoryType.FIELD_NULL);
        historyDTO.setNewValue(jp.getArgs()[2].toString());
        testCycleCaseHistoryService.insert(historyDTO);
    }

    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.deleteAttachmentRel(..))&& args(attachId)")
    public Object recordAttachDelete(ProceedingJoinPoint pjp,Long attachId) throws Throwable {
        TestCycleCaseAttachmentRelDTO attachmentRelE = new TestCycleCaseAttachmentRelDTO();
        attachmentRelE.setId(attachId);
        List<TestCycleCaseAttachmentRelDTO> lists = testCycleCaseAttachmentRelMapper.select(attachmentRelE);
        Object o = pjp.proceed();
        if(!CollectionUtils.isEmpty(lists)){
            DBValidateUtil.executeAndvalidateUpdateNum(lists::size, 1, "error.attach.notFound");
            attachmentRelE = lists.get(0);
            TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
            historyDTO.setExecuteId(attachmentRelE.getAttachmentLinkId());
            historyDTO.setField(TestCycleCaseHistoryType.FIELD_ATTACHMENT);
            historyDTO.setOldValue(attachmentRelE.getAttachmentName());
            historyDTO.setNewValue(TestCycleCaseHistoryType.FIELD_NULL);
            testCycleCaseHistoryService.insert(historyDTO);
        }
        return o;
    }

    @After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.insert(..))")
    public void recordDefectAdd(JoinPoint jp) {
        TestCycleCaseDefectRelVO testCycleCaseDefectRelVO = (TestCycleCaseDefectRelVO) jp.getArgs()[0];
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_DEFECT);
        historyDTO.setExecuteId(testCycleCaseDefectRelVO.getDefectLinkId());
        historyDTO.setOldValue(TestCycleCaseHistoryType.FIELD_NULL);
        String defectName = testCaseService.queryIssue((Long) jp.getArgs()[1], testCycleCaseDefectRelVO.getIssueId(), (Long) jp.getArgs()[2]).getBody().getIssueNum();
        historyDTO.setNewValue(defectName);
        testCycleCaseHistoryService.insert(historyDTO);

    }

    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.delete(..))&& args(testCycleCaseDefectRelVO,projectId,organizationId)")
    public Object recordDefectDelete(ProceedingJoinPoint pjp, TestCycleCaseDefectRelVO testCycleCaseDefectRelVO, Long projectId, Long organizationId) throws Throwable {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelE = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelE.setId(testCycleCaseDefectRelVO.getId());
        testCycleCaseDefectRelE = testCycleCaseDefectRelMapper.select(testCycleCaseDefectRelE).get(0);
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_DEFECT);
        historyDTO.setExecuteId(testCycleCaseDefectRelE.getDefectLinkId());
        String defectName = testCaseService.queryIssue(projectId, testCycleCaseDefectRelE.getIssueId(), organizationId).getBody().getIssueNum();
        historyDTO.setOldValue(defectName);
        historyDTO.setNewValue(TestCycleCaseHistoryType.FIELD_NULL);
        Object o = pjp.proceed();
        testCycleCaseHistoryService.insert(historyDTO);
        return o;
    }
}
