package io.choerodon.test.manager.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCaseLabelRelDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 14:51
 * @description:
 */
public interface TestCaseLabelRelMapper extends Mapper<TestCaseLabelRelDTO> {
    void batchInsert(@Param("testCaseLabelRelDTOList") List<TestCaseLabelRelDTO> testCaseLabelRelDTOList);

    List<TestCaseLabelRelDTO> listByCaseIds(@Param("caseIds") List<Long> caseIds);
}
