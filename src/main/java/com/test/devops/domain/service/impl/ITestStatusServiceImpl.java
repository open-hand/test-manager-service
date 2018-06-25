package com.test.devops.domain.service.impl;

import com.test.devops.domain.entity.TestStatusE;
import com.test.devops.domain.service.ITestStatusService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
@Component
public class ITestStatusServiceImpl implements ITestStatusService {
	@Override
	public List<TestStatusE> query(TestStatusE testStatusE) {
		return testStatusE.querySelf();
	}

	@Override
	public TestStatusE insert(TestStatusE testStatusE) {
		return testStatusE.addSelf();
	}

	@Override
	public void delete(TestStatusE testStatusE) {
		testStatusE.deleteSelf();
	}

	@Override
	public TestStatusE update(TestStatusE testStatusE) {
		return testStatusE.updateSelf();
	}
}
