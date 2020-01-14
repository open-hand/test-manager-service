package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.DataLogVO;
import io.choerodon.test.manager.app.service.TestDataLogService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaotianxin
 * @since 2019/11/21
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/data_log")
public class TestCaseDataLogController {
    @Autowired
    private TestDataLogService dataLogService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据caseId 查询日志记录")
    @GetMapping
    public ResponseEntity<List<DataLogVO>> queryByCaseId(@PathVariable(name = "project_id") Long projectId,
                                                         @RequestParam(name = "case_id") Long caseId){
        return  new ResponseEntity<>(dataLogService.queryByCaseId(projectId,caseId),HttpStatus.OK);
    }
}
