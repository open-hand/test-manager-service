package com.test.devops.domain.service.impl;

import com.test.devops.domain.entity.TestCycleCaseDefectRelE;
import com.test.devops.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class ITestCycleCaseDefectRelServiceImpl implements ITestCycleCaseDefectRelService {
	@Override
	public TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
		return testCycleCaseDefectRelE.addSelf();
	}

	@Override
	public void delete(List<TestCycleCaseDefectRelE> testCycleCaseDefectRelE) {
		testCycleCaseDefectRelE.forEach(v -> v.deleteSelf());
	}

	@Override
	public List<TestCycleCaseDefectRelE> update(List<TestCycleCaseDefectRelE> testCycleCaseDefectRelE) {
		List<TestCycleCaseDefectRelE> testCaseES = new ArrayList<>();
		testCycleCaseDefectRelE.forEach(v -> testCaseES.add(v.updateSelf()));
		return testCaseES;
	}

	@Override
	public Page<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE, PageRequest pageRequest) {
		return testCycleCaseDefectRelE.querySelf(pageRequest);
	}
}
