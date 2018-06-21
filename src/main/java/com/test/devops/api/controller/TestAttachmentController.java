package com.test.devops.api.controller;

import com.test.devops.app.service.TestCycleCaseAttachmentRelService;
import com.test.devops.domain.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
	public TestCycleCaseAttachmentRelE uploadFile(@RequestParam("bucket_name") String bucketName,
												  @RequestParam("file_name") String fileName,
												  @RequestPart("file") MultipartFile multipartFile,
												  @RequestParam("attachmentLinkId") Long attachmentLinkId,
												  @RequestParam("attachmentType") String attachmentType,
												  @RequestParam("comment") String comment) {
		return testCycleCaseAttachmentRelService.upload(bucketName, fileName, multipartFile, attachmentLinkId, attachmentType, comment);
	}

	@Permission(permissionPublic = true)
	@ApiOperation("删除附件")
	@DeleteMapping
	public void removeAttachment(String bucketName, Long attachId) {
		testCycleCaseAttachmentRelService.delete(bucketName, attachId);
	}
}
