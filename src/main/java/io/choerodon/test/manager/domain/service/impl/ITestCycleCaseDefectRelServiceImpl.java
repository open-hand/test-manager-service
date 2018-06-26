package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleCaseDefectRelServiceImpl implements ITestCycleCaseDefectRelService {

	@Autowired
	TestCaseFeignClient testCaseFeignClient;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
		return testCycleCaseDefectRelE.addSelf();
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
		testCycleCaseDefectRelE.deleteSelf();
	}



	@Override
	public List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
		return testCycleCaseDefectRelE.querySelf();
	}

	@Override
	public List<TestCycleCaseDefectRelE> query(Long linkId, String defectType) {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		testCycleCaseDefectRelE.setDefectLinkId(linkId);
		testCycleCaseDefectRelE.setDefectType(defectType);
		List<TestCycleCaseDefectRelE> lists = testCycleCaseDefectRelE.querySelf();
		List<Long> issueLists = lists.stream().map(v -> v.getIssueId()).collect(Collectors.toList());
		//testCaseFeignClient
		//获取issueName
		//插入返回值
		return lists;
	}
}
