package io.choerodon.test.manager.api.eventhandler;

import io.choerodon.core.event.EventPayload;
import io.choerodon.event.consumer.annotation.EventListener;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.domain.test.manager.event.VersionEvent;
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
    public void handleProjectCreateEvent(EventPayload<VersionEvent> payload) {
        VersionEvent versionEvent = payload.getData();
        loggerInfo(versionEvent);
        TestCycleDTO testCycleDTO = new TestCycleDTO(versionEvent.getVersionId());
        testCycleService.insert(testCycleDTO);
    }

}
