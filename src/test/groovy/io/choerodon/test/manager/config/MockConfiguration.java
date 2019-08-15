package groovy.io.choerodon.test.manager.config;

import io.choerodon.agile.api.vo.PriorityVO;
import io.choerodon.test.manager.infra.feign.FileFeignClient;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;
import io.choerodon.test.manager.infra.feign.callback.FileFeignClientFallback;
import io.choerodon.test.manager.infra.feign.callback.IssueFeignClientFallback;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * @author shinan.chen
 * @since 2019/7/22
 */
@Configuration
public class MockConfiguration {

/*    @Bean
    @Primary
    IamFeignClient iamFeignClient() {
        IamFeignClient iamFeignClient = Mockito.mock(IamFeignClientFallback.class);
        UserDO user = new UserDO();
        user.setId(1L);
        user.setRealName("test");
        Mockito.when(iamFeignClient.listUsersByIds(ArgumentMatchers.anyObject(), ArgumentMatchers.anyBoolean())).thenReturn(new ResponseEntity<>(Arrays.asList(user), HttpStatus.OK));
        return iamFeignClient;
    }
    */
    @Bean
    @Primary
    IssueFeignClient issueFeignClient(){
        IssueFeignClient issueFeignClient = Mockito.mock(IssueFeignClientFallback.class);
        PriorityVO priorityVO = new PriorityVO();
        priorityVO.setColour("red");
        priorityVO.setId((long) 12);
        priorityVO.setName("testImportTemp");
        Mockito.when(issueFeignClient.queryByOrganizationIdList(ArgumentMatchers.anyLong())).thenReturn(new ResponseEntity<>(Arrays.asList(priorityVO),HttpStatus.OK));
        return issueFeignClient;
    }
    @Bean
    @Primary
    FileFeignClient fileFeignClient() {
        FileFeignClient fileFeignClient = Mockito.mock(FileFeignClientFallback.class);
        String imageUrl = "https://minio.choerodon.com.cn/agile-service/file_56a005f56a584047b538d5bf84b17d70_blob.png";
        Mockito.when(fileFeignClient.uploadFile(anyString(), anyString(), any(MultipartFile.class))).thenReturn(new ResponseEntity<>(imageUrl, HttpStatus.OK));
        Mockito.when(fileFeignClient.deleteFile(anyString(), anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        return fileFeignClient;
    }
}