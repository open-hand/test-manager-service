package io.choerodon.test.manager.app.service;

import java.util.List;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/21
 */
public interface TestCaseAttachmentService {
    void cloneAttachmentByCaseId(Long projectId,Long caseId,Long oldCaseId);

    List<TestCaseAttachmentDTO> query(Long projectId, Long caseId);

    void baseInsert(TestCaseAttachmentDTO testCaseAttachmentDTO);

    void baseDelete(TestCaseAttachmentDTO testCaseAttachmentDTO);
}
