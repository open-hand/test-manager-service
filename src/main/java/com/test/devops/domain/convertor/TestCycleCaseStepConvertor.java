package com.test.devops.domain.convertor;

import com.test.devops.api.dto.TestCycleCaseStepDTO;
import com.test.devops.domain.entity.TestCycleCaseStepE;
import com.test.devops.domain.factory.TestCycleCaseStepEFactory;
import com.test.devops.infra.dataobject.TestCycleCaseStepDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
@Component
public class TestCycleCaseStepConvertor implements ConvertorI<TestCycleCaseStepE, TestCycleCaseStepDO, TestCycleCaseStepDTO> {
	@Override
	public TestCycleCaseStepE dtoToEntity(TestCycleCaseStepDTO dto) {
		TestCycleCaseStepE testCaseStepE = TestCycleCaseStepEFactory.create();
		BeanUtils.copyProperties(dto, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCycleCaseStepDTO entityToDto(TestCycleCaseStepE entity) {
		TestCycleCaseStepDTO testCycleCaseDTO = new TestCycleCaseStepDTO();
		BeanUtils.copyProperties(entity, testCycleCaseDTO);
		return testCycleCaseDTO;
	}

	@Override
	public TestCycleCaseStepE doToEntity(TestCycleCaseStepDO dataObject) {
		TestCycleCaseStepE testCaseStepE = TestCycleCaseStepEFactory.create();
		BeanUtils.copyProperties(dataObject, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCycleCaseStepDO entityToDo(TestCycleCaseStepE entity) {
		TestCycleCaseStepDO testCycleCaseDTO = new TestCycleCaseStepDO();
		BeanUtils.copyProperties(entity, testCycleCaseDTO);
		return testCycleCaseDTO;
	}
}
