package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.test.manager.infra.dataobject.TestCycleDO;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleRepositoryImpl implements TestCycleRepository {
	@Autowired
	TestCycleMapper cycleMapper;

	@Override
	public TestCycleE insert(TestCycleE testCycleE) {
		Assert.notNull(testCycleE,"error.cycle.insert.not.be.null");
		validateCycle(testCycleE);
		TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
		if (cycleMapper.insert(convert) != 1) {
			throw new CommonException("error.testStepCase.insert");
		}
		return ConvertHelper.convert(convert, TestCycleE.class);
	}

	@Override
	public void delete(TestCycleE testCycleE) {
		TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
		cycleMapper.delete(convert);
	}

	@Override
	public TestCycleE update(TestCycleE testCycleE) {
		Assert.notNull(testCycleE,"error.cycle.update.not.be.null");
		validateCycle(testCycleE);
		TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
		if (cycleMapper.updateByPrimaryKey(convert) != 1) {
			throw new CommonException("error.testCycle.update");
		}
		return ConvertHelper.convert(cycleMapper.selectByPrimaryKey(convert.getCycleId()), TestCycleE.class);
	}


	@Override
	public Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest) {
		TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);

		Page<TestCycleDO> serviceDOPage = PageHelper.doPageAndSort(pageRequest,
				() -> cycleMapper.select(convert));

		return ConvertPageHelper.convertPage(serviceDOPage, TestCycleE.class);
	}

	@Override
	public List<TestCycleE> query(TestCycleE testCycleE) {
		TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
		return ConvertHelper.convertList(cycleMapper.select(convert), TestCycleE.class);

	}

	@Override
	public TestCycleE queryOne(TestCycleE testCycleE) {
		TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
		return ConvertHelper.convert(cycleMapper.selectOne(convert), TestCycleE.class);

	}

	@Override
	public List<TestCycleE> queryBar(Long[] versionId,Long assignedTo) {
		Assert.notNull(versionId, "error.query.cycle.versionIds.not.null");
		versionId = Stream.of(versionId).filter(Objects::nonNull).toArray(Long[]::new);
		if (versionId.length > 0) {
			return ConvertHelper.convertList(cycleMapper.query(versionId,assignedTo), TestCycleE.class);
		}
		return new ArrayList<>();
	}

	/**
	 * @deprecated not be used
	 */
	@Override
	@Deprecated
	public List<TestCycleE> filter(Map parameters) {
		return ConvertHelper.convertList(cycleMapper.filter(parameters), TestCycleE.class);

	}

	/**
	 * @deprecated (not be used)
	 */
	@Override
	@Deprecated
	public List<TestCycleE> getCyclesByVersionId(Long versionId) {
		return ConvertHelper.convertList(cycleMapper.getCyclesByVersionId(versionId), TestCycleE.class);
	}

	@Override
	public List<Long> selectCyclesInVersions(Long[] versionIds) {
		Assert.notNull(versionIds, "error.query.cycle.In.Versions.not.null");
		versionIds = Stream.of(versionIds).filter(Objects::nonNull).toArray(Long[]::new);

		if (versionIds.length > 0) {
			return cycleMapper.selectCyclesInVersions(versionIds);
		}
		return new ArrayList<>();
	}


	/**
	 * 验证version下是否有重名cycle
	 *
	 * @param testCycleE
	 */
	@Override
	public void validateCycle(TestCycleE testCycleE) {
		Assert.notNull(testCycleE.getVersionId(), "error.cycle.versionId.not.be.null");
		Assert.notNull(testCycleE.getCycleName(), "error.cycle.name.not.be.null");
		TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
		if (!cycleMapper.validateCycle(convert).equals(0L)) {
			throw new CommonException("error.cycle.in.version.has.existed");
		}
	}

	@Override
	public List<TestCycleE> queryAll() {
		return ConvertHelper.convertList(cycleMapper.selectAll(), TestCycleE.class);
	}
}
