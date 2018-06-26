package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.repository.TestCycleCaseAttachmentRelRepository;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;
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
public class TestCycleCaseAttachmentRelRepositoryImpl implements TestCycleCaseAttachmentRelRepository {

	@Autowired
	TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

	@Override
	public TestCycleCaseAttachmentRelE insert(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
		TestCycleCaseAttachmentRelDO convert = ConvertHelper.convert(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO.class);
		if (testCycleCaseAttachmentRelMapper.insert(convert) != 1) {
			throw new CommonException("error.testStepCase.insert");
		}
		return ConvertHelper.convert(convert, TestCycleCaseAttachmentRelE.class);
	}

	@Override
	public void delete(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
		TestCycleCaseAttachmentRelDO convert = ConvertHelper.convert(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO.class);
		testCycleCaseAttachmentRelMapper.delete(convert);
	}

	@Override
	public TestCycleCaseAttachmentRelE update(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
		TestCycleCaseAttachmentRelDO convert = ConvertHelper.convert(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO.class);
		if (testCycleCaseAttachmentRelMapper.updateByPrimaryKeySelective(convert) != 1) {
			throw new CommonException("error.testStepCase.update");
		}
		return testCycleCaseAttachmentRelE;
	}

	@Override
	public Page<TestCycleCaseAttachmentRelE> query(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE, PageRequest pageRequest) {
		TestCycleCaseAttachmentRelDO convert = ConvertHelper.convert(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO.class);

		Page<TestCycleCaseAttachmentRelDO> serviceDOPage = PageHelper.doPageAndSort(pageRequest,
				() -> testCycleCaseAttachmentRelMapper.select(convert));
		return ConvertPageHelper.convertPage(serviceDOPage, TestCycleCaseAttachmentRelE.class);
	}

	@Override
	public List<TestCycleCaseAttachmentRelE> query(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
		TestCycleCaseAttachmentRelDO convert = ConvertHelper.convert(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO.class);

		return ConvertHelper.convertList(testCycleCaseAttachmentRelMapper.select(convert), TestCycleCaseAttachmentRelE.class);
	}
}
