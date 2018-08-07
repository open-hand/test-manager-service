package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Lists;
import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.repository.TestCycleCaseDefectRelRepository;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.test.manager.domain.test.manager.entity.DefectReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.ReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jialongZuo@hand-china.com on 7/13/18.
 */

@Component
public class ReporterFormServiceImpl implements ReporterFormService {


	@Autowired
	TestCaseService testCaseService;

	@Autowired
	TestCycleCaseService testCycleCaseService;

	@Autowired
	private TestCycleCaseDefectRelRepository testCycleCaseDefectRelRepository;
	@Autowired
	TestCycleCaseRepository testCycleCaseRepository;

	@Autowired
	TestCycleCaseStepRepository testCycleCaseStepRepository;

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
				.populateLinkedTest(testCaseService.getLinkIssueFromIssueToTest(issueInfosDTO.getProjectId(), Lists.newArrayList(issueInfosDTO.getIssueId())), projectId);

	}


	public List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, Long[] issueIds) {
		Assert.notEmpty(issueIds, "error.query.form.issueId.not.empty");
		PageRequest pageRequest = new PageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(issueIds.length);
		pageRequest.setSort(new Sort(Sort.Direction.ASC, "issueId"));
		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueIds, pageRequest);
		return doCreateFromDefectToIssue(issueResponse.values().stream().collect(Collectors.toList()), projectId);
	}


	public Page<DefectReporterFormE> createFormDefectFromIssue(Long projectId, PageRequest pageRequest) {
		Page page = new Page();
		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMapAndPopulatePageInfo(projectId, new SearchDTO(), pageRequest, page);

		List<DefectReporterFormE> reporterFormES = doCreateFromDefectToIssue(issueResponse.values().stream().collect(Collectors.toList()), projectId);

		page.setContent(reporterFormES);
		page.setNumberOfElements(reporterFormES.size());
		return page;
	}

	private List<DefectReporterFormE> doCreateFromDefectToIssue(List<IssueInfosDTO> issueInfosDTO, Long projectId) {
		List<DefectReporterFormE> formES = Lists.newArrayList();
		List<Long> issues = new ArrayList<>();
		for (IssueInfosDTO infos : issueInfosDTO) {
			DefectReporterFormE form = new DefectReporterFormE(infos);
			formES.add(form);
			issues.add(infos.getIssueId());
		}
		Long[] issueIds = issues.toArray(new Long[issues.size()]);
		List<TestCycleCaseDefectRelE> defectLists = testCycleCaseDefectRelRepository.queryInIssues(issueIds);
		if (defectLists == null || defectLists.isEmpty()) {
			return formES;
		}
		Long[] caseIds = defectLists.stream()
				.filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CYCLE_CASE)).map(TestCycleCaseDefectRelE::getDefectLinkId).toArray(Long[]::new);
		if (caseIds.length > 0) {
			List<TestCycleCaseDTO> cycleCases = ConvertHelper.convertList(testCycleCaseRepository.queryCycleCaseForReporter(caseIds), TestCycleCaseDTO.class);
			formES.stream().forEach(v -> v.populateCycleCase(cycleCases));
		}
		Long[] stepIds = defectLists.stream()
				.filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CASE_STEP)).map(TestCycleCaseDefectRelE::getDefectLinkId).toArray(Long[]::new);
		if (stepIds.length > 0) {
			List<TestCycleCaseStepDTO> cycleCaseSteps = ConvertHelper.convertList(testCycleCaseStepRepository.queryCycleCaseForReporter(stepIds), TestCycleCaseStepDTO.class);
			formES.stream().forEach(v -> v.populateCycleCaseStep(cycleCaseSteps));
		}

		Map<Long, IssueInfosDTO> map = testCaseService.getIssueInfoMap(projectId, issueIds);
		formES.forEach(v -> v.populateIssueInfo(map));

		List<IssueLinkDTO> linkDTOS=testCaseService.getLinkIssueFromTestToIssue(projectId,issues);
		formES.forEach(v -> v.populateIssueLink(projectId, linkDTOS));
		return formES;
	}

}
