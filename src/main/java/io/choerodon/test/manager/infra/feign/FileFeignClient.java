package io.choerodon.test.manager.infra.feign;

import io.choerodon.test.manager.infra.config.FeignMultipartSupportConfig;
import io.choerodon.test.manager.infra.feign.callback.FileFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/3/21.
 * Email: fuqianghuang01@gmail.com
 */
@FeignClient(value = "${hzero.service.file.name:hzero-file}", fallback = FileFeignClientFallback.class, configuration = FeignMultipartSupportConfig.class)
public interface FileFeignClient {
    @PostMapping({"/choerodon/v1/{organizationId}/delete-by-url"})
    ResponseEntity<String> deleteFileByUrl(@PathVariable("organizationId") Long organizationId,
                                           @RequestParam("bucketName") String bucketName,
                                           @RequestBody List<String> urls);
}
