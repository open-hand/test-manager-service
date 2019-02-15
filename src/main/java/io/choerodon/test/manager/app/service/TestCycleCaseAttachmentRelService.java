package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCycleCaseAttachmentRelDTO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseAttachmentRelService {

    TestCycleCaseAttachmentRelDTO upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment);

    void delete(String bucketName, Long attachId);

    void delete(Long linkedId, String Type);

    /**
     * 上传多个附件
     *
     * @param request          request
     * @param bucketName       bucketName
     * @param attachmentLinkId attachmentLinkId
     * @param attachmentType   attachmentType
     * @return TestCycleCaseAttachmentRelDTO
     */
    List<TestCycleCaseAttachmentRelDTO> uploadMultipartFile(HttpServletRequest request, String bucketName, Long attachmentLinkId, String attachmentType);
}
