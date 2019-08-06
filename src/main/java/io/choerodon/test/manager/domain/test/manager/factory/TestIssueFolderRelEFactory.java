package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public class TestIssueFolderRelEFactory {
    public static TestIssueFolderRelE create() {
        return ApplicationContextHelper.getSpringFactory().getBean(TestIssueFolderRelE.class);
    }

}
