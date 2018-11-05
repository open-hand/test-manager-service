package io.choerodon.test.manager.app.service;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public interface NotifyService {
    void postWebSocket(String code,String id, String message);
}
