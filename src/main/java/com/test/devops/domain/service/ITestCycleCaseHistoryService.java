package com.test.devops.domain.service;

import com.test.devops.domain.entity.TestCycleCaseHistoryE;
import com.test.devops.domain.entity.TestCycleCaseStepE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface ITestCycleCaseHistoryService {
	TestCycleCaseHistoryE insert(TestCycleCaseHistoryE testCycleCaseHistoryE);

	void delete(List<TestCycleCaseHistoryE> testCycleCaseHistoryE);

	List<TestCycleCaseHistoryE> update(List<TestCycleCaseHistoryE> testCycleCaseHistoryE);

	Page<TestCycleCaseHistoryE> query(TestCycleCaseHistoryE testCycleCaseHistoryE, PageRequest pageRequest);
}
