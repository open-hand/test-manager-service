package io.choerodon.test.manager.domain.test.manager.convertor;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.test.manager.api.dto.TestAutomationResultDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationResultE;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import io.choerodon.test.manager.infra.dataobject.TestAutomationResultDO;

@Component
public class TestAutomationResultConvertor implements ConvertorI<TestAutomationResultE, TestAutomationResultDO, TestAutomationResultDTO> {

    @Override
    public TestAutomationResultE dtoToEntity(TestAutomationResultDTO dto) {
        TestAutomationResultE testAutomationResultE = SpringUtil.getApplicationContext().getBean(TestAutomationResultE.class);
        BeanUtils.copyProperties(dto, testAutomationResultE);
        return testAutomationResultE;
    }

    @Override
    public TestAutomationResultDTO entityToDto(TestAutomationResultE entity) {
        TestAutomationResultDTO testAutomationResultDTO = new TestAutomationResultDTO();
        BeanUtils.copyProperties(entity, testAutomationResultDTO);
        return testAutomationResultDTO;
    }

    @Override
    public TestAutomationResultE doToEntity(TestAutomationResultDO dataObject) {
        TestAutomationResultE testAutomationResultE = SpringUtil.getApplicationContext().getBean(TestAutomationResultE.class);
        BeanUtils.copyProperties(dataObject, testAutomationResultE);
        return testAutomationResultE;
    }

    @Override
    public TestAutomationResultDO entityToDo(TestAutomationResultE entity) {
        TestAutomationResultDO testAutomationResultDO = new TestAutomationResultDO();
        BeanUtils.copyProperties(entity, testAutomationResultDO);
        return testAutomationResultDO;
    }

}
