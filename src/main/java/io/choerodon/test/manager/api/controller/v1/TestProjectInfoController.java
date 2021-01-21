package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.vo.agile.ProjectInfoVO;
import io.choerodon.swagger.annotation.Permission;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.TestProjectInfoService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/project_info")
public class TestProjectInfoController {

    @Autowired
    private TestProjectInfoService testProjectInfoService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("更新projectInfo")
    @PutMapping
    public ResponseEntity<ProjectInfoVO> updateProjectInfo(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId,
                                                           @ApiParam(value = "projectInfo对象", required = true)
                                                           @RequestBody ProjectInfoVO projectInfoVO) {
        return Optional.ofNullable(testProjectInfoService.updateProjectInfo(projectId, projectInfoVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.projectInfo.update"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询projectinfo")
    @GetMapping
    public ResponseEntity<ProjectInfoVO> queryProjectInfo(@ApiParam(value = "项目id", required = true)
                                                           @PathVariable(name = "project_id") Long projectId) {
        return new ResponseEntity<>(testProjectInfoService.queryProjectInfo(projectId),HttpStatus.OK);
    }

}
