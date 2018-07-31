package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.dataobject.TestCycleDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@Component
public class TestCycleConvertor implements ConvertorI<TestCycleE, TestCycleDO, TestCycleDTO> {

    @Override
    public TestCycleE dtoToEntity(TestCycleDTO dto) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        BeanUtils.copyProperties(dto, testCycleE);
        return testCycleE;
        }

    @Override
    public TestCycleDTO entityToDto(TestCycleE entity) {
        TestCycleDTO testCycleDTO = new TestCycleDTO();
        BeanUtils.copyProperties(entity, testCycleDTO);
        return testCycleDTO;
    }

    @Override
    public TestCycleE doToEntity(TestCycleDO dto) {
        TestCycleE testCycleE = new TestCycleE();
        BeanUtils.copyProperties(dto, testCycleE);
        return testCycleE;
        }

    @Override
    public TestCycleDO entityToDo(TestCycleE entity) {
        TestCycleDO stepDO = new TestCycleDO();
        BeanUtils.copyProperties(entity, stepDO);
        return stepDO;
    }
}
