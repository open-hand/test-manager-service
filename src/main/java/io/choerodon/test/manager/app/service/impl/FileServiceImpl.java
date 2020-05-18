//package io.choerodon.test.manager.app.service.impl;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import io.choerodon.test.manager.app.service.FileService;
//import io.choerodon.test.manager.infra.feign.FileFeignClient;
//
///**
// * Created by zongw.lee@gmail.com on 30/10/2018
// */
//@Service
//public class FileServiceImpl implements FileService {
//
//    @Autowired
//    private FileFeignClient fileFeignClient;
//
//    @Override
//    public ResponseEntity<String> uploadFile(String bucketName, String fileName, MultipartFile multipartFile) {
//        return fileFeignClient.uploadFile(bucketName,fileName,multipartFile);
//    }
//
//    @Override
//    public ResponseEntity deleteFile(String bucketName, String url) {
//        return fileFeignClient.deleteFile(bucketName,url);
//    }
//}
