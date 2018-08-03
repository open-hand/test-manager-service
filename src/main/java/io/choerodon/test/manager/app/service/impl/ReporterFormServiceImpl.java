package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Lists;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.test.manager.entity.DefectReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.ReporterFormE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Created by jialongZuo@hand-china.com on 7/13/18.
 */

@Component
public class ReporterFormServiceImpl implements ReporterFormService {


	@Autowired
	TestCaseService testCaseService;

	@Autowired
	TestCycleCaseService testCycleCaseService;


	public Page<ReporterFormE> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		Page page = new Page();
		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMapAndPopulatePageInfo(projectId, searchDTO, pageRequest, page);
		List<ReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.forEach((k, v) -> reporterFormES.add(doCreateFromIssueToDefect(v, projectId)));
		page.setContent(reporterFormES);
		page.setNumberOfElements(reporterFormES.size());
		return page;
	}

	public List<ReporterFormE> createFromIssueToDefect(Long projectId, Long[] issueIds) {
		Assert.notEmpty(issueIds, "error.query.form.issueId.not.empty");

		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueIds);
		List<ReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.forEach((k, v) -> reporterFormES.add(doCreateFromIssueToDefect(v, projectId)));
		return reporterFormES;
	}

	private ReporterFormE doCreateFromIssueToDefect(IssueInfosDTO issueInfosDTO, Long projectId) {
		ReporterFormE reporterFormE = new ReporterFormE();
		return reporterFormE.setDefectInfo(issueInfosDTO)
				.populateLinkedTest(testCaseService.getLinkIssueFromIssueToTest(issueInfosDTO.getProjectId(), issueInfosDTO.getIssueId()), projectId);

	}


	public List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, Long[] issueIds) {
		Assert.notEmpty(issueIds, "error.query.form.issueId.not.empty");
		PageRequest pageRequest = new PageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(issueIds.length);
		pageRequest.setSort(new Sort(Sort.Direction.ASC, "issueId"));
		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueIds, pageRequest);
		List<DefectReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.forEach((k, v) -> reporterFormES.add(doCreateFromDefectToIssue(v, projectId)));

		return reporterFormES;
	}


	public Page<DefectReporterFormE> createFormDefectFromIssue(Long projectId, PageRequest pageRequest) {
		Page page = new Page();
		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMapAndPopulatePageInfo(projectId,new SearchDTO(), pageRequest, page);

		List<DefectReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.forEach((k, v) -> reporterFormES.add(doCreateFromDefectToIssue(v, projectId)));
		page.setContent(reporterFormES);
		page.setNumberOfElements(reporterFormES.size());
		return page;
	}

	private DefectReporterFormE doCreateFromDefectToIssue(IssueInfosDTO issueInfosDTO, Long projectId) {
		DefectReporterFormE reporterFormE = new DefectReporterFormE(issueInfosDTO);
		return reporterFormE.createReporter(projectId);
	}

}
