package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.choerodon.agile.api.dto.IssueInfoDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
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
	TestCaseFeignClient testCaseFeignClient;

	@Autowired
	TestCycleCaseService testCycleCaseService;


	public List<ReporterFormE> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
		ResponseEntity<Page<IssueListDTO>> issueResponse = testCaseFeignClient.listIssueWithoutSub(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString(), projectId, searchDTO);
		List<ReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.getBody().stream().forEach(v -> reporterFormES.add(doCreateFromIssueToDefect(v, projectId)));
		return reporterFormES;
	}

	public List<ReporterFormE> createFromIssueToDefect(Long projectId, Long[] issueIds) {
		SearchDTO searchDTO = new SearchDTO();
		Map map = new HashMap();
		map.put("issueIds", issueIds);
		searchDTO.setOtherArgs(map);
		ResponseEntity<Page<IssueListDTO>> issueResponse = testCaseFeignClient.listIssueWithoutSub(0, 400, null, projectId, searchDTO);
		List<ReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.getBody().stream().forEach(v -> reporterFormES.add(doCreateFromIssueToDefect(v, projectId)));
		return reporterFormES;
	}

	private ReporterFormE doCreateFromIssueToDefect(IssueListDTO issueListDTOS, Long projectId) {
		ReporterFormE reporterFormE = new ReporterFormE();
		return reporterFormE.populateIssue(issueListDTOS)
				.populateLinkedTest(testCaseFeignClient.listIssueLinkByIssueId(issueListDTOS.getProjectId(), issueListDTOS.getIssueId()).getBody(), projectId);

	}


	public List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, Long[] issueIds) {
		SearchDTO searchDTO = new SearchDTO();
		Map map = new HashMap();
		map.put("issueIds", issueIds);
		searchDTO.setOtherArgs(map);
		ResponseEntity<Page<IssueListDTO>> issueResponse = testCaseFeignClient.listIssueWithoutSub(0, 5, null, projectId, searchDTO);
		List<DefectReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.getBody().stream().forEach(v -> reporterFormES.add(doCreateFromDefectToIssue(v, projectId)));

		return reporterFormES;
	}


	public List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, PageRequest pageRequest) {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		Page<TestCycleCaseDefectRelE> defects = testCycleCaseDefectRelE.querySelf(pageRequest);
		if (defects.size() == 0) {
			return new ArrayList<>();
		}

		List<Long> issueLists = defects.stream().map(v -> v.getIssueId()).collect(Collectors.toList());
		SearchDTO searchDTO = new SearchDTO();
		Map map = new HashMap();
		map.put("issueIds", issueLists.toArray());
		searchDTO.setOtherArgs(map);
		ResponseEntity<Page<IssueListDTO>> issueResponse = testCaseFeignClient.listIssueWithoutSub(0, 5, null, projectId, searchDTO);

		List<DefectReporterFormE> reporterFormES = Lists.newArrayList();
		issueResponse.getBody().stream().forEach(v -> reporterFormES.add(doCreateFromDefectToIssue(v, projectId)));

		return reporterFormES;
	}

	private DefectReporterFormE doCreateFromDefectToIssue(IssueListDTO issueListDTOS, Long projectId) {
		DefectReporterFormE reporterFormE = new DefectReporterFormE(issueListDTOS);
		return reporterFormE.createReporter(testCaseFeignClient, projectId);
	}

}
