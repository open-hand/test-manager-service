package io.choerodon.test.manager.infra.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO;

public interface TestFileLoadHistoryMapper extends Mapper<TestFileLoadHistoryDO> {
    List<TestFileLoadHistoryDO> queryDownloadFile(TestFileLoadHistoryDO testFileLoadHistoryDO);

    List<TestFileLoadHistoryDO> queryLatestHistory(TestFileLoadHistoryDO testFileLoadHistoryDO);

    Long queryLoadHistoryStatus(@Param("id") Long id);

    int cancelFileUpload(@Param("id") Long id);
}
