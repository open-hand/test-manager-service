package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.dto.TestCycleCaseAttachmentRelDTO;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/21/18.
 */
@RestController
@RequestMapping(value = "/v1/project/test/case/attachment")
public class TestAttachmentController {

    @Autowired
    TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("增加附件")
    @PostMapping
	public ResponseEntity<List<TestCycleCaseAttachmentRelDTO>> uploadFile(@RequestParam("bucket_name") String bucketName,
																		  @RequestParam("attachmentLinkId") Long attachmentLinkId,
																		  @RequestParam("attachmentType") String attachmentType,
																		  HttpServletRequest request) {
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");

		List<TestCycleCaseAttachmentRelDTO> attachmentRelDTOS = new ArrayList<>();
		files.forEach(v -> attachmentRelDTOS.add(testCycleCaseAttachmentRelService.upload(bucketName, v.getOriginalFilename(), v, attachmentLinkId, attachmentType, null)));
		return Optional.ofNullable(attachmentRelDTOS)
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.upload.file"));

    }

	@Permission(level = ResourceLevel.PROJECT)
    @ApiOperation("删除附件")
    @DeleteMapping
    public void removeAttachment(String bucketName, Long attachId) {
        testCycleCaseAttachmentRelService.delete(bucketName, attachId);
    }
}
