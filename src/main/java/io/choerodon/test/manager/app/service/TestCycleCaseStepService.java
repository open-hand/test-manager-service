package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseStepService {

    /**
     * 更新循环步骤
     *
     * @param testCycleCaseStepVO
     * @return
     */
    List<TestCycleCaseStepVO> update(List<TestCycleCaseStepVO> testCycleCaseStepVO);

    /**
     * 查询循环测试步骤
     *
     * @param CycleCaseId    CycleCaseId
     * @param projectId      projectId
     * @param organizationId organizationId
     * @return TestCycleCaseStepVO
     */
    List<TestCycleCaseStepVO> querySubStep(Long CycleCaseId, Long projectId, Long organizationId);
}
