package io.choerodon.test.manager.domain.repository;

import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
public interface TestStatusRepository {
	TestStatusE insert(TestStatusE testStatusE);

    void delete(TestStatusE testStatusE);

    TestStatusE update(TestStatusE testStatusE);

	List<TestStatusE> queryAllUnderProject(TestStatusE testStatusE);

	TestStatusE queryOne(Long statusId);

	void validateDeleteCycleCaseAllow(Long statusId);

	void validateDeleteCaseStepAllow(Long statusId);

	Long getDefaultStatus(String statusType);
}
