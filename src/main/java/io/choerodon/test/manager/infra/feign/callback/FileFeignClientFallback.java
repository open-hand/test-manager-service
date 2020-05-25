package io.choerodon.test.manager.infra.feign.callback;

import io.choerodon.test.manager.infra.feign.FileFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by HuangFuqiang on 2018/1/15.
 */
@Component
public class FileFeignClientFallback implements FileFeignClient {

    @Override
    public ResponseEntity<String> deleteFileByUrl(Long organizationId, String bucketName, List<String> urls) {
        throw new CommonException("error.delete.file");
    }
}
