package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelService {
	TestCycleCaseDefectRelDTO insert(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, Long projectId,Long organizationId);

	void delete(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, Long projectId);

	void populateDefectInfo(List<TestCycleCaseDefectRelDTO> lists, Long projectId,Long organizationId);

	void populateDefectAndIssue(TestCycleCaseDTO dto ,Long projectId,Long organizationId);

	void populateCycleCaseDefectInfo(List<TestCycleCaseDTO> testCycleCaseDTOS, Long projectId,Long organizationId);

	void populateCaseStepDefectInfo(List<TestCycleCaseStepDTO> testCycleCaseDTOS, Long projectId,Long organizationId);


	Boolean updateIssuesProjectId(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO,Long organizationId);
}
