package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 10:51
 * @description:
 */
public interface TestCaseLabelService {
    /**
     * label数据迁移
     */
    void labelFix();

    /**
     * 批量插入
     * @param testCaseLabelDTOList
     */
    void batchInsert(List<TestCaseLabelDTO> testCaseLabelDTOList);

    /**
     * 查询项目下的所有标签
     * @param projectId
     * @return
     */
    List<TestCaseLabelDTO> listByProjectIds(Long projectId);

    /**
     * 批量查询标签
     * @param labelIds
     * @return
     */
    List<TestCaseLabelDTO> listLabelByLabelIds(List<Long> labelIds);

    /**
     * 创建或者修改标签
     * @param projectId
     * @param testCaseLabelDTO
     * @return
     */
    TestCaseLabelDTO createOrUpdate(Long projectId, TestCaseLabelDTO testCaseLabelDTO);

    void baseDelete(Long v);
}
