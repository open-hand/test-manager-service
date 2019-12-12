package io.choerodon.test.manager.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.annotation.Permission;

/**
 * Created by 842767365@qq.com on 6/21/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/test/case/attachment")
public class TestAttachmentController {

    @Autowired
    TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("增加附件")
    @PostMapping
    public ResponseEntity<List<TestCycleCaseAttachmentRelVO>> uploadFile(@RequestParam("executeId") Long executeId,
                                                                         @RequestParam("description") String description,
                                                                         @RequestParam("attachmentType") String type,
                                                                         @PathVariable(name = "project_id") Long projectId,
                                                                         HttpServletRequest request) {
        return Optional.ofNullable(testCycleCaseAttachmentRelService.uploadMultipartFile(request, executeId, description, type))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.upload.file"));

    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除附件")
    @DeleteMapping("/{attachId}")
    public ResponseEntity removeAttachment(@PathVariable(name = "attachId") Long attachId,
                                           @PathVariable(name = "project_id") Long projectId) {
        testCycleCaseAttachmentRelService.deleteAttachmentRel(attachId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
