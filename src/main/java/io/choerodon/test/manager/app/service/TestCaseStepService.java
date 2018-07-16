package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCaseStepDTO;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseStepService {

    List<TestCaseStepDTO> query(TestCaseStepDTO testCaseStepDTO);

    void removeStep(TestCaseStepDTO testCaseStepDTO);

    List<TestCaseStepDTO> batchInsertStep(List<TestCaseStepDTO> testCaseStepDTO, Long projectId);

    TestCaseStepDTO changeStep(TestCaseStepDTO testCaseStepDTO, Long projectId);

    TestCaseStepDTO clone(TestCaseStepDTO testCaseStepDTO, Long projectId);

}
