package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseAttachmentRelService {

    TestCycleCaseAttachmentRelE upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment);

    void delete(String bucketName, Long attachId);

}
