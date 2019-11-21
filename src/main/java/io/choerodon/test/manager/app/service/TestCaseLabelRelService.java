package io.choerodon.test.manager.app.service;

import java.util.List;

import com.sun.org.apache.xpath.internal.operations.Bool;

import io.choerodon.agile.api.vo.LabelIssueRelDTO;
import io.choerodon.test.manager.infra.dto.TestCaseLabelRelDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 13:55
 * @description:
 */
public interface TestCaseLabelRelService {

    /**
     * 迁移label_case_rel的数据
     * @param
     * @return
     */
    void fixLabelCaseRel();

    /**
     * 创建用例标签关系
     * @param testCaseLabelRelDTO
     * @return
     */
    Boolean baseCreate(TestCaseLabelRelDTO testCaseLabelRelDTO);

    /**
     * 批量插入
     * @param testCaseLabelRelDTOList
     */
    void batchInsert(List<TestCaseLabelRelDTO> testCaseLabelRelDTOList);

    /**
     * 按测试用例Id 查询用例与标签的关联关系
     * @param caseId
     * @return
     */
    List<TestCaseLabelRelDTO> listLabelByCaseId(Long caseId);

    /**
     * 根据case_id 删除测试用例和标签的关系
     * @param caseId
     */
    void deleteByCaseId(Long caseId);

    /**
     * 复制测试用例关联的标签
     * @param projectId
     * @param caseId
     * @param oldCaseId
     */
    void copyByCaseId(Long projectId, Long caseId, Long oldCaseId);

    /**
     * 改变测试用例关联的标签
     * @param projectId
     * @param caseId
     * @param labelIds
     */
    void change(Long projectId,Long caseId,Long[] labelIds);
}
