package io.choerodon.test.manager.app.service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseAttachmentRelService {

    TestCycleCaseAttachmentRelVO upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment);

    void delete(String bucketName, Long attachId);

    void delete(Long linkedId, String Type);

    /**
     * 上传多个附件
     *
     * @param request          request
     * @param bucketName       bucketName
     * @param attachmentLinkId attachmentLinkId
     * @param attachmentType   attachmentType
     * @return TestCycleCaseAttachmentRelVO
     */
    List<TestCycleCaseAttachmentRelVO> uploadMultipartFile(HttpServletRequest request, String bucketName, Long attachmentLinkId, String attachmentType);

    void batchInsert(List<TestCycleCaseDTO> testCycleCaseDTOS, Map<Long, List<TestCaseAttachmentDTO>> attachmentMap);

    void batchDeleteByExecutIds(List<Long> executeIds);

    List<TestCycleCaseAttachmentRelVO> listByExecuteId(Long executeId);

    void snycByCase(TestCycleCaseDTO testCycleCaseDTO, TestCaseDTO testCaseDTO);

    void cloneAttach(Map<Long, Long> caseIdMap, List<Long> olderExecuteId);
}
