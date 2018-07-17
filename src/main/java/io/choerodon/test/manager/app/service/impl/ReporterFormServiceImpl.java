package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.choerodon.agile.api.dto.IssueInfoDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.test.manager.entity.DefectReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.ReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
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


	public List<ReporterFormE> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMap(projectId, searchDTO, pageRequest);
		List<ReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.forEach((k, v) -> reporterFormES.add(doCreateFromIssueToDefect(v, projectId)));
		return reporterFormES;
	}

	public List<ReporterFormE> createFromIssueToDefect(Long projectId, Long[] issueIds) {

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

		PageRequest pageRequest = new PageRequest();
		pageRequest.setPage(0);
		pageRequest.setSize(5);
		pageRequest.setSort(new Sort(Sort.Direction.ASC, new String[]{"issueId"}));
		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueIds, pageRequest);
		List<DefectReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.forEach((k, v) -> reporterFormES.add(doCreateFromDefectToIssue(v, projectId)));

		return reporterFormES;
	}


	public List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, PageRequest pageRequest) {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		Page<TestCycleCaseDefectRelE> defects = testCycleCaseDefectRelE.querySelf(pageRequest);
		if (defects.size() == 0) {
			return new ArrayList<>();
		}

		List<Long> issueLists = defects.stream().map(v -> v.getIssueId()).collect(Collectors.toList());

		Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueLists.stream().toArray(Long[]::new), pageRequest);

		List<DefectReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.forEach((k, v) -> reporterFormES.add(doCreateFromDefectToIssue(v, projectId)));

		return reporterFormES;
	}

	private DefectReporterFormE doCreateFromDefectToIssue(IssueInfosDTO issueInfosDTO, Long projectId) {
		DefectReporterFormE reporterFormE = new DefectReporterFormE(issueInfosDTO);
		return reporterFormE.createReporter(projectId);
	}

}
