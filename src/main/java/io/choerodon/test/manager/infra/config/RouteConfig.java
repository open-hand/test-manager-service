package io.choerodon.test.manager.infra.config;

import io.choerodon.core.swagger.ChoerodonRouteData;
import io.choerodon.swagger.annotation.ChoerodonExtraData;
import io.choerodon.swagger.swagger.extra.ExtraData;
import io.choerodon.swagger.swagger.extra.ExtraDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * @author superlee
 * @since 2020-05-18
 */
@ChoerodonExtraData
public class RouteConfig implements ExtraDataManager {

    @Autowired
    private Environment environment;

    @Override
    public ExtraData getData() {
        ChoerodonRouteData choerodonRouteData = new ChoerodonRouteData();
        choerodonRouteData.setName(environment.getProperty("hzero.service.current.name", "test-manager"));
        choerodonRouteData.setPath(environment.getProperty("hzero.service.current.path", "/test/**"));
        choerodonRouteData.setServiceId(environment.getProperty("hzero.service.current.service-name", "test-manager-service"));
        choerodonRouteData.setPackages("io.choerodon.test.manager");
        extraData.put(ExtraData.ZUUL_ROUTE_DATA, choerodonRouteData);
        return extraData;
    }
}
