package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.app.service.TestCaseLinkService;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaotianxin
 * @since 2019/11/19
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/case_link")
public class TestCaseLinkController {

    @Autowired
    private TestCaseLinkService testCaseLinkService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建问题链接")
    @PostMapping
    public ResponseEntity<TestCaseLinkDTO> create(@PathVariable(name = "project_id") Long projectId,
                                                  @RequestBody TestCaseLinkDTO testCaseLinkDTO) {
     return  new ResponseEntity<>(testCaseLinkService.create(projectId,testCaseLinkDTO), HttpStatus.OK);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("解除关联issue")
    @DeleteMapping
    public ResponseEntity delete(@PathVariable(name = "project_id") Long projectId,
                                                  @RequestParam(required = true) Long linkId) {
        testCaseLinkService.delete(projectId,linkId);
        return  new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
