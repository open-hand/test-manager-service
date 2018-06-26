package io.choerodon.test.manager.domain.test.manager.factory;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
public class TestCycleCaseAttachmentRelEFactory {
    public static TestCycleCaseAttachmentRelE create() {
        return ApplicationContextHelper.getSpringFactory().getBean(TestCycleCaseAttachmentRelE.class);
    }
}
