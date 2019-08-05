//package io.choerodon.test.manager.infra.repository.impl;
//
//import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
//import io.choerodon.test.manager.domain.repository.TestStatusRepository;
//import io.choerodon.test.manager.infra.util.DBValidateUtil;
//import io.choerodon.test.manager.infra.vo.TestStatusDTO;
//import io.choerodon.test.manager.infra.mapper.TestStatusMapper;
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/25/18.
// */
//@Component
//public class TestStatusRepositoryImpl implements TestStatusRepository {
//
//    @Autowired
//    TestStatusMapper testStatusMapper;
//
//    @Override
//    public List<TestStatusE> queryAllUnderProject(TestStatusE testStatusE) {
//        TestStatusDTO testStatusDTO = modeMapper.map(testStatusE, TestStatusDTO.class);
//        return ConvertHelper.convertList(testStatusMapper.queryAllUnderProject(testStatusDTO), TestStatusE.class);
//    }
//
//    @Override
//    public TestStatusE queryOne(Long statusId) {
//        Assert.notNull(statusId, "error.status.query.one.parameter.not.null");
//
//        return modeMapper.map(testStatusMapper.selectByPrimaryKey(statusId), TestStatusE.class);
//
//    }
//
//    @Override
//    public TestStatusE query(TestStatusE testStatusE) {
//        TestStatusDTO testStatusDTO = modeMapper.map(testStatusE, TestStatusDTO.class);
//        return modeMapper.map(testStatusMapper.selectOne(testStatusDTO), TestStatusE.class);
//    }
//
//    @Override
//    public TestStatusE insert(TestStatusE testStatusE) {
//        if (testStatusE == null || testStatusE.getStatusId() != null) {
//            throw new CommonException("error.status.insert.statusId.should.be.null");
//        }
//        TestStatusDTO testStatusDTO = modeMapper.map(testStatusE, TestStatusDTO.class);
//        if (testStatusMapper.insert(testStatusDTO) != 1) {
//            throw new CommonException("error.test.status.insert");
//        }
//        return modeMapper.map(testStatusDTO, TestStatusE.class);
//    }
//
//    @Override
//    public void delete(TestStatusE testStatusE) {
//        Assert.notNull(testStatusE, "error.status.delete.parameter.not.null");
//
//        TestStatusDTO testStatusDTO = modeMapper.map(testStatusE, TestStatusDTO.class);
//        testStatusMapper.delete(testStatusDTO);
//    }
//
//    @Override
//    public TestStatusE update(TestStatusE testStatusE) {
//        Assert.notNull(testStatusE, "error.status.update.parameter.not.null");
//
//        TestStatusDTO testStatusDTO = modeMapper.map(testStatusE, TestStatusDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(testStatusMapper::updateByPrimaryKey, testStatusDTO, 1, "error.test.status.update");
//        return modeMapper.map(testStatusMapper.selectByPrimaryKey(testStatusDTO.getStatusId()), TestStatusE.class);
//    }
//
//    @Override
//    public void validateDeleteCycleCaseAllow(Long statusId) {
//        Assert.notNull(statusId, "error.validate.delete.allow.parameter.statusId.not.null");
//
//        if (testStatusMapper.ifDeleteCycleCaseAllow(statusId) > 0) {
//            throw new CommonException("error.delete.status.have.used");
//
//        }
//    }
//
//    @Override
//    public void validateDeleteCaseStepAllow(Long statusId) {
//        Assert.notNull(statusId, "error.validate.delete.allow.parameter.statusId.not.null");
//        if (testStatusMapper.ifDeleteCaseStepAllow(statusId) > 0) {
//            throw new CommonException("error.delete.status.have.used");
//        }
//    }
//
//    @Override
//    public Long getDefaultStatus(String statusType) {
//        Assert.notNull(statusType, "error.getDefault.parameter.type.not.null");
//        return testStatusMapper.getDefaultStatus(statusType);
//    }
//}
