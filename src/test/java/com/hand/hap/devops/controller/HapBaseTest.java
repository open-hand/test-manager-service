package com.hand.hap.devops.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * Created by ChaiYuchen on 2017/11/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class HapBaseTest {
    private final static String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJwYXNzd29yZCI6InVua25vd24gcGFzc3dvcmQiLCJ1c2VybmFtZSI6ImFkbWl" +
            "uIiwiYXV0aG9yaXRpZXMiOltdLCJhY2NvdW50Tm9uRXhwaXJlZCI6dHJ1ZSwiYWN" +
            "jb3VudE5vbkxvY2tlZCI6dHJ1ZSwiY3JlZGVudGlhbHNOb25FeHBpcmVkIjp0cnV" +
            "lLCJlbmFibGVkIjp0cnVlLCJ1c2VySWQiOjEsInRpbWVab25lIjoiQ1RUIiwibGF" +
            "uZ3VhZ2UiOiJ6aF9DTiIsIm9yZ2FuaXphdGlvbklkIjoxLCJwcm9qZWN0SWQiOjE" +
            "sImFkZGl0aW9uSW5mbyI6bnVsbH0.g2kWetvUZTytG-umdGm1JKiAvAb9RV6B7Dy" +
            "44ef9qRI";

    private static boolean first = true;

    @Autowired
    public TestRestTemplate restTemplate;
    public final static ObjectMapper MAPPER = new ObjectMapper();

    @Before
    public void before() {
        if (first) {
            restTemplate.getRestTemplate().getInterceptors().add(new OAuthAuthorizationInterceptor(TOKEN));
            first = false;
        }
    }

    class OAuthAuthorizationInterceptor implements ClientHttpRequestInterceptor {
        private final String token;

        private OAuthAuthorizationInterceptor(String token) {
            this.token = token;
        }

        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().add("Authorization", "Bearer " + token);
            return execution.execute(request, body);
        }
    }
}

