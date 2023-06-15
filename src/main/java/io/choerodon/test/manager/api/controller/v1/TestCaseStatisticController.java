package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.vo.TestCaseDailyStatisticVO;
import io.choerodon.test.manager.app.service.TestCaseStatisticService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author zhaotianxin
 * @date 2021-05-07 14:50
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/test_case/statistic")
public class TestCaseStatisticController {

    @Autowired
    private TestCaseStatisticService testCaseStatisticService;


    @Permission(permissionLogin = true)
    @ApiOperation("每日测试用例数")
    @PostMapping(value = "/daily")
    public ResponseEntity<Map<String, Map<String, Integer>>> dailyStatistic(@ApiParam(value = "项目id", required = true)
                                                                            @PathVariable(name = "project_id") Long projectId,
                                                                            @RequestBody @Validated TestCaseDailyStatisticVO testCaseDailyStatisticVO) {
        return ResponseEntity.ok(testCaseStatisticService.dailyStatistic(projectId, testCaseDailyStatisticVO));
    }
}
