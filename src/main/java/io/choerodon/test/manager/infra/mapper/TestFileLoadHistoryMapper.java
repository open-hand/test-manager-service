package io.choerodon.test.manager.infra.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestFileLoadHistoryDTO;

public interface TestFileLoadHistoryMapper extends Mapper<TestFileLoadHistoryDTO> {
    List<TestFileLoadHistoryDTO> queryDownloadFile(TestFileLoadHistoryDTO testFileLoadHistoryDTO);

    List<TestFileLoadHistoryDTO> queryLatestHistory(TestFileLoadHistoryDTO testFileLoadHistoryDTO);

    Long queryLoadHistoryStatus(@Param("id") Long id);

    int cancelFileUpload(@Param("id") Long id);
}
