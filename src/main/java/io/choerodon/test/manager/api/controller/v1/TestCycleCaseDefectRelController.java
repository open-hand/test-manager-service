package io.choerodon.test.manager.api.controller.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.test.manager.api.vo.agile.IssueCreateDTO;
import io.choerodon.test.manager.api.vo.agile.IssueDTO;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.vo.IssueInfosVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;

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

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("增加缺陷")
    @PostMapping
    public ResponseEntity<List<TestCycleCaseDefectRelVO>> insert(@ApiParam(value = "项目id", required = true)
                                                                 @PathVariable(name = "project_id") Long projectId,
                                                                 @ApiParam(value = "关联的缺陷", required = true)
                                                                 @RequestBody List<TestCycleCaseDefectRelVO> testCycleCaseDefectRelVO,
                                                                 @ApiParam(value = "组织id", required = true)
                                                                 @RequestParam Long organizationId) {
        List<TestCycleCaseDefectRelVO> dtos = new ArrayList<>();
        testCycleCaseDefectRelVO.forEach(v -> {
            v.setProjectId(projectId);
            dtos.add(testCycleCaseDefectRelService.insert(v, projectId, organizationId));
        });
        return Optional.ofNullable(dtos)
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.testDefect.insert"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除缺陷")
    @DeleteMapping("/delete/{defectId}")
    public ResponseEntity removeAttachment(@ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId,
                                           @ApiParam(value = "缺陷id", required = true)
                                           @PathVariable(name = "defectId")
                                           @Encrypt Long defectId,
                                           @ApiParam(value = "组织id", required = true)
                                           @RequestParam Long organizationId) {
        TestCycleCaseDefectRelVO testCycleCaseDefectRelVO = new TestCycleCaseDefectRelVO();
        testCycleCaseDefectRelVO.setId(defectId);
        testCycleCaseDefectRelService.delete(testCycleCaseDefectRelVO, projectId, organizationId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建一个缺陷并且关联到对应case或者step")
    @PostMapping("/createIssueAndDefect/{defectType}/{id}")
    public TestCycleCaseDefectRelVO createIssueAndLinkDefect(@ApiParam(value = "关联缺陷创建vo", required = true)
                                                             @RequestBody IssueCreateDTO issueCreateDTO,
                                                             @ApiParam(value = "项目id", required = true)
                                                             @PathVariable("project_id") Long projectId,
                                                             @ApiParam(value = "应用类型", required = true)
                                                             @RequestParam("applyType") String applyType,
                                                             @ApiParam(value = "缺陷类型", required = true)
                                                             @PathVariable("defectType") String defectType,
                                                             @ApiParam(value = "组织id", required = true)
                                                             @RequestParam Long organizationId,
                                                             @ApiParam(value = "用例或步骤id", required = true)
                                                             @PathVariable("id") @Encrypt Long id) {
        IssueDTO issueDTO = testCaseService.createTest(issueCreateDTO, projectId, applyType);
        TestCycleCaseDefectRelVO defectRelDTO = new TestCycleCaseDefectRelVO();
        defectRelDTO.setIssueId(issueDTO.getIssueId());
        defectRelDTO.setProjectId(projectId);
        defectRelDTO.setDefectType(defectType);
        defectRelDTO.setDefectLinkId(id);
        TestCycleCaseDefectRelVO defect = testCycleCaseDefectRelService.insert(defectRelDTO, projectId, organizationId);
        defect.setIssueInfosVO(new IssueInfosVO(issueDTO));
        return defect;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("修改缺陷的projectId")
    @PutMapping("/fix")
    public void fixDefectData(@ApiParam(value = "项目id", required = true)
                              @PathVariable(name = "project_id") Long projectId,
                              @ApiParam(value = "组织id", required = true)
                              @RequestParam Long organizationId) {
        TestCycleCaseDefectRelVO testCycleCaseDefectRelVO = new TestCycleCaseDefectRelVO();
        testCycleCaseDefectRelVO.setProjectId(projectId);
        testCycleCaseDefectRelService.updateIssuesProjectId(testCycleCaseDefectRelVO, organizationId);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除缺陷后解除对应关系")
    @DeleteMapping("/delete_relation/{defectId}")
    public ResponseEntity deleteCaseRel(@ApiParam(value = "项目id", required = true)
                                        @PathVariable(name = "project_id") Long projectId,
                                        @ApiParam(value = "缺陷id", required = true)
                                        @PathVariable(name = "defectId") Long defectId) {
        testCycleCaseDefectRelService.deleteCaseRel(projectId,defectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
