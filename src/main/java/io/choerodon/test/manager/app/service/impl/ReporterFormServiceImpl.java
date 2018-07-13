package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Lists;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.test.manager.entity.ReporterFormE;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

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
//
//	@Override
//	public List<ReporterFormE> createFromIssueToDefect(Long[] issueIds) {
//		return testCaseFeignClient.listIssueWithoutSub(0,400,null,);
//	}

	private ReporterFormE doCreateFromIssueToDefect(IssueListDTO issueListDTOS, Long projectId) {
		ReporterFormE reporterFormE = new ReporterFormE();
		return reporterFormE.populateIssue(issueListDTOS)
				.populateLinkedTest(testCaseFeignClient.listIssueLinkByIssueId(issueListDTOS.getProjectId(), issueListDTOS.getIssueId()).getBody(), projectId);

	}
}
