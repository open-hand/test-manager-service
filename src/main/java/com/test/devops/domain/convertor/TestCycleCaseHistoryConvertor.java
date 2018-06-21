package com.test.devops.domain.convertor;

import com.test.devops.api.dto.TestCycleCaseHistoryDTO;
import com.test.devops.domain.entity.TestCycleCaseHistoryE;
import com.test.devops.domain.factory.TestCycleCaseHistoryEFactory;
import com.test.devops.infra.dataobject.TestCycleCaseHistoryDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
@Component
public class TestCycleCaseHistoryConvertor implements ConvertorI<TestCycleCaseHistoryE, TestCycleCaseHistoryDO, TestCycleCaseHistoryDTO> {

	@Override
	public TestCycleCaseHistoryE dtoToEntity(TestCycleCaseHistoryDTO dto) {
		TestCycleCaseHistoryE testCaseStepE = TestCycleCaseHistoryEFactory.create();
		BeanUtils.copyProperties(dto, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCycleCaseHistoryDTO entityToDto(TestCycleCaseHistoryE entity) {
		TestCycleCaseHistoryDTO testCycleCaseDTO = new TestCycleCaseHistoryDTO();
		BeanUtils.copyProperties(entity, testCycleCaseDTO);
		return testCycleCaseDTO;
	}

	@Override
	public TestCycleCaseHistoryE doToEntity(TestCycleCaseHistoryDO dataObject) {
		TestCycleCaseHistoryE testCaseStepE = TestCycleCaseHistoryEFactory.create();
		BeanUtils.copyProperties(dataObject, testCaseStepE);
		return testCaseStepE;
	}

	@Override
	public TestCycleCaseHistoryDO entityToDo(TestCycleCaseHistoryE entity) {
		TestCycleCaseHistoryDO testCycleCaseDTO = new TestCycleCaseHistoryDO();
		BeanUtils.copyProperties(entity, testCycleCaseDTO);
		return testCycleCaseDTO;
	}
}
