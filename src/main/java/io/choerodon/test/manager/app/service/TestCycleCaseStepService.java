package io.choerodon.test.manager.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseStepService {

    /**
     * 更新循环步骤
     *
     * @param testCycleCaseStepDTO
     * @return
     */
    List<TestCycleCaseStepDTO> update(List<TestCycleCaseStepDTO> testCycleCaseStepDTO);

    /**
     * 查询循环测试步骤
     *
     * @param CycleCaseId    CycleCaseId
     * @param projectId      projectId
     * @param organizationId organizationId
     * @return TestCycleCaseStepDTO
     */
    List<TestCycleCaseStepDTO> querySubStep(Long CycleCaseId, Long projectId, Long organizationId);


}
