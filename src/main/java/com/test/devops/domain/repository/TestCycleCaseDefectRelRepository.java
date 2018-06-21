package com.test.devops.domain.repository;

import com.test.devops.domain.entity.TestCycleCaseAttachmentRelE;
import com.test.devops.domain.entity.TestCycleCaseDefectRelE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelRepository {
	TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

	void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

	TestCycleCaseDefectRelE update(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

	List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

	Page<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE, PageRequest pageRequest);

}
