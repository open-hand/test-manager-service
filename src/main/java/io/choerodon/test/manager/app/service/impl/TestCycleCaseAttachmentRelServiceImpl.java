package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseAttachmentRelServiceImpl implements TestCycleCaseAttachmentRelService {

    @Autowired
    ITestCycleCaseAttachmentRelService iTestCycleCaseAttachmentRelService;

    @Autowired
    TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String bucketName, Long attachId) {
        iTestCycleCaseAttachmentRelService.delete(bucketName, attachId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseAttachmentRelDTO upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment) {
        return ConvertHelper.convert(iTestCycleCaseAttachmentRelService.upload(bucketName, fileName, file, attachmentLinkId, attachmentType, comment), TestCycleCaseAttachmentRelDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long linkedId, String type) {
        Assert.notNull(linkedId, "error.delete.linkedId.not.null");
        Assert.notNull(type, "error.delete.type,not.null");
        TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
        attachmentRelE.setAttachmentLinkId(linkedId);
        attachmentRelE.setAttachmentType(type);
        Optional.ofNullable(attachmentRelE.querySelf()).ifPresent(m ->
                m.forEach(v -> iTestCycleCaseAttachmentRelService
                        .delete(TestCycleCaseAttachmentRelE.ATTACHMENT_BUCKET, v.getId()))
        );
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCycleCaseAttachmentRelDTO> uploadMultipartFile(HttpServletRequest request, String bucketName, Long attachmentLinkId, String attachmentType) {
        List<TestCycleCaseAttachmentRelDTO> testCycleCaseAttachmentRelDTOS = new ArrayList<>();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        files.forEach(v -> testCycleCaseAttachmentRelDTOS.add(testCycleCaseAttachmentRelService.upload(bucketName, v.getOriginalFilename(), v, attachmentLinkId, attachmentType, null)));
        return testCycleCaseAttachmentRelDTOS;
    }
}
