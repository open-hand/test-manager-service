package io.choerodon.test.manager.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 14:52
 * @description:
 */
public interface TestCaseLabelMapper extends Mapper<TestCaseLabelDTO> {
    void batchInsert(List<TestCaseLabelDTO> testCaseLabelDTOList);

    List<TestCaseLabelDTO> listByIds(@Param("labelIds") List<Long> labelIds);
}
