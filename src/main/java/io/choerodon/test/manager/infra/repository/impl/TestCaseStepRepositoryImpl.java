package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.domain.repository.TestCaseStepRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.infra.common.utils.DBValidateUtil;
import io.choerodon.test.manager.infra.common.utils.LiquibaseHelper;
import io.choerodon.test.manager.infra.dataobject.TestCaseStepDO;
import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCaseStepRepositoryImpl implements TestCaseStepRepository {

    private static final String ERROR_STEP_ID_NOT_NULL = "error.case.step.insert.stepId.should.be.null";

    @Autowired
    TestCaseStepMapper testCaseStepMapper;

    @Value("${spring.datasource.url}")
    private String dsUrl;

    @Override
    public TestCaseStepE insert(TestCaseStepE testCaseStepE) {
        if (testCaseStepE == null || testCaseStepE.getStepId() != null) {
            throw new CommonException(ERROR_STEP_ID_NOT_NULL);
        }
        TestCaseStepDO testStepCase = ConvertHelper.convert(testCaseStepE, TestCaseStepDO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseStepMapper::insert, testStepCase, 1, "error.testStepCase.insert");
        return ConvertHelper.convert(testStepCase, TestCaseStepE.class);
    }

    @Override
    public List<TestCaseStepE> batchInsert(List<TestCaseStepE> testCaseSteps) {
        if (testCaseSteps == null || testCaseSteps.isEmpty()) {
            throw new CommonException("error.case.step.list.empty");
        }
        for (TestCaseStepE testCaseStep : testCaseSteps) {
            if (testCaseStep == null || testCaseStep.getStepId() != null) {
                throw new CommonException(ERROR_STEP_ID_NOT_NULL);
            }
        }

        List<TestCaseStepDO> testCaseStepDOs = ConvertHelper.convertList(testCaseSteps, TestCaseStepDO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(
                testCaseStepMapper::batchInsertTestCaseSteps, testCaseStepDOs, testCaseStepDOs.size(), "error.testStepCase.batchInsert");
        return ConvertHelper.convertList(testCaseStepDOs, TestCaseStepE.class);
    }

    @Override
    public void delete(TestCaseStepE testCaseStepE) {
        Assert.notNull(testCaseStepE, "error.case.delete.param1.not.null");

        TestCaseStepDO testCaseStep = ConvertHelper.convert(testCaseStepE, TestCaseStepDO.class);
        testCaseStepMapper.delete(testCaseStep);
    }

    @Override
    public TestCaseStepE update(TestCaseStepE testStepCaseE) {
        Assert.notNull(testStepCaseE, "error.case.step.update.param1.not.null");

        TestCaseStepDO testCaseStepDO = ConvertHelper.convert(testStepCaseE, TestCaseStepDO.class);

        DBValidateUtil.executeAndvalidateUpdateNum(testCaseStepMapper::updateByPrimaryKey, testCaseStepDO, 1, "error.testStepCase.update");
        return ConvertHelper.convert(testCaseStepMapper.query(testCaseStepDO).get(0), TestCaseStepE.class);

    }

    @Override
    public List<TestCaseStepE> query(TestCaseStepE testStepCaseE) {
        TestCaseStepDO testCaseStepDO = ConvertHelper.convert(testStepCaseE, TestCaseStepDO.class);
        return ConvertHelper.convertList(testCaseStepMapper
                .query(testCaseStepDO), TestCaseStepE.class);
    }

    @Override
    public List<TestCaseStepE> queryByParameter(TestCaseStepE testStepCaseE) {
        TestCaseStepDO testCaseStepDO = ConvertHelper.convert(testStepCaseE, TestCaseStepDO.class);
        return ConvertHelper.convertList(testCaseStepMapper
                .select(testCaseStepDO), TestCaseStepE.class);
    }

    @Override
    public String getLastedRank(Long issueId) {
        Assert.notNull(issueId, "error.case.step.getLastedRank.issueid.not.null");
        return LiquibaseHelper.executeFunctionByMysqlOrOracle(testCaseStepMapper::getLastedRank, testCaseStepMapper::getLastedRank_oracle, dsUrl, issueId);
    }
}
