package io.choerodon.test.manager.api.controller.v1;

import java.util.List;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.test.manager.api.vo.DataLogVO;
import io.choerodon.test.manager.app.service.TestDataLogService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
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

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("根据caseId 查询日志记录")
    @GetMapping
    public ResponseEntity<List<DataLogVO>> queryByCaseId(@ApiParam(value = "项目id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "用例id", required = true)
                                                         @RequestParam(name = "case_id")
                                                         @Encrypt Long caseId){
        return  new ResponseEntity<>(dataLogService.queryByCaseId(projectId,caseId),HttpStatus.OK);
    }
}
