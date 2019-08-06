package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.test.manager.api.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
public class TestAutomationHistoryConvertor  implements ConvertorI<TestAutomationHistoryE, TestAutomationHistoryE, TestAutomationHistoryDTO> {
    private static final String[] exclude=new String[]{"testAppInstanceDTO", "testAppInstanceDTO"};

    @Override
    public TestAutomationHistoryDTO entityToDto(TestAutomationHistoryE entity) {
        TestAutomationHistoryDTO historyDTO=new TestAutomationHistoryDTO();
        BeanUtils.copyProperties(entity, historyDTO,exclude);
        historyDTO.setTestAppInstanceDTO(ConvertHelper.convert(entity.getTestAppInstanceE(), TestAppInstanceDTO.class));
        return historyDTO;
    }
}
