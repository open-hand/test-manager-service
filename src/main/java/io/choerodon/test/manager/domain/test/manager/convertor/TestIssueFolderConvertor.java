package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.core.convertor.ConvertorI;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderEFactory;
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderDO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@Component
public class TestIssueFolderConvertor implements ConvertorI<TestIssueFolderE, TestIssueFolderDO, TestIssueFolderDTO> {
    @Override
    public TestIssueFolderE dtoToEntity(TestIssueFolderDTO dto) {
        TestIssueFolderE testIssueFolderE = TestIssueFolderEFactory.create();
        BeanUtils.copyProperties(dto, testIssueFolderE);
        return testIssueFolderE;
    }

    @Override
    public TestIssueFolderDTO entityToDto(TestIssueFolderE entity) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        BeanUtils.copyProperties(entity, testIssueFolderDTO);
        return testIssueFolderDTO;
    }

    @Override
    public TestIssueFolderE doToEntity(TestIssueFolderDO dataObject) {
        TestIssueFolderE testIssueFolderE = TestIssueFolderEFactory.create();
        BeanUtils.copyProperties(dataObject, testIssueFolderE);
        return testIssueFolderE;
    }

    @Override
    public TestIssueFolderDO entityToDo(TestIssueFolderE entity) {
        TestIssueFolderDO testIssueFolderDO = new TestIssueFolderDO();
        BeanUtils.copyProperties(entity, testIssueFolderDO);
        return testIssueFolderDO;
    }

}
