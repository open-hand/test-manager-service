package com.hand.hap.devops.service.impl;

import com.hand.hap.devops.service.DemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {

    @Override
    public String hello() {
        return "Hello World";
    }
}
