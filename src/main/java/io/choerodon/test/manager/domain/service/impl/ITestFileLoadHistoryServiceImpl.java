package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.repository.TestFileLoadHistoryRepository;
import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ITestFileLoadHistoryServiceImpl implements ITestFileLoadHistoryService {
    @Autowired
    TestFileLoadHistoryRepository testFileLoadHistoryRepository;

    @Override
    public TestFileLoadHistoryE insertOne(TestFileLoadHistoryE testFileLoadHistoryE) {
        return testFileLoadHistoryRepository.insertOne(testFileLoadHistoryE);
    }

    @Override
    public TestFileLoadHistoryE update(TestFileLoadHistoryE testFileLoadHistoryE) {
        return testFileLoadHistoryRepository.update(testFileLoadHistoryE);
    }

    @Override
    public List<TestFileLoadHistoryE> queryDownloadFile(TestFileLoadHistoryE testFileLoadHistoryE) {
        return testFileLoadHistoryRepository.queryDownloadFile(testFileLoadHistoryE);
    }

    @Override
    public TestFileLoadHistoryE queryByPrimaryKey(Long id) {
        return testFileLoadHistoryRepository.queryByPrimaryKey(id);
    }

    @Override
    public TestFileLoadHistoryE queryLatestImportIssueHistory(TestFileLoadHistoryE testFileLoadHistoryE) {
        return testFileLoadHistoryRepository.queryLatestImportIssueHistory(testFileLoadHistoryE);
    }
}
