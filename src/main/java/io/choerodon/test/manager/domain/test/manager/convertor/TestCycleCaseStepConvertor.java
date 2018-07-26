package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@Component
public class TestCycleCaseStepConvertor implements ConvertorI<TestCycleCaseStepE, TestCycleCaseStepDO, TestCycleCaseStepDTO> {
    final String[] excludeParam=new String[]{"caseAttachment","stepAttachment","defects"};
    @Override
    public TestCycleCaseStepE dtoToEntity(TestCycleCaseStepDTO dto) {
        TestCycleCaseStepE testCaseStepE = TestCycleCaseStepEFactory.create();
        BeanUtils.copyProperties(dto, testCaseStepE,excludeParam);
        return testCaseStepE;
    }

    @Override
    public TestCycleCaseStepDTO entityToDto(TestCycleCaseStepE entity) {
        TestCycleCaseStepDTO testCycleCaseDTO = new TestCycleCaseStepDTO();
        BeanUtils.copyProperties(entity, testCycleCaseDTO,excludeParam);
        testCycleCaseDTO.setCaseAttachment(entity.getCaseAttachment());
        testCycleCaseDTO.setStepAttachment(entity.getStepAttachment());
        testCycleCaseDTO.setDefects(entity.getDefects());
        return testCycleCaseDTO;
    }

    @Override
    public TestCycleCaseStepE doToEntity(TestCycleCaseStepDO dataObject) {
        TestCycleCaseStepE testCaseStepE = TestCycleCaseStepEFactory.create();
        BeanUtils.copyProperties(dataObject, testCaseStepE,excludeParam);
        testCaseStepE.setCaseAttachment(dataObject.getCaseAttachment());
        testCaseStepE.setStepAttachment(dataObject.getStepAttachment());
        testCaseStepE.setDefects(dataObject.getDefects());
        return testCaseStepE;
    }

    @Override
    public TestCycleCaseStepDO entityToDo(TestCycleCaseStepE entity) {
        TestCycleCaseStepDO testCycleCaseDTO = new TestCycleCaseStepDO();
        BeanUtils.copyProperties(entity, testCycleCaseDTO,excludeParam);
        return testCycleCaseDTO;
    }
}
