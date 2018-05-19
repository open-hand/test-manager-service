package com.test.devops.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DemoControllerTest extends HapBaseTest{
    @Autowired
    public TestRestTemplate restTemplate;

    @Test
    public void test() {
        String result = restTemplate.getForObject("/v1/demo", String.class);
        Assert.assertEquals("Hello World", result);
    }
}