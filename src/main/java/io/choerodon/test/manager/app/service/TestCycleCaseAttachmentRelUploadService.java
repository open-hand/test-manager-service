package io.choerodon.test.manager.app.service;

import org.springframework.web.multipart.MultipartFile;

import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;

/**
 * @author: 25499
 * @date: 2019/12/16 17:21
 * @description:
 */
public interface TestCycleCaseAttachmentRelUploadService {
    TestCycleCaseAttachmentRelDTO baseUpload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment);

}
