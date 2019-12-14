package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelService {
    TestCycleCaseDefectRelVO insert(TestCycleCaseDefectRelVO testCycleCaseDefectRelVO, Long projectId, Long organizationId);

    void delete(TestCycleCaseDefectRelVO testCycleCaseDefectRelVO, Long projectId, Long organizationId);

    void populateDefectInfo(List<TestCycleCaseDefectRelVO> lists, Long projectId, Long organizationId);

    void populateDefectAndIssue(TestCycleCaseVO dto, Long projectId, Long organizationId);

    void populateCycleCaseDefectInfo(List<TestCycleCaseVO> testCycleCaseVOS, Long projectId, Long organizationId);

    void populateCaseStepDefectInfo(List<TestCycleCaseStepVO> testCycleCaseDTOS, Long projectId, Long organizationId);

    Boolean updateIssuesProjectId(TestCycleCaseDefectRelVO testCycleCaseDefectRelVO, Long organizationId);

    List<TestCycleCaseVO> queryByBug(Long projectId, Long bugId);

    void cloneDefect(Map<Long, Long> caseIdMap, List<Long> olderExecuteId);

    void deleteCaseRel(Long projectId,Long defectId);
}
