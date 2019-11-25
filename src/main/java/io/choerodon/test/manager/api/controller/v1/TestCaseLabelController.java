package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.app.service.TestCaseLabelService;
import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaotianxin
 * @since 2019/11/25
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/labels")
public class TestCaseLabelController {
    @Autowired
    private TestCaseLabelService testCaseLabelService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据项目ID查询标签")
    @GetMapping
    public ResponseEntity<List<TestCaseLabelDTO>> listLabels(@PathVariable(name = "project_id") Long projectId){
        return  new ResponseEntity<>(testCaseLabelService.listByProjectIds(projectId), HttpStatus.OK);
    }
}
