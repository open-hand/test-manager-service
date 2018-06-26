package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseHistoryEFactory;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseHistoryDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@Component
public class TestCycleCaseHistoryConvertor implements ConvertorI<TestCycleCaseHistoryE, TestCycleCaseHistoryDO, TestCycleCaseHistoryDTO> {

    @Override
    public TestCycleCaseHistoryE dtoToEntity(TestCycleCaseHistoryDTO dto) {
        TestCycleCaseHistoryE testCaseStepE = TestCycleCaseHistoryEFactory.create();
        BeanUtils.copyProperties(dto, testCaseStepE);
        return testCaseStepE;
    }

    @Override
    public TestCycleCaseHistoryDTO entityToDto(TestCycleCaseHistoryE entity) {
        TestCycleCaseHistoryDTO testCycleCaseDTO = new TestCycleCaseHistoryDTO();
        BeanUtils.copyProperties(entity, testCycleCaseDTO);
        return testCycleCaseDTO;
    }

    @Override
    public TestCycleCaseHistoryE doToEntity(TestCycleCaseHistoryDO dataObject) {
        TestCycleCaseHistoryE testCaseStepE = TestCycleCaseHistoryEFactory.create();
        BeanUtils.copyProperties(dataObject, testCaseStepE);
        return testCaseStepE;
    }

    @Override
    public TestCycleCaseHistoryDO entityToDo(TestCycleCaseHistoryE entity) {
        TestCycleCaseHistoryDO testCycleCaseDTO = new TestCycleCaseHistoryDO();
        BeanUtils.copyProperties(entity, testCycleCaseDTO);
        return testCycleCaseDTO;
    }
}
