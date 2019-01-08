package io.choerodon.test.manager

import com.fasterxml.jackson.databind.ObjectMapper
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.liquibase.LiquibaseConfig
import io.choerodon.liquibase.LiquibaseExecutor
import io.choerodon.test.manager.app.service.*
import io.choerodon.test.manager.domain.aop.TestCaseCountRecordAOP
import io.choerodon.test.manager.domain.aop.TestCycleCaseHistoryRecordAOP
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE
import io.choerodon.test.manager.infra.common.utils.RedisTemplateUtil
import org.redisson.api.RedissonClient
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.support.atomic.RedisAtomicLong
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.security.jwt.JwtHelper
import org.springframework.security.jwt.crypto.sign.MacSigner
import org.springframework.security.jwt.crypto.sign.Signer
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct
import java.time.LocalDateTime

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
    @Primary
    RedissonClient redissonClient(){
        detachedMockFactory.Mock(RedissonClient)
    }

    @Bean
    @Primary
    TestCaseService createMock5() {
        return detachedMockFactory.Mock(TestCaseService)
    }
    @Bean
    @Primary
    UserService createMock6() {
        return detachedMockFactory.Mock(UserService)
    }
    @Bean
    @Primary
    FileService createMock7() {
        return detachedMockFactory.Mock(FileService)
    }
    @Bean
    @Primary
    NotifyService createMock8() {
        return detachedMockFactory.Mock(NotifyService)
    }
    @Bean
    @Primary
    ScheduleService createMock9() {
        return detachedMockFactory.Mock(ScheduleService)
    }

    @Bean
    @Primary
    DevopsService createMock10() {
        return detachedMockFactory.Mock(DevopsService)
    }

    @Bean
    @Primary
    RedisTemplateUtil createMock11() {
        return detachedMockFactory.Mock(RedisTemplateUtil);
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
