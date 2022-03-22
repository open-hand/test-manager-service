package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.FilePathService;
import io.choerodon.test.manager.infra.enums.FileUploadBucket;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * @author superlee
 * @since 2022-03-15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FilePathServiceImpl implements FilePathService {

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Value("${services.bucket.merged}")
    private boolean bucketMerged;

    private static final String DIVIDING_LINE = "/";

    @Override
    public String generateRelativePath(String fullPath) {
        if (ObjectUtils.isEmpty(fullPath)) {
            throw new CommonException("error.file.full.url.empty");
        }
        if (!fullPath.startsWith(attachmentUrl)) {
            throw new CommonException("error.fullPath.not.match.attachmentUrl");
        }
        return fullPath.substring(attachmentUrl.length());
    }

    @Override
    public String generateFullPath(String relativePath) {
        if (ObjectUtils.isEmpty(relativePath)) {
            throw new CommonException("error.file.relativePath.empty");
        }
        StringBuilder builder = new StringBuilder();
        if (!relativePath.startsWith(DIVIDING_LINE)) {
            relativePath = DIVIDING_LINE + relativePath;
        }
        builder.append(attachmentUrl).append(relativePath);
        return builder.toString();
    }

    @Override
    public String bucketName() {
        if (bucketMerged) {
            return FileUploadBucket.TEST_BUCKET.bucket();
        } else {
            return TestAttachmentCode.ATTACHMENT_BUCKET;
        }
    }
}
