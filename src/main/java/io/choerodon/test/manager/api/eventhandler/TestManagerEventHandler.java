package io.choerodon.test.manager.api.eventhandler;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.event.IssuePayload;
import io.choerodon.test.manager.domain.test.manager.event.VersionEvent;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by WangZhe@choerodon.io on 2018/6/25.
 * Email: ettwz@hotmail.com
 */
@Component
public class TestManagerEventHandler {

	@Autowired
	private TestCycleService testCycleService;

	@Autowired
	private TestCycleCaseService testCycleCaseService;

	@Autowired
	private TestCaseStepService testCaseStepService;

	private ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger LOGGER = LoggerFactory.getLogger(TestManagerEventHandler.class);

	private void loggerInfo(Object o) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("data: {}", o);
		}
	}

	/**
	 * 创建临时循环事件
	 *
	 * @param message
	 */
	@SagaTask(code = "test-create-version",
			description = "创建临时循环事件",
			sagaCode = "agile-create-version",
			//enabledDbRecord = true,
			seq = 1)
	public VersionEvent handleProjectVersionCreateEvent(String message) throws IOException {
		VersionEvent versionEvent = objectMapper.readValue(message, VersionEvent.class);
		loggerInfo(versionEvent);
		TestCycleDTO testCycleDTO = new TestCycleDTO();
		testCycleDTO.setVersionId(versionEvent.getVersionId());
		testCycleDTO.setType(TestCycleE.TEMP);
		testCycleDTO.setCycleName("临时");
		testCycleService.insert(testCycleDTO);
		return versionEvent;
	}

	/**
	 * 版本删除事件
	 *
	 * @param message
	 */
	@SagaTask(code = "test-delete-version",
			description = "删除version事件，删除相关测试数据",
			sagaCode = "agile-delete-version",
			//enabledDbRecord = true,
			seq = 1)
	public VersionEvent handleProjectVersionDeleteEvent(String message) throws IOException {
		VersionEvent versionEvent = objectMapper.readValue(message, VersionEvent.class);
		loggerInfo(versionEvent);
		TestCycleDTO testCycleDTO = new TestCycleDTO();
		testCycleDTO.setVersionId(versionEvent.getVersionId());
		testCycleService.delete(testCycleDTO, versionEvent.getProjectId());
		return versionEvent;
	}

	/**
	 * 问题删除事件
	 *
	 * @param message
	 */
	@SagaTask(code = "test-delete-issue",
			description = "删除issue事件，删除相关测试数据",
			sagaCode = "agile-delete-issue",
			// enabledDbRecord = true,
			seq = 1)
	public IssuePayload handleProjectIssueDeleteEvent(String message) throws IOException {
		IssuePayload issuePayload = objectMapper.readValue(message, IssuePayload.class);
		TestCycleCaseDefectRelE defectRelE = TestCycleCaseDefectRelEFactory.create();
		defectRelE.setIssueId(issuePayload.getIssueId());
		defectRelE.deleteSelf();
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setIssueId(issuePayload.getIssueId());
		testCycleCaseService.batchDelete(testCycleCaseDTO, issuePayload.getProjectId());

		TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
		testCaseStepDTO.setIssueId(issuePayload.getIssueId());
		testCaseStepService.removeStep(testCaseStepDTO);
		return issuePayload;
	}
}
