package com.test.devops.domain.service;

import com.test.devops.domain.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface ITestCycleCaseAttachmentRelService {

	void delete(String bucketName, Long attachId);

	TestCycleCaseAttachmentRelE upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment);
}
