package io.choerodon.test.manager.infra.mapper;

import java.math.BigDecimal;
import java.util.List;

import io.choerodon.test.manager.infra.dto.TestPriorityDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper
 *
 * @author jiaxu.cui@hand-china.com 2020-08-19 17:25:29
 */
public interface TestPriorityMapper extends BaseMapper<TestPriorityDTO> {

    List<TestPriorityDTO> fulltextSearch(TestPriorityDTO testPriorityDTO);

    BigDecimal getNextSequence(@Param("organizationId") Long organizationId);

    /**
     * 取消默认优先级
     *
     * @param organizationId 组织id
     */
    void cancelDefaultPriority(@Param("organizationId") Long organizationId);

    void updateMinSeqAsDefault(@Param("organizationId") Long organizationId);

    Long selectDefaultPriority(@Param("organizationId") Long organizationId);
}
