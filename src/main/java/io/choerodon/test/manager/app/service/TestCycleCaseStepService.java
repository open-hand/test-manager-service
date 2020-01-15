package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import org.springframework.data.domain.Pageable;

import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseStepDTO;

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
    void update(TestCycleCaseStepVO testCycleCaseStepVO);

    /**
     * 查询循环测试步骤
     *
     * @param CycleCaseId    CycleCaseId
     * @param projectId      projectId
     * @param organizationId organizationId
     * @return TestCycleCaseStepVO
     */
    List<TestCycleCaseStepVO> querySubStep(Long CycleCaseId, Long projectId, Long organizationId);

    /**
     * 查询用例下的步骤
     *
     * @param CycleCaseId
     * @param projectId
     * @return
     */
    PageInfo<TestCycleCaseStepVO> queryCaseStep(Long CycleCaseId, Long projectId, Pageable pageable);


    void baseUpdate(TestCycleCaseStepDTO testCycleCaseStepDTO);

    void delete(Long executeStepId);

    void create(TestCycleCaseStepVO testCycleCaseStepVO);

    /**
     * 将测试用例步骤 转为执行步骤
     *
     * @param testCycleCaseDTOList
     * @param caseStepMap
     */
    void batchInsert(List<TestCycleCaseDTO> testCycleCaseDTOList, Map<Long, List<TestCaseStepDTO>> caseStepMap);

    /**
     * 用例库测试步骤更新到测试执行
     *
     * @param testCycleCaseDTO
     * @param testCaseDTO
     */
    void snycByCase(TestCycleCaseDTO testCycleCaseDTO, TestCaseDTO testCaseDTO);

    void cloneStep(Map<Long, Long> caseIdMap, List<Long> olderExecuteIds);
}
