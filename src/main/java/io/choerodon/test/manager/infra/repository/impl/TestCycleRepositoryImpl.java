package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.common.utils.DBValidateUtil;
import io.choerodon.test.manager.infra.dataobject.TestCycleDO;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
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
        Assert.notNull(testCycleE, "error.cycle.insert.not.be.null");
        validateCycle(testCycleE);
        TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(cycleMapper::insert, convert, 1, "error.testStepCase.insert");
        return ConvertHelper.convert(convert, TestCycleE.class);
    }

    @Override
    public void delete(TestCycleE testCycleE) {
        TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
        cycleMapper.delete(convert);
    }

    @Override
    public TestCycleE update(TestCycleE testCycleE) {
        Assert.notNull(testCycleE, "error.cycle.update.not.be.null");
        validateCycle(testCycleE);
        TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
        if (cycleMapper.updateByPrimaryKeySelective(convert) != 1) {
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
    public List<TestCycleE> queryBar(Long projectId, Long[] versionId, Long assignedTo) {
        Assert.notNull(versionId, "error.query.cycle.versionIds.not.null");
        versionId = Stream.of(versionId).filter(Objects::nonNull).toArray(Long[]::new);
        if (versionId.length > 0) {
            return ConvertHelper.convertList(cycleMapper.query(projectId, versionId, assignedTo), TestCycleE.class);
        }
        return new ArrayList<>();
    }

    @Override
    public List<TestCycleE> queryBarOneCycle(Long cycleId) {
        Assert.notNull(cycleId, "error.query.cycle.Id.not.null");
        return ConvertHelper.convertList(cycleMapper.queryOneCycleBar(cycleId), TestCycleE.class);
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

    @Override
    public List<TestCycleE> queryChildCycle(TestCycleE testCycleE) {
        TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
        return ConvertHelper.convertList(cycleMapper.queryChildCycle(convert), TestCycleE.class);
    }

    @Override
    public List<TestCycleE> queryCycleInVersion(TestCycleE testCycleE) {
        TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
        return ConvertHelper.convertList(cycleMapper.queryCycleInVersion(convert), TestCycleE.class);
    }

    @Override
    public List<String> queryUpdateRank(TestCycleE testCycleE) {
        long lastCycleId = Long.parseLong(testCycleE.getRank());
        List<TestCycleDO> testCycleDOS;
        List<String> res = new ArrayList<>();

        if (testCycleE.getType().equals(TestCycleE.CYCLE)) {
            TestCycleDO convert = ConvertHelper.convert(testCycleE, TestCycleDO.class);
            testCycleDOS = cycleMapper.queryCycleInVersion(convert);
        } else {
            TestCycleE testCycleETemp = TestCycleEFactory.create();
            testCycleETemp.setCycleId(testCycleE.getParentCycleId());
            TestCycleDO convert = ConvertHelper.convert(testCycleETemp, TestCycleDO.class);
            testCycleDOS = cycleMapper.queryChildCycle(convert);
        }

        if (lastCycleId != -1L) {
            int lastIndex = -1;

            for (int a = 0; a < testCycleDOS.size(); a++) {
                if (testCycleDOS.get(a).getCycleId().equals(lastCycleId)) {
                    lastIndex = a;
                }
            }

            if (lastIndex >= 0) {
                TestCycleE testCycleETemp = TestCycleEFactory.create();
                testCycleETemp.setCycleId(testCycleDOS.get(lastIndex).getCycleId());
                List<TestCycleE> list = testCycleETemp.querySelf();
                res.add(list.get(0).getRank());
            } else {
                res.add(null);
            }

            if (lastIndex < testCycleDOS.size() - 1) {
                TestCycleE testCycleETemp = TestCycleEFactory.create();
                testCycleETemp.setCycleId(testCycleDOS.get(lastIndex + 1).getCycleId());
                List<TestCycleE> list = testCycleETemp.querySelf();
                res.add(list.get(0).getRank());
            } else {
                res.add(null);
            }
        } else {
            res.add(null);
            TestCycleE testCycleETemp = TestCycleEFactory.create();
            testCycleETemp.setCycleId(testCycleDOS.get(0).getCycleId());
            List<TestCycleE> list = testCycleETemp.querySelf();
            res.add(list.get(0).getRank());
        }
        return res;
    }

    @Override
    public String getLastedRank(TestCycleE testCycleE) {
        if (testCycleE.getType().equals(TestCycleE.CYCLE)) {
            return cycleMapper.getCycleLastedRank(testCycleE.getVersionId());
        } else {
            return cycleMapper.getFolderLastedRank(testCycleE.getParentCycleId());
        }
    }

    @Override
    public Long getCount(TestCycleE testCycleE) {
        if (testCycleE.getType().equals(TestCycleE.CYCLE)) {
            return cycleMapper.getCycleCountInVersion(testCycleE.getVersionId());
        } else {
            return cycleMapper.getFolderCountInCycle(testCycleE.getParentCycleId());
        }
    }

    @Override
    public List<TestCycleE> queryChildFolderByRank(TestCycleE testCycleE) {
        return ConvertHelper.convertList(cycleMapper.queryChildFolderByRank(testCycleE.getParentCycleId()), TestCycleE.class);
    }
}
