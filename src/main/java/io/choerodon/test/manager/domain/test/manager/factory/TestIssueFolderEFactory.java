package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
public class TestIssueFolderEFactory {
    public static TestIssueFolderE create() {
        return ApplicationContextHelper.getSpringFactory().getBean(TestIssueFolderE.class);
    }

}
