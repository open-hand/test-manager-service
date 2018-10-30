package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.TestFileLoadHistoryDTO;
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService;
import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestFileLoadHistoryServiceImpl implements TestFileLoadHistoryService {

    @Autowired
    ITestFileLoadHistoryService iTestFileLoadHistoryService;
    @Override
    public List<TestFileLoadHistoryDTO> query(TestFileLoadHistoryDTO testFileLoadHistoryDTO) {
        return ConvertHelper.convertList(iTestFileLoadHistoryService.query( ConvertHelper.convert(testFileLoadHistoryDTO, TestFileLoadHistoryE.class)),TestFileLoadHistoryDTO.class);
    }
}
