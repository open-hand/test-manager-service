package io.choerodon.test.manager.infra.task;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.test.manager.app.service.DataMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FixDataTask {

    @Autowired
    private DataMigrationService dataMigrationService;

    @JobTask(maxRetryCount = 3,
            code = "migrationAgileToTestManager",
            description = "升级到0.20.0,同步敏捷数据到测试管理服务")
    @TimedTask(name = "migrationAgileToTestManager",
            description = "升级到0.20.0,同步敏捷数据到测试管理服务",
            oneExecution = true,
            repeatCount = 0,
            repeatInterval = 1,
            repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS,
            params = {})
    public void migrationAgileToTestManager(Map<String, Object> map) {
        dataMigrationService.fixData();
    }
}
