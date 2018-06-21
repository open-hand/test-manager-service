package com.test.devops.domain.service.impl;

import com.test.devops.domain.entity.TestCaseStepE;
import com.test.devops.domain.entity.TestCycleCaseAttachmentRelE;
import com.test.devops.domain.factory.TestCycleCaseAttachmentRelEFactory;
import com.test.devops.domain.service.ITestCycleCaseAttachmentRelService;
import com.test.devops.infra.feign.FileFeignClient;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
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
