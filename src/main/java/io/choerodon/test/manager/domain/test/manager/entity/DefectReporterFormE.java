package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.agile.api.dto.IssueInfoDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by jialongZuo@hand-china.com on 7/16/18.
 */
public class DefectReporterFormE {

	private String defectName;

	private String defectStatus;

	private Long defectId;

	private String defectColor;

	private String summary;

	List<TestCycleCaseE> testCycleCaseES = new ArrayList<>();

	List<TestCycleCaseStepE> testCycleCaseStepES = new ArrayList<>();

	@Autowired
	TestCycleCaseRepository testCycleCaseRepository;

	@Autowired
	TestCycleCaseStepRepository testCycleCaseStepRepository;

	public DefectReporterFormE(IssueListDTO issueListDTO) {
		this.defectName = issueListDTO.getIssueNum();
		this.defectId = issueListDTO.getIssueId();
		this.defectStatus = issueListDTO.getStatusName();
		this.defectColor = issueListDTO.getStatusColor();
		this.summary = issueListDTO.getSummary();
	}

	public DefectReporterFormE createReporter(TestCaseFeignClient testCaseFeignClient, Long projectId) {
		return populateTestWithDefect().populateIssue(testCaseFeignClient, projectId);
	}

	private DefectReporterFormE populateTestWithDefect() {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = new TestCycleCaseDefectRelE();
		testCycleCaseDefectRelE.setIssueId(this.defectId);
		List<TestCycleCaseDefectRelE> list = testCycleCaseDefectRelE.querySelf();
		Long[] caseIds = list.stream()
				.filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CYCLE_CASE)).map(v -> v.getDefectLinkId()).toArray(Long[]::new);
		if (caseIds.length > 0) {
			testCycleCaseES = testCycleCaseRepository.queryCycleCaseForReporter(caseIds);

		}
		Long[] stepIds = list.stream()
				.filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CASE_STEP)).map(v -> v.getDefectLinkId()).toArray(Long[]::new);
		if (stepIds.length > 0) {
			testCycleCaseStepES = testCycleCaseStepRepository.queryCycleCaseForReporter(stepIds);
		}
		return this;
	}

	private DefectReporterFormE populateIssue(TestCaseFeignClient testCaseFeignClient, Long projectId) {
		List<Long> id = new ArrayList<>();
		id.addAll(testCycleCaseES.stream().map(v -> v.getIssueId()).collect(Collectors.toList()));
		id.addAll(testCycleCaseStepES.stream().map(v -> v.getIssueId()).collect(Collectors.toList()));
		SearchDTO searchDTO = new SearchDTO();
		Map map = new HashMap();
		map.put("issueIds", id);
		searchDTO.setOtherArgs(map);
		ResponseEntity<Page<IssueListDTO>> issueResponse = testCaseFeignClient.listIssueWithoutSub(0, 400, null, projectId, searchDTO);
		Map<Object, IssueListDTO> map1 = new HashMap();
		for (IssueListDTO dto : issueResponse.getBody()) {
			map1.put(dto.getIssueId().longValue(), dto);
		}
		testCycleCaseES.forEach(v -> {
			IssueListDTO d1 = map1.get(v.getIssueId().longValue());
			v.setIssueName(d1.getIssueNum());
			v.setIssueSummary(d1.getSummary());
			v.setIssueStatus(d1.getStatusName());
			v.setIssueColor(d1.getStatusColor());
		});

		testCycleCaseStepES.forEach(v -> {
			IssueListDTO d1 = map1.get(v.getIssueId().longValue());
			v.setIssueName(d1.getIssueNum());
			v.setIssueStatus(d1.getStatusName());
			v.setIssueSummary(d1.getSummary());
			v.setIssueColor(d1.getStatusColor());
		});

		return this;
	}

}
