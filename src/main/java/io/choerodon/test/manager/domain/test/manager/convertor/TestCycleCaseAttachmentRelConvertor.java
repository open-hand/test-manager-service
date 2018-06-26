package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.test.manager.api.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
@Component
public class TestCycleCaseAttachmentRelConvertor implements ConvertorI<TestCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO, TestCycleCaseAttachmentRelDTO> {

    @Override
    public TestCycleCaseAttachmentRelE dtoToEntity(TestCycleCaseAttachmentRelDTO dto) {
        TestCycleCaseAttachmentRelE testCaseStepE = TestCycleCaseAttachmentRelEFactory.create();
        BeanUtils.copyProperties(dto, testCaseStepE);
        return testCaseStepE;
    }

    @Override
    public TestCycleCaseAttachmentRelDTO entityToDto(TestCycleCaseAttachmentRelE entity) {
        TestCycleCaseAttachmentRelDTO stepDO = new TestCycleCaseAttachmentRelDTO();
        BeanUtils.copyProperties(entity, stepDO);
        return stepDO;
    }

    @Override
    public TestCycleCaseAttachmentRelE doToEntity(TestCycleCaseAttachmentRelDO dataObject) {
        TestCycleCaseAttachmentRelE testCaseStepE = TestCycleCaseAttachmentRelEFactory.create();
        BeanUtils.copyProperties(dataObject, testCaseStepE);
        return testCaseStepE;
    }

    @Override
    public TestCycleCaseAttachmentRelDO entityToDo(TestCycleCaseAttachmentRelE entity) {
        TestCycleCaseAttachmentRelDO stepDO = new TestCycleCaseAttachmentRelDO();
        BeanUtils.copyProperties(entity, stepDO);
        return stepDO;
    }
}
