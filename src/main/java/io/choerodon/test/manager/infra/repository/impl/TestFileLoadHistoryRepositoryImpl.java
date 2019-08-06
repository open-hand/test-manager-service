//package io.choerodon.test.manager.infra.repository.impl;
//
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.test.manager.domain.repository.TestFileLoadHistoryRepository;
//import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
//import io.choerodon.test.manager.infra.vo.TestFileLoadHistoryDTO;
//import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//@Component
//public class TestFileLoadHistoryRepositoryImpl implements TestFileLoadHistoryRepository {
//
//    @Autowired
//    TestFileLoadHistoryMapper testFileLoadHistoryMapper;
//
//    @Override
//    public TestFileLoadHistoryE insertOne(TestFileLoadHistoryE testFileLoadHistoryE) {
//        TestFileLoadHistoryDTO testIssueFolderRelDO = modeMapper.map(testFileLoadHistoryE, TestFileLoadHistoryDTO.class);
//        testFileLoadHistoryMapper.insert(testIssueFolderRelDO);
//        return modeMapper.map(testFileLoadHistoryMapper.selectByPrimaryKey(testIssueFolderRelDO), TestFileLoadHistoryE.class);
//    }
//
//    @Override
//    public TestFileLoadHistoryE update(TestFileLoadHistoryE testFileLoadHistoryE) {
//        TestFileLoadHistoryDTO testIssueFolderRelDO = modeMapper.map(testFileLoadHistoryE, TestFileLoadHistoryDTO.class);
//        testFileLoadHistoryMapper.updateByPrimaryKey(testIssueFolderRelDO);
//        return modeMapper.map(testIssueFolderRelDO, TestFileLoadHistoryE.class);
//    }
//
//    @Override
//    public List<TestFileLoadHistoryE> queryDownloadFile(TestFileLoadHistoryE testFileLoadHistoryE) {
//        TestFileLoadHistoryDTO testIssueFolderRelDO = modeMapper.map(testFileLoadHistoryE, TestFileLoadHistoryDTO.class);
//        return ConvertHelper.convertList(testFileLoadHistoryMapper.queryDownloadFile(testIssueFolderRelDO), TestFileLoadHistoryE.class);
//    }
//
//    @Override
//    public TestFileLoadHistoryE queryByPrimaryKey(Long id) {
//        TestFileLoadHistoryDTO testFileLoadHistoryDTO = new TestFileLoadHistoryDTO();
//        testFileLoadHistoryDTO.setId(id);
//        return modeMapper.map(testFileLoadHistoryMapper.selectByPrimaryKey(testFileLoadHistoryDTO), TestFileLoadHistoryE.class);
//    }
//
//    @Override
//    public TestFileLoadHistoryE queryLatestHistory(TestFileLoadHistoryE testFileLoadHistoryE) {
//        TestFileLoadHistoryDTO testFileLoadHistoryDTO = modeMapper.map(testFileLoadHistoryE, TestFileLoadHistoryDTO.class);
//        List<TestFileLoadHistoryDTO> testFileLoadHistoryDTOS = testFileLoadHistoryMapper.queryLatestHistory(testFileLoadHistoryDTO);
//        if (testFileLoadHistoryDTOS == null || testFileLoadHistoryDTOS.isEmpty()) {
//            return null;
//        }
//        return modeMapper.map(testFileLoadHistoryDTOS.get(0), TestFileLoadHistoryE.class);
//    }
//
//    @Override
//    public TestFileLoadHistoryEnums.Status queryLoadHistoryStatus(Long id) {
//        return TestFileLoadHistoryEnums.Status.valueOf(testFileLoadHistoryMapper.queryLoadHistoryStatus(id));
//    }
//
//    @Override
//    public boolean cancelFileUpload(Long historyId) {
//        return testFileLoadHistoryMapper.cancelFileUpload(historyId) == 1;
//    }
//
//    public List<TestFileLoadHistoryE> queryDownloadFileByParameter(TestFileLoadHistoryE testFileLoadHistoryE) {
//        TestFileLoadHistoryDTO testIssueFolderRelDO = modeMapper.map(testFileLoadHistoryE, TestFileLoadHistoryDTO.class);
//        List<TestFileLoadHistoryDTO> res = testFileLoadHistoryMapper.select(testIssueFolderRelDO);
//        Collections.sort(res, Comparator.comparing(TestFileLoadHistoryDTO::getCreationDate));
//        return ConvertHelper.convertList(res, TestFileLoadHistoryE.class);
//    }
//}
