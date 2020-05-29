//package io.choerodon.test.manager.infra.feign.callback;
//
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.test.manager.infra.feign.NotifyFeignClient;
//import org.springframework.stereotype.Component;
//
///**
// * Created by zongw.lee@gmail.com on 01/11/2018
// */
//@Component
//public class NotifyFeignClientFallback implements NotifyFeignClient {
//
//    private static final String CREATE_ERROR = "error.NotifyFeign.create";
//
//    @Override
//    public void postWebSocket(String code, String id, String message) {
//        throw new CommonException(CREATE_ERROR);
//    }
//}
