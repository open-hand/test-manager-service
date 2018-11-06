package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dataobject.TestFileLoadHistoryDO;

import java.util.List;

public interface TestFileLoadHistoryMapper extends BaseMapper<TestFileLoadHistoryDO> {
    List<TestFileLoadHistoryDO> queryDownloadFile(TestFileLoadHistoryDO testFileLoadHistoryDO);
}
