//package io.choerodon.test.manager.infra.task;
//
//import io.choerodon.asgard.schedule.QuartzDefinition;
//import io.choerodon.asgard.schedule.annotation.JobTask;
//import io.choerodon.asgard.schedule.annotation.TimedTask;
//import io.choerodon.test.manager.app.service.DataMigrationService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Map;
//
//@Component
//public class FixDataTask {
//
//    @Autowired
//    private DataMigrationService dataMigrationService;

//    @JobTask(productSource = ZKnowDetailsHelper.VALUE_CHOERODON,
//    maxRetryCount = 3,
//            code = "fixDataPriority",
//            description = "升级到0.24.0,修复优先级数据")
//    @TimedTask(name = "fixDataPriority",
//            description = "升级到0.24.0,修复优先级数据",
//            oneExecution = true,
//            repeatCount = 0,
//            repeatInterval = 1,
//            repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS,
//            params = {})
//    public void fixDataPriority(Map<String, Object> map) {
//        dataMigrationService.fixDataTestCasePriority();
//    }
//}
