package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/21/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/test/case/attachment")
public class TestAttachmentController {

    @Autowired
    TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("增加附件")
    @PostMapping
    public ResponseEntity<List<TestCycleCaseAttachmentRelDTO>> uploadFile(@RequestParam("bucket_name") String bucketName,
                                                                          @RequestParam("attachmentLinkId") Long attachmentLinkId,
                                                                          @RequestParam("attachmentType") String attachmentType,
                                                                          @PathVariable(name = "project_id") Long projectId,
                                                                          HttpServletRequest request) {
        return Optional.ofNullable(testCycleCaseAttachmentRelService.uploadMultipartFile(request, bucketName, attachmentLinkId, attachmentType))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.upload.file"));

    }

    @Permission(level = ResourceLevel.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("删除附件")
    @DeleteMapping("/delete/bucket/{bucketName}/attach/{attachId}")
    public ResponseEntity removeAttachment(@PathVariable(name = "bucketName") String bucketName, @PathVariable(name = "attachId") Long attachId,
                                           @PathVariable(name = "project_id") Long projectId) {
        testCycleCaseAttachmentRelService.delete(bucketName, attachId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
