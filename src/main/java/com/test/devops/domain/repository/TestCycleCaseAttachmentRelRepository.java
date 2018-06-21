package com.test.devops.domain.repository;

import com.test.devops.domain.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseAttachmentRelRepository {

	TestCycleCaseAttachmentRelE insert(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE);

	void delete(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE);

	TestCycleCaseAttachmentRelE update(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE);

	Page<TestCycleCaseAttachmentRelE> query(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE, PageRequest pageRequest);

	List<TestCycleCaseAttachmentRelE> query(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE);
}
