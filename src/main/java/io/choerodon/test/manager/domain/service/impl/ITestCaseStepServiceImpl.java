package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import io.choerodon.agile.infra.common.utils.RankUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@Service
public class ITestCaseStepServiceImpl implements ITestCaseStepService {

    @Autowired
    ITestCycleCaseStepService testCycleCaseStepService;

	@Autowired
	TestCycleCaseAttachmentRelService attachmentRelService;


    @Override
    public List<TestCaseStepE> query(TestCaseStepE testCaseStepE) {
        return testCaseStepE.querySelf();
    }


	@Override
	public void removeStep(TestCaseStepE testCaseStepE) {
		testCaseStepE.querySelf().forEach(v -> {
			deleteCycleCaseStep(v);
			deleteLinkedAttachment(v.getStepId());
		});
		testCaseStepE.deleteSelf();
	}

	private void deleteLinkedAttachment(Long stepId) {
		TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
		attachmentRelE.setAttachmentLinkId(stepId);
		attachmentRelE.setAttachmentType(TestCycleCaseAttachmentRelE.ATTACHMENT_CASE_STEP);
		attachmentRelE.querySelf().forEach(v -> attachmentRelService.delete(TestCycleCaseAttachmentRelE.ATTACHMENT_BUCKET, v.getId()));
	}

    private void deleteCycleCaseStep(TestCaseStepE testCaseStepE) {
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        testCycleCaseStepE.setStepId(testCaseStepE.getStepId());
        testCycleCaseStepService.deleteStep(testCycleCaseStepE);
    }


}
