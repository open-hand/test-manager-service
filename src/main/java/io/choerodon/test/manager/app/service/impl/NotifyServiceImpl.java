package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.app.service.NotifyService;
import io.choerodon.test.manager.infra.feign.NotifyFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zongw.lee@gmail.com on 01/11/2018
 */
@Component
public class NotifyServiceImpl implements NotifyService {

    @Autowired
    NotifyFeignClient notifyFeignClient;

    @Override
    public void postWebSocket(String code, String id, String message) {
        notifyFeignClient.postWebSocket(code,id,message);
    }
}
