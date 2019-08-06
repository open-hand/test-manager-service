package io.choerodon.test.manager.domain.test.manager.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.test.manager.api.dto.TestIssuesUploadHistoryDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
import io.choerodon.test.manager.domain.test.manager.factory.TestFileLoadHistoryEFactory;
import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO;

@Component
public class TesIssuesUploadHistoryConvertor implements ConvertorI<TestFileLoadHistoryE, TestFileLoadHistoryDO, TestIssuesUploadHistoryDTO> {

    @Override
    public TestIssuesUploadHistoryDTO entityToDto(TestFileLoadHistoryE entity) {
        TestIssuesUploadHistoryDTO testIssuesUploadHistoryDTO = new TestIssuesUploadHistoryDTO();
        BeanUtils.copyProperties(entity, testIssuesUploadHistoryDTO);
        return testIssuesUploadHistoryDTO;
    }


    @Override
    public TestFileLoadHistoryE doToEntity(TestFileLoadHistoryDO dto) {
        TestFileLoadHistoryE testCycleE = TestFileLoadHistoryEFactory.create();
        BeanUtils.copyProperties(dto, testCycleE);
        return testCycleE;
    }

    @Override
    public TestFileLoadHistoryDO entityToDo(TestFileLoadHistoryE entity) {
        TestFileLoadHistoryDO stepDO = new TestFileLoadHistoryDO();
        BeanUtils.copyProperties(entity, stepDO);
        return stepDO;
    }

}
