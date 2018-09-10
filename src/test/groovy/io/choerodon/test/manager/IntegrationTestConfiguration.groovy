package io.choerodon.test.manager

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import io.choerodon.test.manager.app.service.TestCaseService
import io.choerodon.test.manager.app.service.UserService
import io.choerodon.test.manager.app.service.impl.TestCaseServiceImpl
import io.choerodon.test.manager.app.service.impl.TestIssueFolderServiceImpl
import io.choerodon.test.manager.app.service.impl.UserServiceImpl
import io.choerodon.test.manager.infra.feign.ProductionVersionClient
import io.choerodon.test.manager.infra.feign.ProjectFeignClient
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient
import io.choerodon.test.manager.infra.feign.UserFeignClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.MacSigner
import org.springframework.security.jwt.crypto.sign.Signer
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by hailuoliu@choerodon.io on 2018/7/13.
 */
@TestConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(LiquibaseConfig)
class IntegrationTestConfiguration {

    private final detachedMockFactory = new DetachedMockFactory()

    @Value('${choerodon.oauth.jwt.key:choerodon}')
    String key

    @Autowired
    TestRestTemplate testRestTemplate

    @Autowired
    LiquibaseExecutor liquibaseExecutor

    final ObjectMapper objectMapper = new ObjectMapper()

    @Bean
    KafkaTemplate kafkaTemplate() {
        detachedMockFactory.Mock(KafkaTemplate)
    }

//    TestCaseFeignClient testCaseFeignClient=detachedMockFactory.Mock(TestCaseFeignClient)
//    ProductionVersionClient productionVersionClient=detachedMockFactory.Mock(ProductionVersionClient)
//    ProjectFeignClient projectFeignClient=detachedMockFactory.Mock(ProjectFeignClient)
//    UserFeignClient userFeignClient=detachedMockFactory.Mock(UserFeignClient)
//
//    @Bean(name = "mockTestCaseFeignClient")
//    TestCaseFeignClient createMock1(){
//        return testCaseFeignClient
//    }
//    @Bean(name = "mockProductionVersionClient")
//    ProductionVersionClient createMock2(){
//        return productionVersionClient
//    }
//
//    @Bean(name = "mockProjectFeignClient")
//    ProjectFeignClient createMock3(){
//        return projectFeignClient
//    }
//    @Bean(name = "mockUserFeignClient")
//    UserFeignClient createMock4(){
//        return userFeignClient
//    }
    @Bean
    @Primary
    TestCaseService createMock5() {
        return detachedMockFactory.Mock(TestCaseService)
       //return new TestCaseServiceImpl(testCaseFeignClient,productionVersionClient,projectFeignClient)
    }
    @Bean
    @Primary
    UserService createMock6() {
        return detachedMockFactory.Mock(UserService)
    }

    @PostConstruct
    void init() {
        liquibaseExecutor.execute()
        setTestRestTemplateJWT()
    }

    private void setTestRestTemplateJWT() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory())
        testRestTemplate.getRestTemplate().setInterceptors([new ClientHttpRequestInterceptor() {
            @Override
            ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                httpRequest.getHeaders()
                        .add('JWT_Token', createJWT(key, objectMapper))
                return clientHttpRequestExecution.execute(httpRequest, bytes)
            }
        }])
    }

    static String createJWT(final String key, final ObjectMapper objectMapper) {
        Signer signer = new MacSigner(key)
        CustomUserDetails defaultUserDetails = new CustomUserDetails('default', 'unknown', Collections.emptyList())
        defaultUserDetails.setUserId(0L)
        defaultUserDetails.setOrganizationId(0L)
        defaultUserDetails.setLanguage('zh_CN')
        defaultUserDetails.setTimeZone('CCT')
        String jwtToken = null
        try {
            jwtToken = 'Bearer ' + JwtHelper.encode(objectMapper.writeValueAsString(defaultUserDetails), signer).getEncoded()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return jwtToken
    }


}
