package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;

import java.util.List;

public interface ITestFileLoadHistoryService {

    TestFileLoadHistoryE insertOne(TestFileLoadHistoryE testFileLoadHistoryE);

    TestFileLoadHistoryE update(TestFileLoadHistoryE testFileLoadHistoryE);

    List<TestFileLoadHistoryE> queryDownloadFile(TestFileLoadHistoryE testFileLoadHistoryE);

    TestFileLoadHistoryE queryByPrimaryKey(Long id);

}
