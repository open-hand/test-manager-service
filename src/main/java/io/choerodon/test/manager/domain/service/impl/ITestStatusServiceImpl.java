package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.repository.TestStatusRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@Component
public class ITestStatusServiceImpl implements ITestStatusService {

	@Autowired
	TestStatusRepository statusRepository;

	@Override
	public List<TestStatusE> query(TestStatusE testStatusE) {
		return testStatusE.queryAllUnderProject();
	}

	@Override
	public TestStatusE insert(TestStatusE testStatusE) {
		return testStatusE.addSelf();
	}

	@Override
	public void delete(TestStatusE testStatusE) {
		TestStatusE statusE = testStatusE.queryOne();
		if (StringUtils.equals(statusE.getStatusType(), TestStatusE.STATUS_TYPE_CASE)) {
			statusRepository.validateDeleteCycleCaseAllow(statusE.getStatusId());
		} else {
			statusRepository.validateDeleteCaseStepAllow(statusE.getStatusId());
		}
		testStatusE.deleteSelf();
	}

	@Override
	public TestStatusE update(TestStatusE testStatusE) {
		return testStatusE.updateSelf();
	}

	@Override
	public Long getDefaultStatusId(String type) {
		return statusRepository.getDefaultStatus(type);
	}
}
