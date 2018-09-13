package io.choerodon.test.manager.domain.aop;


import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.*;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/28/18.
 */
@Aspect
@Component
public class TestCycleCaseHistoryRecordAOP {


	@Autowired
	TestCycleCaseHistoryService testCycleCaseHistoryService;


	@Autowired
	ITestCycleCaseService testCycleCaseService;

	@Autowired
	TestCaseFeignClient testCaseFeignClient;

	@Autowired
	TestCaseService testCaseService;

	@Autowired
	UserService userService;

	@Autowired
	TestStatusService testStatusService;


	@Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseService.changeOneCase(..)) && args(testCycleCaseDTO,projectId)")
	public Object afterTest(ProceedingJoinPoint pjp, TestCycleCaseDTO testCycleCaseDTO, Long projectId) throws Throwable {
		TestCycleCaseE case1 = TestCycleCaseEFactory.create();
		case1.setExecuteId(testCycleCaseDTO.getExecuteId());
		TestCycleCaseE before = testCycleCaseService.queryOne(case1);
		TestCycleCaseDTO beforeCeaseDTO = ConvertHelper.convert(before, TestCycleCaseDTO.class);
		testStatusService.populateStatus(beforeCeaseDTO);
		Object o = pjp.proceed();
		TestCycleCaseHistoryDTO historyDTO;

		if (testCycleCaseDTO.getExecutionStatus().longValue() != before.getExecutionStatus().longValue()) {
			historyDTO=testCycleCaseHistoryService.createStatusHistory(testCycleCaseDTO,beforeCeaseDTO);
		} else if (testCycleCaseDTO.getAssignedTo().longValue() != before.getAssignedTo().longValue()) {
			historyDTO=testCycleCaseHistoryService.createAssignedHistory(testCycleCaseDTO,beforeCeaseDTO);

		} else if (!StringUtils.equals(testCycleCaseDTO.getComment(), before.getComment())) {
			historyDTO=testCycleCaseHistoryService.createCommentHistory(testCycleCaseDTO,beforeCeaseDTO);
		} else {
			return o;
		}
		testCycleCaseHistoryService.insert(historyDTO);
		return o;
	}


	@After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.upload(..))")
	public void recordAttachUpload(JoinPoint jp) {

		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(TestCycleCaseHistoryE.FIELD_ATTACHMENT);
		historyDTO.setExecuteId((Long) jp.getArgs()[3]);
		historyDTO.setOldValue(TestCycleCaseHistoryE.FIELD_NULL);
		historyDTO.setNewValue(jp.getArgs()[1].toString());
		testCycleCaseHistoryService.insert(historyDTO);

	}

	@Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.delete(..))&& args(bucketName,attachId)")
	public Object recordAttachDelete(ProceedingJoinPoint pjp, String bucketName, Long attachId) throws Throwable {
		TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
		attachmentRelE.setId(attachId);
		List<TestCycleCaseAttachmentRelE> lists = attachmentRelE.querySelf();
		if (lists.size() != 1) {
			throw new CommonException("error.attach.notFound");
		}
		attachmentRelE = lists.get(0);
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setExecuteId(attachmentRelE.getAttachmentLinkId());
		historyDTO.setField(TestCycleCaseHistoryE.FIELD_ATTACHMENT);
		historyDTO.setOldValue(attachmentRelE.getAttachmentName());
		historyDTO.setNewValue(TestCycleCaseHistoryE.FIELD_NULL);
		Object o = pjp.proceed();
		testCycleCaseHistoryService.insert(historyDTO);
		return o;
	}

	@After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.insert(..))")
	public void recordDefectAdd(JoinPoint jp) {
		TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = (TestCycleCaseDefectRelDTO) jp.getArgs()[0];
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(TestCycleCaseHistoryE.FIELD_DEFECT);
		historyDTO.setExecuteId(testCycleCaseDefectRelDTO.getDefectLinkId());
		historyDTO.setOldValue(TestCycleCaseHistoryE.FIELD_NULL);
		String defectName = testCaseService.queryIssue((Long) jp.getArgs()[1], testCycleCaseDefectRelDTO.getIssueId()).getBody().getIssueNum();
		historyDTO.setNewValue(defectName);
		testCycleCaseHistoryService.insert(historyDTO);

	}

	@Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.delete(..))&& args(testCycleCaseDefectRelDTO,projectId)")
	public Object recordDefectDelete(ProceedingJoinPoint pjp, TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, Long projectId) throws Throwable {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		testCycleCaseDefectRelE.setId(testCycleCaseDefectRelDTO.getId());
		testCycleCaseDefectRelE = testCycleCaseDefectRelE.querySelf().get(0);
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(TestCycleCaseHistoryE.FIELD_DEFECT);
		historyDTO.setExecuteId(testCycleCaseDefectRelE.getDefectLinkId());
		List<Long> defectIds = new ArrayList<>();
		defectIds.add(testCycleCaseDefectRelE.getIssueId());
		String defectName = testCaseService.queryIssue(projectId, testCycleCaseDefectRelDTO.getIssueId()).getBody().getIssueNum();
		historyDTO.setOldValue(defectName);
		historyDTO.setNewValue(TestCycleCaseHistoryE.FIELD_NULL);
		Object o = pjp.proceed();
		testCycleCaseHistoryService.insert(historyDTO);
		return o;
	}
}
