package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.test.manager.api.dto.TestFileLoadHistoryDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
import io.choerodon.test.manager.domain.test.manager.factory.TestFileLoadHistoryEFactory;
import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TestFileLoadHistoryConvertor  implements ConvertorI<TestFileLoadHistoryE, TestFileLoadHistoryDO, TestFileLoadHistoryDTO> {

    @Override
    public TestFileLoadHistoryE dtoToEntity(TestFileLoadHistoryDTO dto) {
        TestFileLoadHistoryE testCycleE = TestFileLoadHistoryEFactory.create();
        BeanUtils.copyProperties(dto, testCycleE);
        return testCycleE;
    }

    @Override
    public TestFileLoadHistoryDTO entityToDto(TestFileLoadHistoryE entity) {
        TestFileLoadHistoryDTO testCycleDTO = new TestFileLoadHistoryDTO();
        BeanUtils.copyProperties(entity, testCycleDTO);
        return testCycleDTO;
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
