package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
public class TestStatusEFactory {
    public static TestStatusE create() {
        return ApplicationContextHelper.getSpringFactory().getBean(TestStatusE.class);
    }

    private TestStatusEFactory() {
    }
}
