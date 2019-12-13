package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.vo.DemoPayload;
import io.choerodon.test.manager.api.vo.event.OrganizationRegisterEventPayload;

/**
 * Created by WangZhe@choerodon.io on 2019-02-15.
 * Email: ettwz@hotmail.com
 */
public interface DemoService {

    OrganizationRegisterEventPayload demoInit(DemoPayload demoPayload);

}
