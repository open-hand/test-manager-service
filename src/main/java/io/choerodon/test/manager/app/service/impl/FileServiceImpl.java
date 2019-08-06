package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.app.service.FileService;
import io.choerodon.test.manager.infra.feign.FileFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by zongw.lee@gmail.com on 30/10/2018
 */
@Component
public class FileServiceImpl implements FileService {

    @Autowired
    FileFeignClient fileFeignClient;

    @Override
    public ResponseEntity<String> uploadFile(String bucketName, String fileName, MultipartFile multipartFile) {
        return fileFeignClient.uploadFile(bucketName,fileName,multipartFile);
    }

    @Override
    public ResponseEntity deleteFile(String bucketName, String url) {
        return fileFeignClient.deleteFile(bucketName,url);
    }
}
