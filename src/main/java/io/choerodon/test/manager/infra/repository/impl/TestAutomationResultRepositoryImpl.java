//package io.choerodon.test.manager.infra.repository.impl;
//
//import java.util.Date;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.test.manager.domain.repository.TestAutomationResultRepository;
//import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationResultE;
//import io.choerodon.test.manager.infra.util.DBValidateUtil;
//import io.choerodon.test.manager.infra.vo.TestAutomationResultDTO;
//import io.choerodon.test.manager.infra.mapper.TestAutomationResultMapper;
//
//@Component
//public class TestAutomationResultRepositoryImpl implements TestAutomationResultRepository {
//
//    @Autowired
//    private TestAutomationResultMapper testAutomationResultMapper;
//
//    @Override
//    public List<TestAutomationResultE> query(TestAutomationResultE testAutomationResultE) {
//        TestAutomationResultDTO testAutomationResultDTO = modeMapper.map(testAutomationResultE, TestAutomationResultDTO.class);
//        return ConvertHelper.convertList(testAutomationResultMapper.select(testAutomationResultDTO), TestAutomationResultE.class);
//    }
//
//    @Override
//    public TestAutomationResultE insert(TestAutomationResultE testAutomationResultE) {
//        Date now = new Date();
//        testAutomationResultE.setCreationDate(now);
//        testAutomationResultE.setLastUpdateDate(now);
//        TestAutomationResultDTO testAutomationResultDTO = modeMapper.map(testAutomationResultE, TestAutomationResultDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(
//                testAutomationResultMapper::insertOneResult, testAutomationResultDTO, 1, "error.testAutomationResult.insert");
//        return modeMapper.map(testAutomationResultDTO, TestAutomationResultE.class);
//    }
//
//    @Override
//    public void delete(TestAutomationResultE testAutomationResultE) {
//        Assert.notNull(testAutomationResultE, "error.testAutomationResult.delete.param1.not.null");
//
//        TestAutomationResultDTO testAutomationResultDTO = modeMapper.map(testAutomationResultE, TestAutomationResultDTO.class);
//        testAutomationResultMapper.delete(testAutomationResultDTO);
//    }
//
//    @Override
//    public TestAutomationResultE update(TestAutomationResultE testAutomationResultE) {
//        TestAutomationResultDTO testAutomationResultDTO = modeMapper.map(testAutomationResultE, TestAutomationResultDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(
//                testAutomationResultMapper::updateByPrimaryKeySelective, testAutomationResultDTO, 1, "error.testAutomationResult.update");
//
//        return modeMapper.map(testAutomationResultMapper.selectByPrimaryKey(testAutomationResultE.getId()), TestAutomationResultE.class);
//    }
//}
