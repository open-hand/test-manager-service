package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.agile.api.dto.IssueCreateDTO;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.base.annotation.Permission;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/defect")
public class TestCycleCaseDefectRelController {

    @Autowired
    TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    TestCaseService testCaseService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("增加缺陷")
    @PostMapping
    public ResponseEntity<List<TestCycleCaseDefectRelDTO>> insert(@PathVariable(name = "project_id") Long projectId,
                                                                  @RequestBody List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelDTO,
                                                                  @RequestParam Long organizationId ) {
        List<TestCycleCaseDefectRelDTO> dtos = new ArrayList<>();
        testCycleCaseDefectRelDTO.forEach(v -> {
            v.setProjectId(projectId);
            dtos.add(testCycleCaseDefectRelService.insert(v, projectId,organizationId));
        });
        return Optional.ofNullable(dtos)
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testDefect.insert"));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除缺陷")
    @DeleteMapping("/delete/{defectId}")
    public ResponseEntity removeAttachment(@PathVariable(name = "project_id") Long projectId,
                                           @PathVariable(name = "defectId") Long defectId,
                                           @RequestParam Long organizationId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setId(defectId);
        testCycleCaseDefectRelService.delete(testCycleCaseDefectRelDTO, projectId,organizationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("创建一个缺陷并且关联到对应case或者step")
    @PostMapping("/createIssueAndDefect/{defectType}/{id}")
    public TestCycleCaseDefectRelDTO createIssueAndLinkDefect(@RequestBody IssueCreateDTO issueCreateDTO,
                                                              @PathVariable("project_id") Long projectId,
                                                              @RequestParam("applyType") String applyType,
                                                              @PathVariable("defectType") String defectType,
                                                              @RequestParam Long organizationId,
                                                              @PathVariable("id") Long id){
        IssueDTO issueDTO=testCaseService.createTest(issueCreateDTO,projectId,applyType);
        TestCycleCaseDefectRelDTO defectRelDTO=new TestCycleCaseDefectRelDTO();
        defectRelDTO.setIssueId(issueDTO.getIssueId());
        defectRelDTO.setProjectId(projectId);
        defectRelDTO.setDefectType(defectType);
        defectRelDTO.setDefectLinkId(id);
        TestCycleCaseDefectRelDTO defect= testCycleCaseDefectRelService.insert(defectRelDTO,projectId,organizationId);
        defect.setIssueInfosDTO(new IssueInfosDTO(issueDTO));
        return defect;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("修改缺陷的projectId")
    @PutMapping("/fix")
    public void fixDefectData(@PathVariable(name = "project_id") Long projectId,
                              @RequestParam Long organizationId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setProjectId(projectId);
        testCycleCaseDefectRelService.updateIssuesProjectId(testCycleCaseDefectRelDTO,organizationId);
    }
}
