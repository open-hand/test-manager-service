package io.choerodon.test.manager.domain.repository;

import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;

import java.util.List;

public interface TestFileLoadHistoryRepository {

    TestFileLoadHistoryE insertOne(TestFileLoadHistoryE testFileLoadHistoryE);

    TestFileLoadHistoryE update(TestFileLoadHistoryE testFileLoadHistoryE);

    List<TestFileLoadHistoryE>queryDownloadFile(TestFileLoadHistoryE testFileLoadHistoryE);
}
