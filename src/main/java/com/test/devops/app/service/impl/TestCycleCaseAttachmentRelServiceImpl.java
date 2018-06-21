package com.test.devops.app.service.impl;

import com.test.devops.api.dto.TestCycleCaseAttachmentRelDTO;
import com.test.devops.app.service.TestCycleCaseAttachmentRelService;
import com.test.devops.domain.entity.TestCycleCaseAttachmentRelE;
import com.test.devops.domain.service.ITestCycleCaseAttachmentRelService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
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
