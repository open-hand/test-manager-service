package io.choerodon.test.manager.infra.common.utils;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.custom.extra.ExtraData;
import io.choerodon.swagger.custom.extra.ExtraDataManager;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
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