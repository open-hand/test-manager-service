package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseAttachmentRelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseAttachmentRelServiceImpl implements TestCycleCaseAttachmentRelService {

	@Autowired
    ITestCycleCaseAttachmentRelService iTestCycleCaseAttachmentRelService;


	@Override
	public void delete(String bucketName, Long attachId) {
		iTestCycleCaseAttachmentRelService.delete(bucketName, attachId);
	}

	public TestCycleCaseAttachmentRelE upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment) {
		return iTestCycleCaseAttachmentRelService.upload(bucketName, fileName, file, attachmentLinkId, attachmentType, comment);
	}

}
