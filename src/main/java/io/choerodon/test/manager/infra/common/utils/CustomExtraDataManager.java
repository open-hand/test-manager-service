package io.choerodon.test.manager.infra.common.utils;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@ChoerodonExtraData
public class CustomExtraDataManager implements ExtraDataManager {
    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName("test");
        choerodonRouteData.setPath("/test/**");
        choerodonRouteData.setServiceId("test-manager-service");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}