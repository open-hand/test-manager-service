package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestPlanTreeVO;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.api.vo.TestTreeIssueFolderVO;
import io.choerodon.test.manager.app.service.TestPlanServcie;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaotianxin
 * @since 2019/11/26
 */
@RestController
@RequestMapping("/v1/project/{project_id}/plan")
public class TestPlanController {
    @Autowired
    private TestPlanServcie testPlanServcie;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建测试计划")
    @PostMapping
    public ResponseEntity<TestPlanDTO> create(@PathVariable("project_id") Long projectId,
                                              @RequestBody TestPlanVO testPlanVO) {
        return new ResponseEntity<>(testPlanServcie.create(projectId, testPlanVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("测试计划树形展示")
    @PostMapping("/tree")
    public ResponseEntity<TestTreeIssueFolderVO> queryTree(@PathVariable("project_id") Long projectId,
                                                           @RequestParam("status_code") String statusCode) {
        return new ResponseEntity<>(testPlanServcie.ListPlanAndFolderTree(projectId, statusCode), HttpStatus.OK);
    }
}
