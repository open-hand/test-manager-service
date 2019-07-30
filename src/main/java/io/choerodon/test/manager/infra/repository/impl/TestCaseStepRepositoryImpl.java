//package io.choerodon.test.manager.infra.repository.impl;
//
//import io.choerodon.agile.infra.common.utils.RankUtil;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.test.manager.domain.repository.TestCaseStepRepository;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
//import io.choerodon.test.manager.infra.util.DBValidateUtil;
//import io.choerodon.test.manager.infra.util.LiquibaseHelper;
//import io.choerodon.test.manager.infra.vo.TestCaseStepDTO;
//import io.choerodon.test.manager.infra.mapper.TestCaseStepMapper;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//import java.util.Date;
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//@Component
//public class TestCaseStepRepositoryImpl implements TestCaseStepRepository {
//
//    private static final String ERROR_STEP_ID_NOT_NULL = "error.case.step.insert.stepId.should.be.null";
//
//    @Autowired
//    TestCaseStepMapper testCaseStepMapper;
//
//    @Value("${spring.datasource.url}")
//    private String dsUrl;
//
//    @Override
//    public TestCaseStepE insert(TestCaseStepE testCaseStepE) {
//        if (testCaseStepE == null || testCaseStepE.getStepId() != null) {
//            throw new CommonException(ERROR_STEP_ID_NOT_NULL);
//        }
//        TestCaseStepDTO testStepCase = modeMapper.map(testCaseStepE, TestCaseStepDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(testCaseStepMapper::insert, testStepCase, 1, "error.testStepCase.insert");
//        return modeMapper.map(testStepCase, TestCaseStepE.class);
//    }
//
//    @Override
//    public List<TestCaseStepE> batchInsert(List<TestCaseStepE> testCaseSteps) {
//        if (testCaseSteps == null || testCaseSteps.isEmpty()) {
//            throw new CommonException("error.case.step.list.empty");
//        }
//        Date now = new Date();
//        for (TestCaseStepE testCaseStep : testCaseSteps) {
//            if (testCaseStep == null || testCaseStep.getStepId() != null) {
//                throw new CommonException(ERROR_STEP_ID_NOT_NULL);
//            }
//            testCaseStep.setLastUpdateDate(now);
//            testCaseStep.setCreationDate(now);
//        }
//
//        List<TestCaseStepDTO> testCaseStepDTOS = ConvertHelper.convertList(testCaseSteps, TestCaseStepDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(
//                testCaseStepMapper::batchInsertTestCaseSteps, testCaseStepDTOS, testCaseStepDTOS.size(), "error.testStepCase.batchInsert");
//        return ConvertHelper.convertList(testCaseStepDTOS, TestCaseStepE.class);
//    }
//
//    @Override
//    public void delete(TestCaseStepE testCaseStepE) {
//        Assert.notNull(testCaseStepE, "error.case.delete.param1.not.null");
//
//        TestCaseStepDTO testCaseStep = modeMapper.map(testCaseStepE, TestCaseStepDTO.class);
//        testCaseStepMapper.delete(testCaseStep);
//    }
//
//    @Override
//    public TestCaseStepE update(TestCaseStepE testStepCaseE) {
//        Assert.notNull(testStepCaseE, "error.case.step.update.param1.not.null");
//
//        TestCaseStepDTO testCaseStepDTO = modeMapper.map(testStepCaseE, TestCaseStepDTO.class);
//
//        DBValidateUtil.executeAndvalidateUpdateNum(testCaseStepMapper::updateByPrimaryKey, testCaseStepDTO, 1, "error.testStepCase.update");
//        return modeMapper.map(testCaseStepMapper.query(testCaseStepDTO).get(0), TestCaseStepE.class);
//
//    }
//
//    @Override
//    public List<TestCaseStepE> query(TestCaseStepE testStepCaseE) {
//        TestCaseStepDTO testCaseStepDTO = modeMapper.map(testStepCaseE, TestCaseStepDTO.class);
//        return ConvertHelper.convertList(testCaseStepMapper
//                .query(testCaseStepDTO), TestCaseStepE.class);
//    }
//
//    @Override
//    public List<TestCaseStepE> queryByParameter(TestCaseStepE testStepCaseE) {
//        TestCaseStepDTO testCaseStepDTO = modeMapper.map(testStepCaseE, TestCaseStepDTO.class);
//        return ConvertHelper.convertList(testCaseStepMapper
//                .select(testCaseStepDTO), TestCaseStepE.class);
//    }
//
//    @Override
//    public String getLastedRank(Long issueId) {
//        Assert.notNull(issueId, "error.case.step.getLastedRank.issueid.not.null");
//        return LiquibaseHelper.executeFunctionByMysqlOrOracle(testCaseStepMapper::getLastedRank, testCaseStepMapper::getLastedRank_oracle, dsUrl, issueId);
//    }
//}
