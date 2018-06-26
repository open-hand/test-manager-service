package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

/**
 * Created by jialongZuo@hand-china.com on 6/21/18.
 */
@RestController
@RequestMapping(value = "/v1/project/test/case/attachment")
public class TestAttachmentController {

    @Autowired
    TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Permission(permissionPublic = true)
    @ApiOperation("增加测试")
    @PostMapping
    public ResponseEntity<TestCycleCaseAttachmentRelE> uploadFile(@RequestParam("bucket_name") String bucketName,
                                                                  @RequestParam("file_name") String fileName,
                                                                  @RequestPart("file") MultipartFile multipartFile,
                                                                  @RequestParam("attachmentLinkId") Long attachmentLinkId,
                                                                  @RequestParam("attachmentType") String attachmentType,
                                                                  @RequestParam("comment") String comment) {
        return Optional.ofNullable(testCycleCaseAttachmentRelService.upload(bucketName, fileName, multipartFile, attachmentLinkId, attachmentType, comment))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycleCase.query"));

    }

    @Permission(permissionPublic = true)
    @ApiOperation("删除附件")
    @DeleteMapping
    public void removeAttachment(String bucketName, Long attachId) {
        testCycleCaseAttachmentRelService.delete(bucketName, attachId);
    }
}
