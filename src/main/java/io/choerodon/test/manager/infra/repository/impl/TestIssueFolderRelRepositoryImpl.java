//package io.choerodon.test.manager.infra.repository.impl;
//
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.test.manager.domain.repository.TestIssueFolderRelRepository;
//import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
//import io.choerodon.test.manager.infra.vo.TestIssueFolderRelDTO;
//import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * Created by zongw.lee@gmail.com on 08/31/2018
// */
//@Component
//public class TestIssueFolderRelRepositoryImpl implements TestIssueFolderRelRepository {
//
//    @Autowired
//    TestIssueFolderRelMapper testIssueFolderRelMapper;
//
//    @Override
//    public List<TestIssueFolderRelE> queryAllUnderProject(TestIssueFolderRelE testIssueFolderRelE) {
//        TestIssueFolderRelDTO testIssueFolderRelDTO = modeMapper.map(testIssueFolderRelE, TestIssueFolderRelDTO.class);
//        return ConvertHelper.convertList(testIssueFolderRelMapper.select(testIssueFolderRelDTO), TestIssueFolderRelE.class);
//    }
//
//    @Override
//    public TestIssueFolderRelE queryOneIssueUnderProjectVersionFolder(TestIssueFolderRelE testIssueFolderRelE) {
//        TestIssueFolderRelDTO testIssueFolderRelDTO = modeMapper.map(testIssueFolderRelE, TestIssueFolderRelDTO.class);
//        return modeMapper.map(testIssueFolderRelMapper.selectOne(testIssueFolderRelDTO), TestIssueFolderRelE.class);
//    }
//
//    @Override
//    public TestIssueFolderRelE insert(TestIssueFolderRelE testIssueFolderRelE) {
//        TestIssueFolderRelDTO testIssueFolderRelDTO = modeMapper.map(testIssueFolderRelE, TestIssueFolderRelDTO.class);
//        testIssueFolderRelMapper.insert(testIssueFolderRelDTO);
//        return modeMapper.map(testIssueFolderRelDTO, TestIssueFolderRelE.class);
//    }
//
//    @Override
//    public void delete(TestIssueFolderRelE testIssueFolderRelE) {
//        TestIssueFolderRelDTO testIssueFolderRelDTO = modeMapper.map(testIssueFolderRelE, TestIssueFolderRelDTO.class);
//        testIssueFolderRelMapper.delete(testIssueFolderRelDTO);
//    }
//
//    @Override
//    public TestIssueFolderRelE updateFolderByIssue(TestIssueFolderRelE testIssueFolderRelE) {
//        TestIssueFolderRelDTO testIssueFolderRelDTO = modeMapper.map(testIssueFolderRelE, TestIssueFolderRelDTO.class);
//        testIssueFolderRelMapper.updateFolderByIssue(testIssueFolderRelDTO);
//        return modeMapper.map(testIssueFolderRelMapper.selectByPrimaryKey(testIssueFolderRelDTO.getId()), TestIssueFolderRelE.class);
//    }
//
//    @Override
//    public TestIssueFolderRelE updateVersionByFolderWithNoLock(TestIssueFolderRelE testIssueFolderRelE) {
//        TestIssueFolderRelDTO testIssueFolderRelDTO = modeMapper.map(testIssueFolderRelE, TestIssueFolderRelDTO.class);
//        testIssueFolderRelMapper.updateVersionByFolderWithNoLock(testIssueFolderRelDTO);
//        return modeMapper.map(testIssueFolderRelMapper.selectByPrimaryKey(testIssueFolderRelDTO.getId()), TestIssueFolderRelE.class);
//    }
//}
