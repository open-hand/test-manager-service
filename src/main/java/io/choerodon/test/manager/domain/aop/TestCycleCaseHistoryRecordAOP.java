package io.choerodon.test.manager.domain.aop;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
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
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/28/18.
 */
@Aspect
@Component
public class TestCycleCaseHistoryRecordAOP {

	private final String FIELD_STATUS = "执行状态";
	private final String FIELD_ASSIGNED = "已指定至";
	private final String FIELD_ATTACHMENT = "附件";
	private final String FIELD_DEFECT = "缺陷";
	private final String FIELD_COMMENT = "注释";

	private final String FIELD_NULL = " ";
	@Autowired
	TestCycleCaseHistoryService testCycleCaseHistoryService;


	@Autowired
	ITestCycleCaseService testCycleCaseService;

	@Autowired
	TestCaseFeignClient testCaseFeignClient;

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
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setExecuteId(before.getExecuteId());

		if (testCycleCaseDTO.getExecutionStatus().longValue() != before.getExecutionStatus().longValue()) {
			String newColor = testCycleCaseDTO.getExecutionStatusName();
			String oldColor = beforeCeaseDTO.getExecutionStatusName();
			historyDTO.setField(FIELD_STATUS);
			historyDTO.setNewValue(newColor);
			historyDTO.setOldValue(oldColor);
		} else if (testCycleCaseDTO.getAssignedTo().longValue() != before.getAssignedTo().longValue()) {
			historyDTO.setField(FIELD_ASSIGNED);
			Long after_as = testCycleCaseDTO.getAssignedTo();
			Long before_as = before.getAssignedTo();
			Long[] para = new Long[]{before_as, after_as};
			Map<Long, UserDO> users = userService.query(para);

			if (before_as != null && before_as.longValue() != 0) {
				UserDO u = users.get(before_as);
				historyDTO.setOldValue(u.getLoginName() + u.getRealName());
			} else {
				historyDTO.setOldValue(FIELD_NULL);
			}
			if (before_as != null && after_as.longValue() != 0) {
				UserDO u = users.get(after_as);
				historyDTO.setNewValue(u.getLoginName() + u.getRealName());
			} else {
				historyDTO.setNewValue(FIELD_NULL);
			}

		} else if (!StringUtils.equals(testCycleCaseDTO.getComment(), before.getComment())) {
			historyDTO.setField(FIELD_COMMENT);
			if (StringUtils.isEmpty(testCycleCaseDTO.getComment())) {
				historyDTO.setNewValue(FIELD_NULL);
			} else {
				historyDTO.setNewValue(testCycleCaseDTO.getComment());
			}
			if (StringUtils.isEmpty(before.getComment())) {
				historyDTO.setOldValue(FIELD_NULL);
			} else {
				historyDTO.setOldValue(before.getComment());
			}
		} else {
			return o;
		}
		testCycleCaseHistoryService.insert(historyDTO);
		return o;
	}


	@After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService.upload(..))")
	public void recordAttachUpload(JoinPoint jp) {

		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(FIELD_ATTACHMENT);
		historyDTO.setExecuteId((Long) jp.getArgs()[3]);
		historyDTO.setOldValue(FIELD_NULL);
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
		historyDTO.setField(FIELD_ATTACHMENT);
		historyDTO.setOldValue(attachmentRelE.getAttachmentName());
		historyDTO.setNewValue(FIELD_NULL);
		Object o = pjp.proceed();
		testCycleCaseHistoryService.insert(historyDTO);
		return o;
	}

	@After("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.insert(..))")
	public void recordDefectAdd(JoinPoint jp) {
		TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = (TestCycleCaseDefectRelDTO) jp.getArgs()[0];
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(FIELD_DEFECT);
		historyDTO.setExecuteId(testCycleCaseDefectRelDTO.getDefectLinkId());
		historyDTO.setOldValue(FIELD_NULL);
		List<Long> defectIds = new ArrayList<>();
		defectIds.add(testCycleCaseDefectRelDTO.getIssueId());
		String defectName = testCaseFeignClient.listByIssueIds((Long) jp.getArgs()[1], defectIds).getBody().get(0).getIssueNum();

		historyDTO.setNewValue(defectName);
		testCycleCaseHistoryService.insert(historyDTO);

	}

	@Around("execution(* io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService.delete(..))&& args(testCycleCaseDefectRelDTO,projectId)")
	public Object recordDefectDelete(ProceedingJoinPoint pjp, TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, Long projectId) throws Throwable {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		testCycleCaseDefectRelE.setId(testCycleCaseDefectRelDTO.getId());
		testCycleCaseDefectRelE = testCycleCaseDefectRelE.querySelf().get(0);
		TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
		historyDTO.setField(FIELD_DEFECT);
		historyDTO.setExecuteId(testCycleCaseDefectRelE.getDefectLinkId());
		List<Long> defectIds = new ArrayList<>();
		defectIds.add(testCycleCaseDefectRelE.getIssueId());
		String defectName = testCaseFeignClient.listByIssueIds(projectId, defectIds).getBody().get(0).getIssueNum();

		historyDTO.setOldValue(defectName);
		historyDTO.setNewValue(FIELD_NULL);
		Object o = pjp.proceed();
		testCycleCaseHistoryService.insert(historyDTO);
		return o;
	}
}
