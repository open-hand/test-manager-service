package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory;
import io.choerodon.test.manager.domain.service.ITestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.infra.feign.FileFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleCaseAttachmentRelServiceImpl implements ITestCycleCaseAttachmentRelService {


    @Autowired
    FileFeignClient fileFeignClient;


    @Override
    public void delete(String bucketName, Long attachId) {
        TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
        attachmentRelE.setId(attachId);

        String url;
        try {
            url = URLDecoder.decode(attachmentRelE.querySelf().get(0).getUrl(), "UTF-8");
        } catch (IOException i) {
            throw new CommonException(i.getMessage());
        }

        ResponseEntity<String> response = fileFeignClient.deleteFile(bucketName, url);
        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new CommonException("error.attachment.upload");
        }
        attachmentRelE.deleteSelf();
    }


    @Override
    public TestCycleCaseAttachmentRelE upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment) {
        TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
        attachmentRelE.setAttachmentLinkId(attachmentLinkId);
        attachmentRelE.setAttachmentName(fileName);
        attachmentRelE.setComment(comment);

        ResponseEntity<String> response = fileFeignClient.uploadFile(bucketName, fileName, file);
        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new CommonException("error.attachment.upload");
        }

        attachmentRelE.setUrl(response.getBody());
        attachmentRelE.setAttachmentType(attachmentType);
        return attachmentRelE.addSelf();
    }

}
