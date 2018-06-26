package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.test.manager.api.dto.TestStatusDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory;
import io.choerodon.test.manager.infra.dataobject.TestStatusDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jialongZuo@hand-china.com on 6/25/18.
 */
@Component
public class TestStatusEConvertor implements ConvertorI<TestStatusE, TestStatusDO, TestStatusDTO> {
    @Override
    public TestStatusE dtoToEntity(TestStatusDTO dto) {
        TestStatusE testCaseStepE = TestStatusEFactory.create();
        BeanUtils.copyProperties(dto, testCaseStepE);
        return testCaseStepE;
    }

    @Override
    public TestStatusDTO entityToDto(TestStatusE entity) {
        TestStatusDTO testCycleCaseDTO = new TestStatusDTO();
        BeanUtils.copyProperties(entity, testCycleCaseDTO);
        return testCycleCaseDTO;
    }

    @Override
    public TestStatusE doToEntity(TestStatusDO dataObject) {
        TestStatusE testCaseStepE = TestStatusEFactory.create();
        BeanUtils.copyProperties(dataObject, testCaseStepE);
        return testCaseStepE;
    }

    @Override
    public TestStatusDO entityToDo(TestStatusE entity) {
        TestStatusDO testCycleCaseDTO = new TestStatusDO();
        BeanUtils.copyProperties(entity, testCycleCaseDTO);
        return testCycleCaseDTO;
    }

}
