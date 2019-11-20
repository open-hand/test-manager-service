package io.choerodon.test.manager.infra.mapper;

import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import org.apache.ibatis.annotations.Param;

public interface TestAttachmentMapper extends Mapper<TestCaseAttachmentDTO> {

    void insertTestCaseAttachment(@Param("testCaseAttachment") TestCaseAttachmentDTO testCaseAttachmentDTO);

}
