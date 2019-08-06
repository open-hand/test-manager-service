package io.choerodon.test.manager.domain.repository;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseAttachmentRelRepository {

    TestCycleCaseAttachmentRelE insert(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE);

    void delete(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE);

    List<TestCycleCaseAttachmentRelE> query(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE);
}
