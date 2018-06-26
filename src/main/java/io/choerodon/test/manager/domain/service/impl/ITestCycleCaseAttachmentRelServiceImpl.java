package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory;
import io.choerodon.test.manager.domain.service.ITestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.infra.feign.FileFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleCaseAttachmentRelServiceImpl implements ITestCycleCaseAttachmentRelService {


    @Autowired
    FileFeignClient fileFeignClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String bucketName, Long attachId) {
        TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
        attachmentRelE.setId(attachId);
        fileFeignClient.deleteFile(bucketName, attachmentRelE.querySelf().get(0).getUrl());
        attachmentRelE.deleteSelf();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseAttachmentRelE upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment) {
        TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
        attachmentRelE.setAttachmentLinkId(attachmentLinkId);
        attachmentRelE.setAttachmentName(fileName);
        attachmentRelE.setComment(comment);
        attachmentRelE.setUrl(fileFeignClient.uploadFile(bucketName, fileName, file).getBody());
        attachmentRelE.setAttachmentType(attachmentType);
        return attachmentRelE.addSelf();
    }
}
