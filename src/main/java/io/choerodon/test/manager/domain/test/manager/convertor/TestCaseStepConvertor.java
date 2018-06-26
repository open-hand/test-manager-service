package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
import io.choerodon.test.manager.infra.dataobject.TestCaseStepDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCaseStepConvertor implements ConvertorI<TestCaseStepE, TestCaseStepDO, TestCaseStepDTO> {
	@Override
	public TestCaseStepE doToEntity(TestCaseStepDO dataObject) {
		TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
		BeanUtils.copyProperties(dataObject, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCaseStepDO entityToDo(TestCaseStepE entity) {
		TestCaseStepDO stepDO = new TestCaseStepDO();
		BeanUtils.copyProperties(entity, stepDO);
		return stepDO;
	}

	@Override
	public TestCaseStepE dtoToEntity(TestCaseStepDTO dto) {
		TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
		BeanUtils.copyProperties(dto, testCaseStepE);

		return testCaseStepE;
	}

	@Override
	public TestCaseStepDTO entityToDto(TestCaseStepE entity) {
		TestCaseStepDTO stepDTO = new TestCaseStepDTO();
		BeanUtils.copyProperties(entity, stepDTO);
		return stepDTO;
	}
}
