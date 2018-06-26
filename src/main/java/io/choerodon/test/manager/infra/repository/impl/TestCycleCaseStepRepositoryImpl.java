package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseStepRepositoryImpl implements TestCycleCaseStepRepository {
	@Autowired
	TestCycleCaseStepMapper testCycleCaseStepMapper;

	@Override
	public TestCycleCaseStepE insert(TestCycleCaseStepE testCycleCaseStepE) {
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
		if (testCycleCaseStepMapper.insert(convert) != 1) {
			throw new CommonException("error.testStepCase.insert");
		}
		return testCycleCaseStepE;
	}

	@Override
	public void delete(TestCycleCaseStepE testCycleCaseStepE) {
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
		testCycleCaseStepMapper.delete(convert);
	}

	@Override
	public TestCycleCaseStepE update(TestCycleCaseStepE testCycleCaseStepE) {
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
		if (testCycleCaseStepMapper.updateByPrimaryKeySelective(convert) != 1) {
			throw new CommonException("error.testStepCase.update");
		}
		return testCycleCaseStepE;
	}

	@Override
	public Page<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE, PageRequest pageRequest) {
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);

		Page<TestCycleCaseAttachmentRelDO> serviceDOPage = PageHelper.doPageAndSort(pageRequest,
				() -> testCycleCaseStepMapper.select(convert));

		return ConvertPageHelper.convertPage(serviceDOPage, TestCycleCaseStepE.class);
	}

	@Override
	public List<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE) {
		TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);

		return ConvertHelper.convertList(testCycleCaseStepMapper.queryWithTestCaseStep(convert), TestCycleCaseStepE.class);
	}
}
