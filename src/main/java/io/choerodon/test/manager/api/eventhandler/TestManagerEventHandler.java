package io.choerodon.test.manager.api.eventhandler;

import io.choerodon.core.event.EventPayload;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
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

    private static final String AGILE_SERVICE = "agile-service";

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCaseStepService testCaseStepService;

    @Autowired
    private TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    private static final Logger LOGGER = LoggerFactory.getLogger(TestManagerEventHandler.class);

    private void loggerInfo(Object o) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("data: {}", o);
        }
    }

    /**
     * 创建临时循环事件
     *
     * @param payload payload
     */
    @EventListener(topic = AGILE_SERVICE, businessType = "versionCreate")
    public void handleProjectVersionCreateEvent(EventPayload<VersionEvent> payload) {
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
     * @param payload payload
     */
    @EventListener(topic = AGILE_SERVICE, businessType = "versionDelete")
    public void handleProjectVersionDeleteEvent(EventPayload<VersionEvent> payload) {
        VersionEvent versionEvent = payload.getData();
        loggerInfo(versionEvent);
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        testCycleDTO.setVersionId(versionEvent.getVersionId());
        //  testCycleService.delete(testCycleDTO);
    }

    /**
     * 问题删除事件
     *
     * @param payload payload
     */
    @EventListener(topic = AGILE_SERVICE, businessType = "deleteIssue")
    public void handleProjectIssueDeleteEvent(EventPayload<IssuePayload> payload) {
        IssuePayload issuePayload = payload.getData();
        TestCycleCaseDefectRelE defectRelE=TestCycleCaseDefectRelEFactory.create();
        defectRelE.setIssueId(issuePayload.getIssueId());
        defectRelE.deleteSelf();
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		testCycleCaseDTO.setIssueId(issuePayload.getIssueId());
        //	testCycleCaseService.delete(testCycleCaseDTO);

		TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
		testCaseStepDTO.setIssueId(issuePayload.getIssueId());
		testCaseStepService.removeStep(testCaseStepDTO);
    }
}
