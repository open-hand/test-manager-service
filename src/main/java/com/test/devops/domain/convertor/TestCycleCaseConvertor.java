package com.test.devops.domain.convertor;

import com.test.devops.api.dto.TestCycleCaseDTO;
import com.test.devops.domain.entity.TestCycleCaseE;
import com.test.devops.domain.factory.TestCycleCaseEFactory;
import com.test.devops.infra.dataobject.TestCycleCaseDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
@Component
public class TestCycleCaseConvertor implements ConvertorI<TestCycleCaseE, TestCycleCaseDO, TestCycleCaseDTO> {

	@Override
	public TestCycleCaseE dtoToEntity(TestCycleCaseDTO dto) {
		TestCycleCaseE testCaseStepE = TestCycleCaseEFactory.create();
		BeanUtils.copyProperties(dto, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCycleCaseDTO entityToDto(TestCycleCaseE entity) {
		TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
		BeanUtils.copyProperties(entity, testCycleCaseDTO);
		return testCycleCaseDTO;
	}

	@Override
	public TestCycleCaseE doToEntity(TestCycleCaseDO dataObject) {
		TestCycleCaseE testCaseStepE = TestCycleCaseEFactory.create();
		BeanUtils.copyProperties(dataObject, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCycleCaseDO entityToDo(TestCycleCaseE entity) {
		TestCycleCaseDO testCycleCaseDTO = new TestCycleCaseDO();
		BeanUtils.copyProperties(entity, testCycleCaseDTO);
		return testCycleCaseDTO;
	}
}
