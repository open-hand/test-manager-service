package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.entity.TestCycleCaseDefectRelE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface ITestCycleCaseDefectRelService {
	TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE);

	void delete(List<TestCycleCaseDefectRelE> testCycleCaseDefectRelE);

	List<TestCycleCaseDefectRelE> update(List<TestCycleCaseDefectRelE> testCycleCaseDefectRelE);

	Page<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE, PageRequest pageRequest);
}
