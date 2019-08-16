package io.choerodon.test.manager.infra.feign;

import io.choerodon.test.manager.infra.config.FeignMultipartSupportConfig;
import io.choerodon.test.manager.infra.feign.callback.FileFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/3/21.
 * Email: fuqianghuang01@gmail.com
 */
@FeignClient(value = "file-service", fallback = FileFeignClientFallback.class, configuration = FeignMultipartSupportConfig.class)
public interface FileFeignClient {

    @PostMapping(value = "/v1/files",
            produces = {MediaType.APPLICATION_JSON_UTF8_VALUE},
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> uploadFile(@RequestParam("bucket_name") String bucketName,
                                      @RequestParam("file_name") String fileName,
                                      @RequestPart("file") MultipartFile multipartFile);

    @DeleteMapping(value = "/v1/files")
    ResponseEntity deleteFile(@RequestParam("bucket_name") String bucketName,
                              @RequestParam("url") String url);
}
