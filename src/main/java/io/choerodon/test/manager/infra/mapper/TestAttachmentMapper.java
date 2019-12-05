package io.choerodon.test.manager.infra.mapper;

import java.util.List;
import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import org.apache.ibatis.annotations.Param;

public interface TestAttachmentMapper extends Mapper<TestCaseAttachmentDTO> {

    void insertTestCaseAttachment(TestCaseAttachmentDTO testCaseAttachmentDTO);

    List<TestCaseAttachmentDTO> listByCaseIds(@Param("caseIds") List<Long> caseIds);

    void deleteByCaseId(@Param("caseId") Long caseId);

    void batchInsert(@Param("list") List<TestCaseAttachmentDTO> testCaseAttachmentDTOS);
}
