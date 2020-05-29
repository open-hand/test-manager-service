package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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
     * @param cycleCaseId    cycleCaseId
     * @param projectId      projectId
     * @param organizationId organizationId
     * @return TestCycleCaseStepVO
     */
    Page<TestCycleCaseStepVO> querySubStep(Long cycleCaseId, Long projectId, Long organizationId,PageRequest pageRequest);

    /**
     * 查询用例下的步骤
     *
     * @param cycleCaseId
     * @param projectId
     * @return
     */
    Page<TestCycleCaseStepVO> queryCaseStep(Long cycleCaseId, Long projectId, PageRequest pageRequest);


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
