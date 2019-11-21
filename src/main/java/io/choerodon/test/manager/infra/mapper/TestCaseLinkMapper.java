package io.choerodon.test.manager.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
public interface TestCaseLinkMapper extends Mapper<TestCaseLinkDTO> {
    void batchInsert(@Param("testCaseLinkDTOList") List<TestCaseLinkDTO> testCaseLinkDTOList);
}
