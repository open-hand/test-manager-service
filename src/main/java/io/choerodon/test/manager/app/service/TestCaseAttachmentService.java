package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import io.choerodon.test.manager.api.vo.TestCaseAttachmentCombineVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/21
 */
public interface TestCaseAttachmentService {

    List<TestCaseAttachmentDTO> create(Long projectId, Long caseId, HttpServletRequest request);

    Boolean delete(Long projectId, Long issueAttachmentId);

    List<String> uploadForAddress(Long projectId, HttpServletRequest request);

    /**
     * 生成issueAttachment记录并生成日志（用于复制issue）
     *
     * @param projectId projectId
     * @param issueId   issueId
     * @param fileName  fileName
     * @param url       url
     */
    TestCaseAttachmentDTO dealIssue(Long projectId, Long issueId, String fileName, String url);

    void cloneAttachmentByCaseId(Long projectId,Long caseId,Long oldCaseId);

    void batchInsert(List<TestCaseAttachmentDTO> caseAttachDTOS,List<String> fileNames);

    void asynAttachToCase(List<TestCycleCaseAttachmentRelVO> testCycleCaseAttachmentRelVOS, TestCaseDTO testCaseDTO, Long executeId);

    /**
     * 分片合并
     * @param projectId 项目id
     * @param testCaseAttachmentCombineVO 分片上传附件参数
     * @return 上传的文件记录
     */
    TestCaseAttachmentDTO attachmentCombineUpload(Long projectId, TestCaseAttachmentCombineVO testCaseAttachmentCombineVO);

    /**
     * 校验分片上传合并参数
     * @param testCaseAttachmentCombineVO 分片上传合并参数
     */
    void validCombineUpload(TestCaseAttachmentCombineVO testCaseAttachmentCombineVO);
}
