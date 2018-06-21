package com.test.devops.domain.convertor;

import com.test.devops.api.dto.TestCycleCaseDefectRelDTO;
import com.test.devops.domain.entity.TestCycleCaseDefectRelE;
import com.test.devops.domain.factory.TestCycleCaseDefectRelEFactory;
import com.test.devops.infra.dataobject.TestCycleCaseDefectRelDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
@Component
public class TestCycleCaseDefectRelConvertor implements ConvertorI<TestCycleCaseDefectRelE, TestCycleCaseDefectRelDO, TestCycleCaseDefectRelDTO> {
	@Override
	public TestCycleCaseDefectRelE dtoToEntity(TestCycleCaseDefectRelDTO dto) {
		TestCycleCaseDefectRelE testCaseStepE = TestCycleCaseDefectRelEFactory.create();
		BeanUtils.copyProperties(dto, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCycleCaseDefectRelDTO entityToDto(TestCycleCaseDefectRelE entity) {
		TestCycleCaseDefectRelDTO testCycleCaseDTO = new TestCycleCaseDefectRelDTO();
		BeanUtils.copyProperties(entity, testCycleCaseDTO);
		return testCycleCaseDTO;
	}

	@Override
	public TestCycleCaseDefectRelE doToEntity(TestCycleCaseDefectRelDO dataObject) {
		TestCycleCaseDefectRelE testCaseStepE = TestCycleCaseDefectRelEFactory.create();
		BeanUtils.copyProperties(dataObject, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCycleCaseDefectRelDO entityToDo(TestCycleCaseDefectRelE entity) {
		TestCycleCaseDefectRelDO testCycleCaseDTO = new TestCycleCaseDefectRelDO();
		BeanUtils.copyProperties(entity, testCycleCaseDTO);
		return testCycleCaseDTO;
	}
}
