package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderRelEFactory;
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderRelDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
@Component
public class TestIssueFolderRelConvertor implements ConvertorI<TestIssueFolderRelE, TestIssueFolderRelDO, TestIssueFolderRelDTO> {
    @Override
    public TestIssueFolderRelE dtoToEntity(TestIssueFolderRelDTO dto) {
        TestIssueFolderRelE testIssueFolderRelE = TestIssueFolderRelEFactory.create();
        BeanUtils.copyProperties(dto, testIssueFolderRelE);
        return testIssueFolderRelE;
    }

    @Override
    public TestIssueFolderRelDTO entityToDto(TestIssueFolderRelE entity) {
        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
        BeanUtils.copyProperties(entity, testIssueFolderRelDTO);
        return testIssueFolderRelDTO;
    }

    @Override
    public TestIssueFolderRelE doToEntity(TestIssueFolderRelDO dataObject) {
        TestIssueFolderRelE testIssueFolderRelE = TestIssueFolderRelEFactory.create();
        BeanUtils.copyProperties(dataObject, testIssueFolderRelE);
        return testIssueFolderRelE;
    }

    @Override
    public TestIssueFolderRelDO entityToDo(TestIssueFolderRelE entity) {
        TestIssueFolderRelDO testIssueFolderRelDO = new TestIssueFolderRelDO();
        BeanUtils.copyProperties(entity, testIssueFolderRelDO);
        return testIssueFolderRelDO;
    }

}
