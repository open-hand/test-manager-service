package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.IIssueAttachmentService;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IIssueAttachmentServiceImpl implements IIssueAttachmentService {

    private static final String INSERT_ERROR = "error.IssueAttachment.create";

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Override
    @DataLog(type = DataLogConstants.CREATE_ATTACHMENT)
    public TestCaseAttachmentDTO createBase(TestCaseAttachmentDTO issueAttachmentDTO) {
        if (testAttachmentMapper.insert(issueAttachmentDTO) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return testAttachmentMapper.selectByPrimaryKey(issueAttachmentDTO.getAttachmentId());
    }

    @Override
    @DataLog(type = DataLogConstants.CREATE_ATTACHMENT)
    public Boolean deleteBase(Long attachmentId) {
        TestCaseAttachmentDTO issueAttachmentDTO = testAttachmentMapper.selectByPrimaryKey(attachmentId);
        if (issueAttachmentDTO == null) {
            throw new CommonException("error.attachment.get");
        }
        if (testAttachmentMapper.delete(issueAttachmentDTO) != 1) {
            throw new CommonException("error.attachment.delete");
        }
        return true;
    }

}
