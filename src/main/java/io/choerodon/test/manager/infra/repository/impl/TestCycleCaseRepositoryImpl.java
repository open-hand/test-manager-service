package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper;
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
public class TestCycleCaseRepositoryImpl implements TestCycleCaseRepository {
    @Autowired
    TestCycleCaseMapper testCycleCaseMapper;

    @Override
    public TestCycleCaseE insert(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseDO convert = ConvertHelper.convert(testCycleCaseE, TestCycleCaseDO.class);
        if (testCycleCaseMapper.insert(convert) != 1) {
            throw new CommonException("error.testStepCase.insert");
        }
        return ConvertHelper.convert(convert, TestCycleCaseE.class);
    }

    @Override
    public void delete(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseDO convert = ConvertHelper.convert(testCycleCaseE, TestCycleCaseDO.class);
        testCycleCaseMapper.delete(convert);
    }

    @Override
    public TestCycleCaseE update(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseDO convert = ConvertHelper.convert(testCycleCaseE, TestCycleCaseDO.class);
        if (testCycleCaseMapper.updateByPrimaryKey(convert) != 1) {
            throw new CommonException("error.testStepCase.update");
        }
		return ConvertHelper.convert(testCycleCaseMapper.selectByPrimaryKey(convert.getExecuteId()), TestCycleCaseE.class);
    }

    @Override
    public Page<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, PageRequest pageRequest) {
        TestCycleCaseDO convert = ConvertHelper.convert(testCycleCaseE, TestCycleCaseDO.class);

        Page<TestCycleCaseAttachmentRelDO> serviceDOPage = PageHelper.doPageAndSort(pageRequest,
                () -> testCycleCaseMapper.select(convert));

        return ConvertPageHelper.convertPage(serviceDOPage, TestCycleCaseE.class);
    }

    @Override
    public List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseDO convert = ConvertHelper.convert(testCycleCaseE, TestCycleCaseDO.class);

        return ConvertHelper.convertList(testCycleCaseMapper.select(convert), TestCycleCaseE.class);
    }

    @Override
    public TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseDO convert = ConvertHelper.convert(testCycleCaseE, TestCycleCaseDO.class);
        List<TestCycleCaseDO> list = testCycleCaseMapper.query(convert);
        if (list.size() != 1) {
			throw new CommonException("error.cycle.case.query.not.found");
        }
        return ConvertHelper.convert(list.get(0), TestCycleCaseE.class);
    }


}
