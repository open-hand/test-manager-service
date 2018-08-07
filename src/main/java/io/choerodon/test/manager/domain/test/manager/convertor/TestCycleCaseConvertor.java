package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@Component
public class TestCycleCaseConvertor implements ConvertorI<TestCycleCaseE, TestCycleCaseDO, TestCycleCaseDTO> {

    @Override
    public TestCycleCaseE dtoToEntity(TestCycleCaseDTO dto) {
        TestCycleCaseE testCaseStepE = TestCycleCaseEFactory.create();
        BeanUtils.copyProperties(dto, testCaseStepE,new String[]{"caseAttachment","caseDefect","subStepDefects"});
        return testCaseStepE;
    }

    @Override
    public TestCycleCaseDTO entityToDto(TestCycleCaseE entity) {
        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        BeanUtils.copyProperties(entity, testCycleCaseDTO,new String[]{"caseAttachment","caseDefect","subStepDefects"});
        testCycleCaseDTO.setCaseAttachment(entity.getCaseAttachment());
        testCycleCaseDTO.setDefects(entity.getDefects());
        testCycleCaseDTO.setSubStepDefects(entity.getSubStepDefects());
        return testCycleCaseDTO;
    }

    @Override
    public TestCycleCaseE doToEntity(TestCycleCaseDO dataObject) {
        TestCycleCaseE testCaseStepE = TestCycleCaseEFactory.create();
        BeanUtils.copyProperties(dataObject, testCaseStepE,new String[]{"caseAttachment","caseDefect","subStepDefects"});
        testCaseStepE.setCaseAttachment(dataObject.getCaseAttachment());
        testCaseStepE.setDefects(dataObject.getCaseDefect());
        testCaseStepE.setSubStepDefects(dataObject.getSubStepDefects());
        return testCaseStepE;
    }

    @Override
    public TestCycleCaseDO entityToDo(TestCycleCaseE entity) {
        TestCycleCaseDO testCycleCaseDTO = new TestCycleCaseDO();
        BeanUtils.copyProperties(entity, testCycleCaseDTO,new String[]{"caseAttachment","caseDefect","subStepDefects"});
        return testCycleCaseDTO;
    }
}
