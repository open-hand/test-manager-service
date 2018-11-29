package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.test.manager.api.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by zongw.lee@gmail.com on 11/28/2018
 */
@Component
public class TestAppInstanceConvertor implements ConvertorI<TestAppInstanceE, TestAppInstanceE, TestAppInstanceDTO> {
    @Override
    public TestAppInstanceE dtoToEntity(TestAppInstanceDTO dto) {
        TestAppInstanceE testAppInstanceE = new TestAppInstanceE();
        BeanUtils.copyProperties(dto, testAppInstanceE);
        return testAppInstanceE;
    }

    @Override
    public TestAppInstanceDTO entityToDto(TestAppInstanceE entity) {
        TestAppInstanceDTO testIssueFolderDTO = new TestAppInstanceDTO();
        BeanUtils.copyProperties(entity, testIssueFolderDTO);
        return testIssueFolderDTO;
    }

}
