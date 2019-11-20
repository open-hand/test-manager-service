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
}
