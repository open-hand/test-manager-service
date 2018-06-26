package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.repository.TestCaseStepRepository;
import io.choerodon.test.manager.infra.dataobject.TestCaseStepDO;
import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class TestCaseStepRepositoryImpl implements TestCaseStepRepository {

    @Autowired
    TestCaseStepMapper testCaseStepMapper;


    @Override
    public TestCaseStepE insert(TestCaseStepE testCaseStepE) {
        TestCaseStepDO testStepCase = ConvertHelper.convert(testCaseStepE, TestCaseStepDO.class);
        if (testCaseStepMapper.insert(testStepCase) != 1) {
            throw new CommonException("error.testStepCase.insert");
        }
        return ConvertHelper.convert(testStepCase, TestCaseStepE.class);
    }

    @Override
    public void delete(TestCaseStepE testCaseStepE) {
        TestCaseStepDO testCaseStep = ConvertHelper.convert(testCaseStepE, TestCaseStepDO.class);
        testCaseStepMapper.delete(testCaseStep);
    }

    @Override
    public TestCaseStepE update(TestCaseStepE testStepCaseE) {
        TestCaseStepDO testCaseStepDO = ConvertHelper.convert(testStepCaseE, TestCaseStepDO.class);
        if (testCaseStepMapper.updateByPrimaryKey(testCaseStepDO) != 1) {
            throw new CommonException("error.testStepCase.update");
        }
        return ConvertHelper.convert(
                testCaseStepDO, TestCaseStepE.class);
    }

    @Override
    public List<TestCaseStepE> query(TestCaseStepE testStepCaseE) {
        TestCaseStepDO testCaseStepDO = ConvertHelper.convert(testStepCaseE, TestCaseStepDO.class);
        return ConvertHelper.convertList(testCaseStepMapper
                .query(testCaseStepDO), TestCaseStepE.class);

    }

//    @Override
//    public TestCaseStepE queryOne(TestCaseStepE testCaseStepE) {
//        return ConvertHelper.convert(testCaseStepMapper.selectOne(ConvertHelper.convert(testCaseStepE, TestCaseStepDO.class)), TestCaseStepE.class);
//    }


}
