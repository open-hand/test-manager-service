//package io.choerodon.test.manager.infra.repository.impl;
//
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.test.manager.domain.repository.TestCycleCaseDefectRelRepository;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
//import io.choerodon.test.manager.infra.util.DBValidateUtil;
//import io.choerodon.test.manager.infra.vo.TestCycleCaseDefectRelDTO;
//import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//@Component
//public class TestCycleCaseDefectRelRepositoryImpl implements TestCycleCaseDefectRelRepository {
//    @Autowired
//    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;
//
//
//    Logger log = LoggerFactory.getLogger(this.getClass());
//
//    @Override
//    public TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
//        TestCycleCaseDefectRelDTO convert = modeMapper.map(testCycleCaseDefectRelE, TestCycleCaseDefectRelDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseDefectRelMapper::insert, convert, 1, "error.defect.insert");
//
//        return modeMapper.map(convert, TestCycleCaseDefectRelE.class);
//    }
//
//    @Override
//    public void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
//        TestCycleCaseDefectRelDTO convert = modeMapper.map(testCycleCaseDefectRelE, TestCycleCaseDefectRelDTO.class);
//        testCycleCaseDefectRelMapper.delete(convert);
//    }
//
//
//    @Override
//    public List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
//        TestCycleCaseDefectRelDTO convert = modeMapper.map(testCycleCaseDefectRelE, TestCycleCaseDefectRelDTO.class);
//
//        return ConvertHelper.convertList(testCycleCaseDefectRelMapper.select(convert), TestCycleCaseDefectRelE.class);
//    }
//
//    @Override
//    public List<TestCycleCaseDefectRelE> queryInIssues(Long[] issues, Long projectId) {
//        Assert.notEmpty(issues, "error.query.issues.not.empty");
//        return ConvertHelper.convertList(testCycleCaseDefectRelMapper.queryInIssues(issues, projectId), TestCycleCaseDefectRelE.class);
//    }
//
//    @Override
//    public Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
//        TestCycleCaseDefectRelDTO convert = modeMapper.map(testCycleCaseDefectRelE, TestCycleCaseDefectRelDTO.class);
//        int count = testCycleCaseDefectRelMapper.updateProjectIdByIssueId(convert);
//        if (log.isDebugEnabled()) {
//            log.debug("fix defect data issueID {} updates num {}", convert.getIssueId(), count);
//        }
//        return true;
//    }
//
//    @Override
//    public List<Long> queryIssueIdAndDefectId(Long projectId) {
//        return testCycleCaseDefectRelMapper.queryIssueIdAndDefectId(projectId);
//    }
//
//    @Override
//    public List<Long> queryAllIssueIds() {
//        return testCycleCaseDefectRelMapper.queryAllIssueIds();
//    }
//}
