package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.repository.TestFileLoadHistoryRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO;
import io.choerodon.test.manager.infra.mapper.TestFileLoadHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestFileLoadHistoryRepositoryImpl implements TestFileLoadHistoryRepository {

    @Autowired
    TestFileLoadHistoryMapper testFileLoadHistoryMapper;

    @Override
    public TestFileLoadHistoryE insertOne(TestFileLoadHistoryE testFileLoadHistoryE) {
        TestFileLoadHistoryDO testIssueFolderRelDO = ConvertHelper.convert(testFileLoadHistoryE, TestFileLoadHistoryDO.class);
        testFileLoadHistoryMapper.insert(testIssueFolderRelDO);
        return ConvertHelper.convert(testFileLoadHistoryMapper.selectByPrimaryKey(testIssueFolderRelDO), TestFileLoadHistoryE.class);
    }

    @Override
    public TestFileLoadHistoryE update(TestFileLoadHistoryE testFileLoadHistoryE) {
        TestFileLoadHistoryDO testIssueFolderRelDO = ConvertHelper.convert(testFileLoadHistoryE, TestFileLoadHistoryDO.class);
        testFileLoadHistoryMapper.updateByPrimaryKey(testIssueFolderRelDO);
        return ConvertHelper.convert(testIssueFolderRelDO, TestFileLoadHistoryE.class);
    }

    @Override
    public List<TestFileLoadHistoryE> query(TestFileLoadHistoryE testFileLoadHistoryE) {
        TestFileLoadHistoryDO testIssueFolderRelDO = ConvertHelper.convert(testFileLoadHistoryE, TestFileLoadHistoryDO.class);
        return ConvertHelper.convertList(testFileLoadHistoryMapper.select(testIssueFolderRelDO), TestFileLoadHistoryE.class);
    }
}
