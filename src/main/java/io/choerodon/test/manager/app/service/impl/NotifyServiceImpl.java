package io.choerodon.test.manager.app.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.test.manager.app.service.NotifyService;
import io.choerodon.test.manager.infra.feign.NotifyFeignClient;
import org.springframework.stereotype.Service;

/**
 * Created by zongw.lee@gmail.com on 01/11/2018
 */
@Service
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    private NotifyFeignClient notifyFeignClient;

    @Override
    public void postWebSocket(String code, String id, String message) {
        notifyFeignClient.postWebSocket(code,id,message);
    }
}
