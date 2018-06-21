package com.test.devops.infra.repository.impl;

import com.test.devops.domain.entity.TestCycleCaseAttachmentRelE;
import com.test.devops.domain.entity.TestCycleCaseDefectRelE;
import com.test.devops.domain.repository.TestCycleCaseDefectRelRepository;
import com.test.devops.infra.dataobject.TestCycleCaseAttachmentRelDO;
import com.test.devops.infra.dataobject.TestCycleCaseDefectRelDO;
import com.test.devops.infra.mapper.TestCycleCaseDefectRelMapper;
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
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class TestCycleCaseDefectRelRepositoryImpl implements TestCycleCaseDefectRelRepository {
	@Autowired
	TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

	@Override
	public TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
		TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);
		if (testCycleCaseDefectRelMapper.insert(convert) != 1) {
			throw new CommonException("error.testStepCase.insert");
		}
		return ConvertHelper.convert(convert, TestCycleCaseDefectRelE.class);
	}

	@Override
	public void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
		TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);
		testCycleCaseDefectRelMapper.delete(convert);
	}

	@Override
	public TestCycleCaseDefectRelE update(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
		TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);
		if (testCycleCaseDefectRelMapper.updateByPrimaryKeySelective(convert) != 1) {
			throw new CommonException("error.testStepCase.update");
		}
		return testCycleCaseDefectRelE;
	}

	@Override
	public Page<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE, PageRequest pageRequest) {
		TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);

		Page<TestCycleCaseAttachmentRelDO> serviceDOPage = PageHelper.doPageAndSort(pageRequest,
				() -> testCycleCaseDefectRelMapper.select(convert));

		return ConvertPageHelper.convertPage(serviceDOPage, TestCycleCaseDefectRelE.class);
	}

	@Override
	public List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
		TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);

		return ConvertHelper.convertList(testCycleCaseDefectRelMapper.select(convert), TestCycleCaseDefectRelE.class);
	}
}
