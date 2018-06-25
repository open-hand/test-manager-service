package com.test.devops.infra.repository.impl;

import com.test.devops.domain.entity.TestStatusE;
import com.test.devops.domain.repository.TestStatusRepository;
import com.test.devops.infra.dataobject.TestStatusDO;
import com.test.devops.infra.mapper.TestStatusMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
@Component
public class TestStatusRepositoryImpl implements TestStatusRepository {
	@Autowired
	TestStatusMapper testStatusMapper;

	@Override
	public List<TestStatusE> query(TestStatusE testStatusE) {
		TestStatusDO testStatusDO = ConvertHelper.convert(testStatusE, TestStatusDO.class);
		return ConvertHelper.convertList(testStatusMapper.select(testStatusDO), TestStatusE.class);
	}

	@Override
	public TestStatusE insert(TestStatusE testStatusE) {
		TestStatusDO testStatusDO = ConvertHelper.convert(testStatusE, TestStatusDO.class);
		if (testStatusMapper.insert(testStatusDO) != 1) {
			throw new CommonException("error.test.status.insert");
		}
		return ConvertHelper.convert(testStatusDO, TestStatusE.class);
	}

	@Override
	public void delete(TestStatusE testStatusE) {
		TestStatusDO testStatusDO = ConvertHelper.convert(testStatusE, TestStatusDO.class);
		if (testStatusMapper.delete(testStatusDO) != 1) {
			throw new CommonException("error.test.status.delete");
		}
	}

	@Override
	public TestStatusE update(TestStatusE testStatusE) {
		TestStatusDO testStatusDO = ConvertHelper.convert(testStatusE, TestStatusDO.class);
		if (testStatusMapper.updateByPrimaryKey(testStatusDO) != 1) {
			throw new CommonException("error.test.status.update");
		}
		return ConvertHelper.convert(testStatusDO, TestStatusE.class);
	}
}
