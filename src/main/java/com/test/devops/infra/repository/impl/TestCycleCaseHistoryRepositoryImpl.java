package com.test.devops.infra.repository.impl;

import com.test.devops.domain.entity.TestCycleCaseDefectRelE;
import com.test.devops.domain.entity.TestCycleCaseHistoryE;
import com.test.devops.domain.repository.TestCycleCaseHistoryRepository;
import com.test.devops.infra.dataobject.TestCycleCaseAttachmentRelDO;
import com.test.devops.infra.dataobject.TestCycleCaseDefectRelDO;
import com.test.devops.infra.dataobject.TestCycleCaseHistoryDO;
import com.test.devops.infra.mapper.TestCycleCaseHistoryMapper;
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
public class TestCycleCaseHistoryRepositoryImpl implements TestCycleCaseHistoryRepository {
	@Autowired
	TestCycleCaseHistoryMapper testCycleCaseHistoryMapper;

	@Override
	public TestCycleCaseHistoryE insert(TestCycleCaseHistoryE testCycleCaseHistoryE) {
		TestCycleCaseHistoryDO convert = ConvertHelper.convert(testCycleCaseHistoryE, TestCycleCaseHistoryDO.class);
		if (testCycleCaseHistoryMapper.insert(convert) != 1) {
			throw new CommonException("error.testStepCase.insert");
		}
		return ConvertHelper.convert(convert, TestCycleCaseHistoryE.class);
	}

	@Override
	public void delete(TestCycleCaseHistoryE testCycleCaseHistoryE) {
		TestCycleCaseHistoryDO convert = ConvertHelper.convert(testCycleCaseHistoryE, TestCycleCaseHistoryDO.class);
		testCycleCaseHistoryMapper.delete(convert);
	}

	@Override
	public TestCycleCaseHistoryE update(TestCycleCaseHistoryE testCycleCaseHistoryE) {
		TestCycleCaseHistoryDO convert = ConvertHelper.convert(testCycleCaseHistoryE, TestCycleCaseHistoryDO.class);
		if (testCycleCaseHistoryMapper.updateByPrimaryKeySelective(convert) != 1) {
			throw new CommonException("error.testStepCase.update");
		}
		return testCycleCaseHistoryE;
	}

	@Override
	public Page<TestCycleCaseHistoryE> query(TestCycleCaseHistoryE testCycleCaseHistoryE, PageRequest pageRequest) {
		TestCycleCaseHistoryDO convert = ConvertHelper.convert(testCycleCaseHistoryE, TestCycleCaseHistoryDO.class);

		Page<TestCycleCaseAttachmentRelDO> serviceDOPage = PageHelper.doPageAndSort(pageRequest,
				() -> testCycleCaseHistoryMapper.select(convert));

		return ConvertPageHelper.convertPage(serviceDOPage, TestCycleCaseHistoryE.class);
	}

	@Override
	public List<TestCycleCaseHistoryE> query(TestCycleCaseHistoryE testCycleCaseHistoryE) {
		TestCycleCaseHistoryDO convert = ConvertHelper.convert(testCycleCaseHistoryE, TestCycleCaseHistoryDO.class);

		return ConvertHelper.convertList(testCycleCaseHistoryMapper.select(convert), TestCycleCaseHistoryE.class);
	}
}
