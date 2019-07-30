//package io.choerodon.test.manager.domain.repository;
//
//import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
//
//import java.util.List;
//
//public interface TestFileLoadHistoryRepository {
//
//    TestFileLoadHistoryE insertOne(TestFileLoadHistoryE testFileLoadHistoryE);
//
//    TestFileLoadHistoryE update(TestFileLoadHistoryE testFileLoadHistoryE);
//
//    List<TestFileLoadHistoryE> queryDownloadFile(TestFileLoadHistoryE testFileLoadHistoryE);
//
//    TestFileLoadHistoryE queryByPrimaryKey(Long id);
//
//    TestFileLoadHistoryE queryLatestHistory(TestFileLoadHistoryE testFileLoadHistoryE);
//
//    TestFileLoadHistoryEnums.Status queryLoadHistoryStatus(Long id);
//
//    boolean cancelFileUpload(Long historyId);
//
//    List<TestFileLoadHistoryE> queryDownloadFileByParameter(TestFileLoadHistoryE testFileLoadHistoryE);
//
//}
