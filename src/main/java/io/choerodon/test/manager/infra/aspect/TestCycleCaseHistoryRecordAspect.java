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

import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.infra.enums.TestCycleCaseHistoryType;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper;
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
    private TestStatusService testStatusService;

    @Autowired
    TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Autowired
    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCaseFeignClient testCaseFeignClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.update(..)) && args(testCycleCaseVO)")
    public Object afterTest(ProceedingJoinPoint pjp, TestCycleCaseVO testCycleCaseVO) throws Throwable {
        //执行历史
        TestCycleCaseDTO before = testCycleCaseMapper.selectByExecuteId(testCycleCaseVO.getExecuteId());
        TestCycleCaseVO beforeCeaseDTO = modelMapper.map(before, TestCycleCaseVO.class);
        Object o = pjp.proceed();
        if (!ObjectUtils.isEmpty(testCycleCaseVO.getExecutionStatus())&& testCycleCaseVO.getExecutionStatus().longValue() != before.getExecutionStatus().longValue()) {
            testCycleCaseHistoryService.createStatusHistory(testCycleCaseVO, beforeCeaseDTO);
        }
        if (!ObjectUtils.isEmpty(testCycleCaseVO.getAssignedTo())&&!testCycleCaseVO.getAssignedTo().equals(before.getAssignedTo())) {
            testCycleCaseHistoryService.createAssignedHistory(testCycleCaseVO, beforeCeaseDTO);
        }
        if (!ObjectUtils.isEmpty(testCycleCaseVO.getDescription())&&!StringUtils.equals(testCycleCaseVO.getDescription(), before.getDescription())) {
            testCycleCaseHistoryService.createCommentHistory(testCycleCaseVO, beforeCeaseDTO);
        }
        return o;
    }


    @After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.upload(..))")
    public void recordAttachUpload(JoinPoint jp) {
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_ATTACHMENT);
        historyDTO.setExecuteId((Long) jp.getArgs()[3]);
        historyDTO.setOldValue(TestCycleCaseHistoryType.FIELD_NULL);
        historyDTO.setNewValue(jp.getArgs()[1].toString());
        testCycleCaseHistoryService.insert(historyDTO);
    }

    @Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.delete(..))&& args(linkedId,type)")
    public Object recordAttachDelete(ProceedingJoinPoint pjp,Long linkedId, String type) throws Throwable {
        TestCycleCaseAttachmentRelDTO attachmentRelE = new TestCycleCaseAttachmentRelDTO();
        attachmentRelE.setId(linkedId);
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
