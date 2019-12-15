//package groovy.io.choerodon.test.manager.config;
//
//import io.choerodon.test.manager.infra.feign.FileFeignClient;
//import io.choerodon.test.manager.infra.feign.IssueFeignClient;
//import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
//import io.choerodon.test.manager.infra.feign.callback.FileFeignClientFallback;
//import io.choerodon.test.manager.infra.feign.callback.IssueFeignClientFallback;
//import io.choerodon.test.manager.infra.feign.callback.TestCaseFeignClientFallback;
//import org.hamcrest.Matchers;
//import org.mockito.ArgumentMatchers;
//import org.mockito.Mockito;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//
///**
// * @author shinan.chen
// * @since 2019/7/22
// */
//@Configuration
//public class MockConfiguration {
//
///*    @Bean
//    @Primary
//    IamFeignClient iamFeignClient() {
//        IamFeignClient iamFeignClient = Mockito.mock(IamFeignClientFallback.class);
//        UserDO user = new UserDO();
//        user.setId(1L);
//        user.setRealName("test");
//        Mockito.when(iamFeignClient.listUsersByIds(ArgumentMatchers.anyObject(), ArgumentMatchers.anyBoolean())).thenReturn(new ResponseEntity<>(Arrays.asList(user), HttpStatus.OK));
//        return iamFeignClient;
//    }
//    */
////    @Bean
////    @Primary
////    IssueFeignClient issueFeignClient(){
////        IssueFeignClient issueFeignClient = Mockito.mock(IssueFeignClientFallback.class);
////        PriorityVO priorityVO = new PriorityVO();
////        priorityVO.setColour("red");
////        priorityVO.setId((long) 12);
////        priorityVO.setName("testImportTemp");
////        Mockito.when(issueFeignClient.queryByOrganizationIdList(ArgumentMatchers.anyLong())).thenReturn(new ResponseEntity<>(Arrays.asList(priorityVO),HttpStatus.OK));
////        return issueFeignClient;
////    }
//    @Bean
//    @Primary
//    FileFeignClient fileFeignClient() {
//        FileFeignClient fileFeignClient = Mockito.mock(FileFeignClientFallback.class);
//        String imageUrl = "https://minio.choerodon.com.cn/agile-service/file_56a005f56a584047b538d5bf84b17d70_blob.png";
//        Mockito.when(fileFeignClient.uploadFile(anyString(), anyString(), any(MultipartFile.class))).thenReturn(new ResponseEntity<>(imageUrl, HttpStatus.OK));
//        Mockito.when(fileFeignClient.deleteFile(anyString(), anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
//        return fileFeignClient;
//    }
////    @Bean
////    @Primary
////    TestCaseFeignClient testCaseFeignClient() {
////        TestCaseFeignClient testCaseFeignClient = Mockito.mock(TestCaseFeignClientFallback.class);
////        List<IssueInfoDTO> issueInfoDTOList = new ArrayList<IssueInfoDTO>();
////        IssueInfoDTO issueInfoDTO = new IssueInfoDTO();
////        issueInfoDTO.setIssueId(1L);
////        issueInfoDTO.setIssueNum("name1");
////        issueInfoDTO.setSummary("summary");
////        issueInfoDTOList.add(issueInfoDTO);
////        Mockito.when(testCaseFeignClient.listByIssueIds(anyLong(), anyList())).thenReturn(new ResponseEntity<>(issueInfoDTOList, HttpStatus.OK));
////        return testCaseFeignClient;
////    }
//}