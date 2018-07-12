package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCaseStepService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCaseStepServiceImpl implements TestCaseStepService {
	@Autowired
	ITestCaseStepService iTestCaseStepService;


	@Transactional(rollbackFor = Exception.class)
	@Override
	public void removeStep(TestCaseStepDTO testCaseStepDTO) {
		iTestCaseStepService.removeStep(ConvertHelper.convert(testCaseStepDTO, TestCaseStepE.class));
	}


	@Override
	public List<TestCaseStepDTO> query(TestCaseStepDTO testCaseStepDTO) {
		return ConvertHelper.convertList(iTestCaseStepService.query(ConvertHelper.convert(testCaseStepDTO, TestCaseStepE.class)), TestCaseStepDTO.class);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public TestCaseStepDTO changeStep(TestCaseStepDTO testCaseStepDTO) {
		TestCaseStepE testCaseStepE = ConvertHelper.convert(testCaseStepDTO, TestCaseStepE.class);
		if (testCaseStepE.getStepId() == null) {
			testCaseStepE.createOneStep();
		} else {
			testCaseStepE.changeOneStep();
		}
		return ConvertHelper.convert(testCaseStepE, TestCaseStepDTO.class);
	}


	@Transactional(rollbackFor = Exception.class)
	@Override
	public List<TestCaseStepDTO> batchInsertStep(List<TestCaseStepDTO> testCaseStepDTO) {
		List<TestCaseStepDTO> result = new ArrayList<>();
		String[] rank = new String[1];
		testCaseStepDTO.forEach(v -> {
			v.setLastRank(rank[0]);
			TestCaseStepDTO temp = changeStep(v);
			rank[0] = temp.getRank();
			result.add(temp);
		});
		return result;
	}

	@Transactional
	@Override
	public TestCaseStepDTO clone(TestCaseStepDTO testCaseStepDTO) {
		TestCaseStepE testCaseStepE = ConvertHelper.convert(testCaseStepDTO, TestCaseStepE.class);
		testCaseStepE.setStepId(null);
		testCaseStepE.setLastRank(testCaseStepE.getLastedStepRank());
		return ((TestCaseStepService) AopContext.currentProxy()).changeStep(testCaseStepDTO);

	}

}
