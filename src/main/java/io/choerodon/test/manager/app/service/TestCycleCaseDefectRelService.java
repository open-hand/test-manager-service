package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelService {
	TestCycleCaseDefectRelDTO insert(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, Long projectId);

	void delete(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, Long projectId);

	void populateDefectInfo(List<TestCycleCaseDefectRelDTO> lists, Long projectId);

	void populateCycleCaseDefectInfo(List<TestCycleCaseDTO> testCycleCaseDTOS, Long projectId);

    List<TestCycleCaseDefectRelDTO> query(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO);

	void populateCaseStepDefectInfo(List<TestCycleCaseStepDTO> testCycleCaseDTOS, Long projectId);

	/** 查询一个测试用例下所包含的拥有缺陷的步骤
	 * @param cycleCaseId
	 * @return
	 */
	List<TestCycleCaseDefectRelDTO> getSubCycleStepsHaveDefect(Long cycleCaseId);

	Boolean updateIssuesProjectId(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO);
}
