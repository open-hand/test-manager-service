package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.impl.TestCycleCaseServiceImpl;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.service.impl.ITestCycleCaseDefectRelServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by jialongZuo@hand-china.com on 7/13/18.
 */
public class ReporterFormE {

	private IssueInfosDTO defectInfo;

	private Long defectCount = new Long(0);

	private List<LinkedTestIssue> linkedTestIssues;


	public ReporterFormE populateIssue(IssueListDTO issueListDTO) {
		defectInfo = new IssueInfosDTO(issueListDTO);
		return this;
	}

	public ReporterFormE populateLinkedTest(List<IssueLinkDTO> linkedTestIssues, Long projectId) {
		List<LinkedTestIssue> list = new ArrayList<>();
		linkedTestIssues.stream().forEach(v -> {
			list.add(new LinkedTestIssue(v.getIssueId(), v.getIssueNum(), v.getSummary(), projectId));
		});
		this.linkedTestIssues = list;
		return this;
	}


	private class LinkedTestIssue {
		private Long issueId;
		private String issueName;
		private String summary;

		private List<TestCycleCaseDTO> testCycleCaseES;


		public LinkedTestIssue(Long issueId, String issueName, String summary, Long projectId) {
			this.issueId = issueId;
			this.issueName = issueName;
			this.summary = summary;
			testCycleCaseES = ApplicationContextHelper.getContext().getBean(TestCycleCaseServiceImpl.class).queryByIssuse(issueId, projectId);
			ITestCycleCaseDefectRelService defectRelService = ApplicationContextHelper.getContext().getBean(ITestCycleCaseDefectRelServiceImpl.class);

			testCycleCaseES.forEach(v -> {
				Optional.ofNullable(defectRelService.getSubCycleStepsHaveDefect(v.getExecuteId()))
						.ifPresent(u -> v.getSubStepDefects().addAll(ConvertHelper.convertList(u, TestCycleCaseDefectRelDTO.class)));
				defectCount += v.getDefects().size() + v.getSubStepDefects().size();
			});
		}

		public List<TestCycleCaseDTO> getTestCycleCaseES() {
			return testCycleCaseES;
		}

		public void setTestCycleCaseES(List<TestCycleCaseDTO> testCycleCaseES) {
			this.testCycleCaseES = testCycleCaseES;
		}


		public Long getIssueId() {
			return issueId;
		}

		public void setIssueId(Long issueId) {
			this.issueId = issueId;
		}

		public String getIssueName() {
			return issueName;
		}

		public void setIssueName(String issueName) {
			this.issueName = issueName;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}
	}

	public IssueInfosDTO getDefectInfo() {
		return defectInfo;
	}

	public ReporterFormE setDefectInfo(IssueInfosDTO defectInfo) {
		this.defectInfo = defectInfo;
		return this;
	}

	public Long getDefectCount() {
		return defectCount;
	}

	public void setDefectCount(Long defectCount) {
		this.defectCount = defectCount;
	}

	public List<LinkedTestIssue> getLinkedTestIssues() {
		return linkedTestIssues;
	}

	public void setLinkedTestIssues(List<LinkedTestIssue> linkedTestIssues) {
		this.linkedTestIssues = linkedTestIssues;
	}
}
