package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface TestFileLoadHistoryMapper extends BaseMapper<TestFileLoadHistoryDO> {
    List<TestFileLoadHistoryDO> queryDownloadFile(TestFileLoadHistoryDO testFileLoadHistoryDO);

    List<TestFileLoadHistoryDO> queryLatestImportIssueHistory(TestFileLoadHistoryDO testFileLoadHistoryDO);

    Long queryLoadHistoryStatus(@Param("id") Long id);

    int cancelFileUpload(@Param("id") Long id);
}
