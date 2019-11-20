package io.choerodon.test.manager.infra.mapper;

import java.util.List;

import tk.mybatis.mapper.common.BaseMapper;

import io.choerodon.test.manager.infra.dto.TestCaseLabelRelDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 14:51
 * @description:
 */
public interface TestCaseLabelRelMapper extends BaseMapper<TestCaseLabelRelDTO> {
    void batchInsert(List<TestCaseLabelRelDTO> testCaseLabelRelDTOList);
}
