package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;

import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
import io.choerodon.test.manager.api.vo.TestStatusVO;
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
     * @param CycleCaseId
     * @param projectId
     * @return
     */
    PageInfo<TestCycleCaseStepVO> queryCaseStep(Long CycleCaseId, Long projectId, Pageable pageable);




    void batchUpdate(Long executeId, List<TestCaseStepDTO> testCaseStepDTOS );

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
     * 批量删除
     * @param executeStepIds
     */
    void batchDelete(List<Long> executeStepIds);

    /**
     * 批量创建
     */
    void batchCreate(List<TestCycleCaseStepDTO> testCycleCaseStepDTOS);

}
