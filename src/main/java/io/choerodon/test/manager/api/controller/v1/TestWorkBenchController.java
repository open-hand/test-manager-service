package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.vo.CaseSearchVO;
import io.choerodon.test.manager.api.vo.TestFolderCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestStatusVO;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author huaxin.deng@hand-china.com 2021-03-02 13:48:55
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/test_work_bench")
public class TestWorkBenchController {

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation("工作台查询我执行的用例")
    @PostMapping("/personal/my_execution_case")
    public ResponseEntity<Page<TestFolderCycleCaseVO>> pagedQueryMyExecutionalCase(@ApiParam(value = "组织id", required = true)
                                                                                   @PathVariable(name = "organization_id") Long organizationId,
                                                                                   @ApiParam(value = "项目id")
                                                                                   @RequestParam(required = false) Long projectId,
                                                                                   @ApiParam(value = "分页信息", required = true)
                                                                                   PageRequest pageRequest,
                                                                                   @ApiParam(value = "查询参数")
                                                                                   @RequestBody(required = false) CaseSearchVO caseSearchVO) {
        return ResponseEntity.ok(testCycleCaseService.pagedQueryMyExecutionalCase(organizationId, projectId, pageRequest, caseSearchVO));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation("工作台查询我执行的用例状态")
    @PostMapping("/personal/my_execution_case/status/query")
    public ResponseEntity<Page<TestStatusVO>> pageQueryCaseStatus(@ApiParam(value = "组织id", required = true)
                                                                  @PathVariable(name="organization_id") Long organizationId,
                                                                  @ApiParam(value = "项目id")
                                                                  @RequestParam(required = false) Long projectId,
                                                                  @ApiParam(value = "分页信息", required = true)
                                                                  PageRequest pageRequest,
                                                                  @ApiParam(value = "查询参数")
                                                                  @RequestParam(required = false) String param){
        return ResponseEntity.ok(testCycleCaseService.pageQueryCaseStatus(organizationId, projectId, pageRequest, param));
    }
}
