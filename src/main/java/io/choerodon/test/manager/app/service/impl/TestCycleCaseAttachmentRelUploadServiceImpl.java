package io.choerodon.test.manager.app.service.impl;

import org.hzero.boot.file.FileClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
//import io.choerodon.test.manager.app.service.FileService;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelUploadService;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;

/**
 * @author: 25499
 * @date: 2019/12/16 17:28
 * @description:
 */
@Service
public class TestCycleCaseAttachmentRelUploadServiceImpl implements TestCycleCaseAttachmentRelUploadService {
    @Autowired
    private TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

//    @Autowired
//    private FileService fileService;
    @Autowired
    private FileClient fileClient;
    @Override
    public TestCycleCaseAttachmentRelDTO baseUpload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment,Long organizationId) {
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setAttachmentLinkId(attachmentLinkId);
        testCycleCaseAttachmentRelDTO.setAttachmentName(fileName);
        testCycleCaseAttachmentRelDTO.setComment(comment);

        String path= fileClient.uploadFile(organizationId,bucketName, fileName, file);
        testCycleCaseAttachmentRelDTO.setUrl(path);
        testCycleCaseAttachmentRelDTO.setAttachmentType(attachmentType);
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseAttachmentRelMapper::insert, testCycleCaseAttachmentRelDTO, 1, "error.attachment.insert");

        return testCycleCaseAttachmentRelDTO;
    }
}
