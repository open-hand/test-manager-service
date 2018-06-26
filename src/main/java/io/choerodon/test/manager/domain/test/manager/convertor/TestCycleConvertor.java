package io.choerodon.test.manager.domain.test.manager.convertor;

import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.dataobject.TestCycleDO;
import io.choerodon.core.convertor.ConvertorI;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by 842767365@qq.com on 6/12/18.
 */
@Component
public class TestCycleConvertor implements ConvertorI<TestCycleE, TestCycleDO, TestCycleDTO> {

	@Override
	public TestCycleE dtoToEntity(TestCycleDTO dto) {
		return getTestCycleE(dto.getBuild(), dto.getCycleName(), dto.getDescription(), dto.getEnvironment(), dto.getType(), dto.getVersionId(), dto.getCycleId(), dto.getParentCycleId(), dto.getFromDate(), dto.getToDate(), dto.getObjectVersionNumber());
	}

	@Override
	public TestCycleDTO entityToDto(TestCycleE entity) {
		TestCycleDTO stepDO = new TestCycleDTO();
		stepDO.setBuild(entity.getBuild());
		stepDO.setCycleName(entity.getCycleName());
		stepDO.setDescription(entity.getDescription());
		stepDO.setEnvironment(entity.getEnvironment());
		stepDO.setType(entity.getType());
		stepDO.setVersionId(entity.getVersionId());
		stepDO.setCycleId(entity.getCycleId());
		stepDO.setParentCycleId(entity.getParentCycleId());
		stepDO.setFromDate(entity.getFromDate());
		stepDO.setToDate(entity.getToDate());
		stepDO.setObjectVersionNumber(entity.getObjectVersionNumber());
		return stepDO;
	}

	@Override
	public TestCycleE doToEntity(TestCycleDO dto) {
		return getTestCycleE(dto.getBuild(), dto.getCycleName(), dto.getDescription(), dto.getEnvironment(), dto.getType(), dto.getVersionId(), dto.getCycleId(), dto.getParentCycleId(), dto.getFromDate(), dto.getToDate(), dto.getObjectVersionNumber());
	}

	private TestCycleE getTestCycleE(String build, String cycleName, String description, String environment, String type, Long versionId, Long cycleId, Long parentCycleId, Date fromDate, Date toDate, Long objectVersionNumber) {
		TestCycleE testCaseStepE = TestCycleEFactory.create();
		testCaseStepE.setBuild(build);
		testCaseStepE.setCycleName(cycleName);
		testCaseStepE.setDescription(description);
		testCaseStepE.setEnvironment(environment);
		testCaseStepE.setType(type);
		testCaseStepE.setVersionId(versionId);
		testCaseStepE.setCycleId(cycleId);
		testCaseStepE.setParentCycleId(parentCycleId);
		testCaseStepE.setFromDate(fromDate);
		testCaseStepE.setToDate(toDate);
		testCaseStepE.setObjectVersionNumber(objectVersionNumber);
		return testCaseStepE;
	}

	@Override
	public TestCycleDO entityToDo(TestCycleE entity) {
		TestCycleDO stepDO = new TestCycleDO();
		stepDO.setBuild(entity.getBuild());
		stepDO.setCycleName(entity.getCycleName());
		stepDO.setDescription(entity.getDescription());
		stepDO.setEnvironment(entity.getEnvironment());
		stepDO.setType(entity.getType());
		stepDO.setVersionId(entity.getVersionId());
		stepDO.setCycleId(entity.getCycleId());
		stepDO.setParentCycleId(entity.getParentCycleId());
		stepDO.setFromDate(entity.getFromDate());
		stepDO.setToDate(entity.getToDate());
		stepDO.setObjectVersionNumber(entity.getObjectVersionNumber());
		return stepDO;
	}
}
