package io.choerodon.test.manager.api.controller.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/defect")
public class TestCycleCaseDefectRelController {

    @Autowired
    TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("增加缺陷")
    @PostMapping
    public ResponseEntity<List<TestCycleCaseDefectRelDTO>> insert(@PathVariable(name = "project_id") Long projectId,
                                                                  @RequestBody List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelDTO) {
        List<TestCycleCaseDefectRelDTO> dtos = new ArrayList<>();
        testCycleCaseDefectRelDTO.forEach(v -> {
            v.setProjectId(projectId);
            dtos.add(testCycleCaseDefectRelService.insert(v, projectId));
        });
        return Optional.ofNullable(dtos)
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testDefect.insert"));
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除缺陷")
    @DeleteMapping("/delete/{defectId}")
    public ResponseEntity removeAttachment(@PathVariable(name = "project_id") Long projectId,
                                           @PathVariable(name = "defectId") Long defectId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setId(defectId);
        testCycleCaseDefectRelService.delete(testCycleCaseDefectRelDTO, projectId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改缺陷的projectId")
    @PutMapping("/fix")
    public void fixDefectData(@PathVariable(name = "project_id") Long projectId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setProjectId(projectId);
        testCycleCaseDefectRelService.updateIssuesProjectId(testCycleCaseDefectRelDTO);
    }
}
