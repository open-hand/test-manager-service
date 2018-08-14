package io.choerodon.test.manager.api.eventhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.SagaTask;
import io.choerodon.core.event.EventPayload;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

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

	@Qualifier("objectMapper")
	@Autowired
	private ObjectMapper objectMapper;

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
	public void handleProjectVersionCreateEvent(String message) throws IOException {
		EventPayload<VersionEvent> payload = objectMapper.readValue(message, EventPayload.class);
		VersionEvent versionEvent = payload.getData();
        loggerInfo(versionEvent);
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setVersionId(versionEvent.getVersionId());
        testCycleDTO.setType(TestCycleE.TEMP);
        testCycleDTO.setCycleName("临时");
        testCycleService.insert(testCycleDTO);
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
    public void handleProjectVersionDeleteEvent(String message) throws IOException {
		EventPayload<VersionEvent> payload = objectMapper.readValue(message, EventPayload.class);

		VersionEvent versionEvent = payload.getData();
        loggerInfo(versionEvent);
        TestCycleDTO testCycleDTO =	new TestCycleDTO();
        testCycleDTO.setVersionId(versionEvent.getVersionId());
          testCycleService.delete(testCycleDTO,versionEvent.getProjectId());
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
    public void handleProjectIssueDeleteEvent(String message) throws IOException {
		EventPayload<IssuePayload> payload = objectMapper.readValue(message, EventPayload.class);

		IssuePayload issuePayload = payload.getData();
        TestCycleCaseDefectRelE defectRelE=TestCycleCaseDefectRelEFactory.create();
        defectRelE.setIssueId(issuePayload.getIssueId());
        defectRelE.deleteSelf();
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setIssueId(issuePayload.getIssueId());
		testCycleCaseService.batchDelete(testCycleCaseDTO,issuePayload.getProjectId());

		TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
		testCaseStepDTO.setIssueId(issuePayload.getIssueId());
		testCaseStepService.removeStep(testCaseStepDTO);
    }
}
