package io.choerodon.test.manager.app.service;

/**
 * @author zhaotianxin
 * @since 2019/11/21
 */
public interface TestCaseAttachmentService {
    void cloneAttachmentByCaseId(Long projectId,Long caseId,Long oldCaseId);
}
