//package io.choerodon.test.manager.infra.repository.impl;
//
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.test.manager.domain.repository.TestIssueFolderRepository;
//import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
//import io.choerodon.test.manager.infra.vo.TestIssueFolderDTO;
//import io.choerodon.test.manager.infra.exception.IssueFolderException;
//import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * Created by zongw.lee@gmail.com on 08/30/2018
// */
//@Component
//public class TestIssueFolderRepositoryImpl implements TestIssueFolderRepository {
//
//    @Autowired
//    TestIssueFolderMapper testIssueFolderMapper;
//
//    @Override
//    public List<TestIssueFolderE> queryAllUnderProject(TestIssueFolderE testIssueFolderE) {
//        TestIssueFolderDTO testIssueFolderDTO = modeMapper.map(testIssueFolderE, TestIssueFolderDTO.class);
//        return ConvertHelper.convertList(testIssueFolderMapper.select(testIssueFolderDTO), TestIssueFolderE.class);
//    }
//
//    @Override
//    public TestIssueFolderE queryOne(TestIssueFolderE testIssueFolderE) {
//        TestIssueFolderDTO testIssueFolderDTO = modeMapper.map(testIssueFolderE, TestIssueFolderDTO.class);
//        return modeMapper.map(testIssueFolderMapper.selectOne(testIssueFolderDTO), TestIssueFolderE.class);
//    }
//
//    @Override
//    public TestIssueFolderE queryByPrimaryKey(Long folderId) {
//        return modeMapper.map(testIssueFolderMapper.selectByPrimaryKey(folderId), TestIssueFolderE.class);
//    }
//
//
//    @Override
//    public TestIssueFolderE insert(TestIssueFolderE testIssueFolderE) {
//        if (testIssueFolderE == null || testIssueFolderE.getFolderId() != null) {
//            throw new CommonException("error.issue.folder.insert.folderId.should.be.null");
//        }
//        TestIssueFolderDTO testIssueFolderDTO = modeMapper.map(testIssueFolderE, TestIssueFolderDTO.class);
//        testIssueFolderMapper.insert(testIssueFolderDTO);
//        return modeMapper.map(testIssueFolderDTO, TestIssueFolderE.class);
//    }
//
//    @Override
//    public void delete(TestIssueFolderE testIssueFolderE) {
//        TestIssueFolderDTO testIssueFolderDTO = modeMapper.map(testIssueFolderE, TestIssueFolderDTO.class);
//        testIssueFolderMapper.delete(testIssueFolderDTO);
//    }
//
//    @Override
//    public TestIssueFolderE update(TestIssueFolderE testIssueFolderE) {
//        TestIssueFolderDTO testIssueFolderDTO = modeMapper.map(testIssueFolderE, TestIssueFolderDTO.class);
//        if (testIssueFolderMapper.updateByPrimaryKeySelective(testIssueFolderDTO) != 1) {
//            throw new IssueFolderException(IssueFolderException.ERROR_UPDATE, testIssueFolderDTO.toString());
//        }
//        return modeMapper.map(testIssueFolderMapper.selectByPrimaryKey(testIssueFolderDTO.getFolderId()), TestIssueFolderE.class);
//    }
//}
