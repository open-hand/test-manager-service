package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Maps;
import io.choerodon.agile.api.dto.IssueInfoDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleCaseDefectRelServiceImpl implements ITestCycleCaseDefectRelService {

    @Autowired
    TestCaseFeignClient testCaseFeignClient;


    @Override
    public TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        return testCycleCaseDefectRelE.addSelf();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        testCycleCaseDefectRelE.deleteSelf();
    }


    @Override
    public List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        return testCycleCaseDefectRelE.querySelf();
    }

    @Override
    public List<TestCycleCaseDefectRelE> query(Long linkId, String defectType, Long projectId) {
		TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		testCycleCaseDefectRelE.setDefectLinkId(linkId);
		testCycleCaseDefectRelE.setDefectType(defectType);
		List<TestCycleCaseDefectRelE> lists = testCycleCaseDefectRelE.querySelf();
		if (lists.size() == 0) {
			return null;
		}
		populateDefectInfo(lists, projectId);
//		List<Long> issueLists = lists.stream().map(v -> v.getIssueId()).collect(Collectors.toList());
//
//		//List<IssueInfoDTO> list = testCaseFeignClient.listByIssueIds(projectId, issueLists).getBody();
//		SearchDTO searchDTO = new SearchDTO();
//		Map map = new HashMap();
//		map.put("issueIds", issueLists);
//		searchDTO.setOtherArgs(map);
//		ResponseEntity<Page<IssueListDTO>> issueResponse = testCaseFeignClient.listIssueWithoutSub(0, 400, null, projectId, searchDTO);
//
//		Map defectMap = new HashMap();
//		for (IssueListDTO issueInfoDTO : issueResponse.getBody()) {
//			defectMap.put(issueInfoDTO.getIssueId().longValue(), issueInfoDTO);
//		}
//		lists.forEach(v -> {
//			v.setDefectName(((IssueListDTO) defectMap.get(v.getIssueId().longValue())).getIssueNum());
//			v.setDefectStatus(((IssueListDTO) defectMap.get(v.getIssueId().longValue())).getStatusName());
//			v.setDefectColor(((IssueListDTO) defectMap.get(v.getIssueId().longValue())).getStatusColor());
//		});

		return lists;
    }

	public void populateDefectInfo(List<TestCycleCaseDefectRelE> lists, Long projectId) {
		List<Long> issueLists = lists.stream().map(v -> v.getIssueId()).collect(Collectors.toList());
		Assert.notEmpty(issueLists, "error.defect.getInfo.issueId.not.null");
		SearchDTO searchDTO = new SearchDTO();
		searchDTO.setOtherArgs(new HashMap() {{
			put("issueIds", issueLists);
		}});
		ResponseEntity<Page<IssueListDTO>> issueResponse = testCaseFeignClient.listIssueWithoutSub(0, 400, null, projectId, searchDTO);
		Map defectMap = new HashMap();
		for (IssueListDTO issueInfoDTO : issueResponse.getBody()) {
			defectMap.put(issueInfoDTO.getIssueId().longValue(), issueInfoDTO);
		}

		lists.forEach(v -> {
			v.setDefectName(((IssueListDTO) defectMap.get(v.getIssueId().longValue())).getIssueNum());
			v.setDefectStatus(((IssueListDTO) defectMap.get(v.getIssueId().longValue())).getStatusName());
			v.setDefectColor(((IssueListDTO) defectMap.get(v.getIssueId().longValue())).getStatusColor());
		});
	}


	public List<TestCycleCaseDefectRelE> getSubCycleStepsHaveDefect(Long cycleCaseId) {
		TestCycleCaseStepE caseStepE = TestCycleCaseStepEFactory.create();
		caseStepE.setExecuteId(cycleCaseId);
		List<TestCycleCaseStepE> caseStepES = caseStepE.querySelf();
		List<TestCycleCaseDefectRelE> defectRelES = new ArrayList<>();
		caseStepES.stream().forEach(v -> {
			Optional.ofNullable(cycleStepHaveDefect(v.getExecuteStepId())).ifPresent(u -> defectRelES.addAll(u));
		});
		return defectRelES;
	}

	private List<TestCycleCaseDefectRelE> cycleStepHaveDefect(Long cycleStepId) {
		TestCycleCaseDefectRelE caseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		caseDefectRelE.setDefectLinkId(cycleStepId);
		caseDefectRelE.setDefectType(TestCycleCaseDefectRelE.CASE_STEP);
		return caseDefectRelE.querySelf();

	}
}
