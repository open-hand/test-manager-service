package io.choerodon.test.manager.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCaseStepDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import org.springframework.web.multipart.MultipartFile;

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
     * @param testCycleCaseDTO
     * @return
     */
    Page<TestCycleCaseStepDTO> querySubStep(Long CycleCaseId, PageRequest pageRequest, Long projectId);

    /**
     * 启动循环测试下所有步骤
     *
     * @param testCycleCaseDTO
     */
	void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO, Long projectId);

    /**
     * 删除CycleCase下所有Step
     *
     * @param testCycleCaseDTO
     */
    void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO);

    TestCycleCaseStepDTO updateOneCase(List<MultipartFile> files, TestCycleCaseStepDTO testCycleCaseStepDTO, List<TestCycleCaseDefectRelDTO> defects);
}
