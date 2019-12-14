package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestCaseStepVO;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;
import io.choerodon.test.manager.infra.dto.TestCaseStepProDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseStepService {

    List<TestCaseStepVO> query(TestCaseStepVO testCaseStepVO);

    void removeStep(Long projectId,TestCaseStepVO testCaseStepVO);

    TestCaseStepVO changeStep(TestCaseStepVO testCaseStepVO, Long projectId,Boolean changeVersionNum);

    TestCaseStepVO clone(TestCaseStepVO testCaseStepVO, Long projectId);

    List<TestCaseStepVO> batchClone(TestCaseStepVO testCaseStepVO, Long issueId, Long projectId);

    TestCaseStepDTO createOneStep(TestCaseStepProDTO testCaseStepProDTO);

    void removeStepByIssueId(Long projectId,Long caseId);

    List<TestCaseStepDTO> listByCaseIds(List<Long> caseIds);
}
