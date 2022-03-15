package io.choerodon.test.manager.app.service;

/**
 * @author superlee
 * @since 2022-03-15
 */
public interface FilePathService {

    String generateRelativePath(String fullPath);

    String generateFullPath(String bucketName,
                            String relativePath);
}
