package io.choerodon.test.manager.infra.aspect;

import java.util.List;

import io.choerodon.test.manager.api.vo.agile.IssueDTO;
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

import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseStepDTO;
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
    TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Autowired
    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    private static final String ATT_TYPE = "CYCLE_STEP";
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

    @After("execution(* io.choerodon.test.manager.app.service.impl.TestCycleCaseAttachmentRelUploadServiceImpl.baseUpload(..))")
    public void recordAttachUpload(JoinPoint jp) {
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_ATTACHMENT);
        if(ATT_TYPE.equals(String.valueOf(jp.getArgs()[4]))){
            TestCycleCaseStepDTO testCycleCaseStepDTO = testCycleCaseStepMapper.selectByPrimaryKey((Long) jp.getArgs()[3]);
            historyDTO.setExecuteId(testCycleCaseStepDTO.getExecuteId());
        }else {
            historyDTO.setExecuteId((Long) jp.getArgs()[3]);
        }
        historyDTO.setOldValue(TestCycleCaseHistoryType.FIELD_NULL);
        historyDTO.setNewValue(jp.getArgs()[1].toString());
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
            if(ATT_TYPE.equals(attachmentRelE.getAttachmentType())){
                TestCycleCaseStepDTO testCycleCaseStepDTO = testCycleCaseStepMapper.selectByPrimaryKey(attachmentRelE.getAttachmentLinkId());
                historyDTO.setExecuteId(testCycleCaseStepDTO.getExecuteId());
            }else {
                historyDTO.setExecuteId(attachmentRelE.getAttachmentLinkId());
            }
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
        TestCycleCaseStepDTO testCycleCaseStepDTO = testCycleCaseStepMapper.selectByPrimaryKey(testCycleCaseDefectRelVO.getDefectLinkId());
        historyDTO.setExecuteId(testCycleCaseStepDTO.getExecuteId());
        historyDTO.setOldValue(TestCycleCaseHistoryType.FIELD_NULL);
        IssueDTO issueDTO = testCaseService.queryIssue((Long) jp.getArgs()[1], testCycleCaseDefectRelVO.getIssueId(), (Long) jp.getArgs()[2]);
        if (ObjectUtils.isEmpty(issueDTO)) {
            return;
        }
        historyDTO.setNewValue(issueDTO.getIssueNum());
        testCycleCaseHistoryService.insert(historyDTO);
    }

    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.delete(..))&& args(testCycleCaseDefectRelVO,projectId,organizationId)")
    public Object recordDefectDelete(ProceedingJoinPoint pjp, TestCycleCaseDefectRelVO testCycleCaseDefectRelVO, Long projectId, Long organizationId) throws Throwable {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelE = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelE.setId(testCycleCaseDefectRelVO.getId());
        testCycleCaseDefectRelE = testCycleCaseDefectRelMapper.select(testCycleCaseDefectRelE).get(0);
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_DEFECT);
        TestCycleCaseStepDTO testCycleCaseStepDTO = testCycleCaseStepMapper.selectByPrimaryKey(testCycleCaseDefectRelE.getDefectLinkId());
        historyDTO.setExecuteId(testCycleCaseStepDTO.getExecuteId());
        IssueDTO issueDTO = testCaseService.queryIssue(projectId, testCycleCaseDefectRelE.getIssueId(), organizationId);
        if (!ObjectUtils.isEmpty(issueDTO)) {
            historyDTO.setOldValue(issueDTO.getIssueNum());
            historyDTO.setNewValue(TestCycleCaseHistoryType.FIELD_NULL);
            testCycleCaseHistoryService.insert(historyDTO);
        }
        Object o = pjp.proceed();
        return o;
    }
}
