package io.choerodon.test.manager.infra.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.repository.TestAutomationResultRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationResultE;
import io.choerodon.test.manager.infra.common.utils.DBValidateUtil;
import io.choerodon.test.manager.infra.dataobject.TestAutomationResultDO;
import io.choerodon.test.manager.infra.mapper.TestAutomationResultMapper;

@Component
public class TestAutomationResultRepositoryImpl implements TestAutomationResultRepository {

    @Autowired
    private TestAutomationResultMapper testAutomationResultMapper;

    @Override
    public List<TestAutomationResultE> query(TestAutomationResultE testAutomationResultE) {
        TestAutomationResultDO testAutomationResultDO = ConvertHelper.convert(testAutomationResultE, TestAutomationResultDO.class);
        return ConvertHelper.convertList(testAutomationResultMapper.select(testAutomationResultDO), TestAutomationResultE.class);
    }

    @Override
    public TestAutomationResultE insert(TestAutomationResultE testAutomationResultE) {
        TestAutomationResultDO testAutomationResultDO = ConvertHelper.convert(testAutomationResultE, TestAutomationResultDO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(
                testAutomationResultMapper::insertOneResult, testAutomationResultDO, 1, "error.testAutomationResult.insert");
        return ConvertHelper.convert(testAutomationResultDO, TestAutomationResultE.class);
    }

    @Override
    public void delete(TestAutomationResultE testAutomationResultE) {
        Assert.notNull(testAutomationResultE, "error.testAutomationResult.delete.param1.not.null");

        TestAutomationResultDO testAutomationResultDO = ConvertHelper.convert(testAutomationResultE, TestAutomationResultDO.class);
        testAutomationResultMapper.delete(testAutomationResultDO);
    }

    @Override
    public TestAutomationResultE update(TestAutomationResultE testAutomationResultE) {
        TestAutomationResultDO testAutomationResultDO = ConvertHelper.convert(testAutomationResultE, TestAutomationResultDO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(
                testAutomationResultMapper::updateByPrimaryKeySelective, testAutomationResultDO, 1, "error.testAutomationResult.update");

        return ConvertHelper.convert(testAutomationResultMapper.selectByPrimaryKey(testAutomationResultE.getId()), TestAutomationResultE.class);
    }
}
