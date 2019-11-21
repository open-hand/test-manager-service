package io.choerodon.test.manager.app.service.impl;

import java.util.List;
import io.choerodon.test.manager.app.service.TestCaseAttachmentService;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author zhaotianxin
 * @since 2019/11/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseAttachmentServiceImpl implements TestCaseAttachmentService {
    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Override
    public void cloneAttachmentByCaseId(Long projectId, Long caseId, Long oldCaseId) {
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        testCaseAttachmentDTO.setCaseId(oldCaseId);
        testCaseAttachmentDTO.setProjectId(projectId);
        List<TestCaseAttachmentDTO> oldAttachment = testAttachmentMapper.select(testCaseAttachmentDTO);
        if (!CollectionUtils.isEmpty(oldAttachment)) {
            oldAttachment.forEach(v -> {
                v.setCaseId(caseId);
                v.setAttachmentId(null);
                v.setObjectVersionNumber(null);
                testAttachmentMapper.insertSelective(v);
            });
        }
    }
}
