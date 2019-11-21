package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;

public interface IIssueAttachmentService {

    TestCaseAttachmentDTO createBase(TestCaseAttachmentDTO issueAttachmentDTO);

    Boolean deleteBase(Long attachmentId);
}
