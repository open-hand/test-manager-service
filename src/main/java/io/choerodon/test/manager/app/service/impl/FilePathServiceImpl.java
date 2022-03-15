package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.FilePathService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author superlee
 * @since 2022-03-15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FilePathServiceImpl implements FilePathService {

    @Value("${services.attachment.url}")
    private String attachmentUrl;
    @Value("${services.bucket.prefix}")
    private String bucketPrefix;

    private static final String HTTPS = "https://";

    @Override
    public String generateRelativePath(String fullPath) {
        URL url = null;
        try {
            url = new URL(fullPath);
        } catch (MalformedURLException e) {
            throw new CommonException("error.malformed.url", e);
        }
        return url.getFile();
    }

    @Override
    public String generateFullPath(String bucketName, String relativePath) {
        StringBuilder builder = new StringBuilder();
        builder
                .append(HTTPS)
                .append(bucketPrefix)
                .append("-")
                .append(bucketName)
                .append(".")
                .append(attachmentUrl);
        if (!relativePath.startsWith("/")) {
            builder.append("/");
        }
        builder.append(relativePath);
        return builder.toString();
    }
}
