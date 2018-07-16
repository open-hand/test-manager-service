package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.agile.api.dto.IssueInfoDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		List<Long> issueLists = lists.stream().map(v -> v.getIssueId()).collect(Collectors.toList());

		//List<IssueInfoDTO> list = testCaseFeignClient.listByIssueIds(projectId, issueLists).getBody();
		SearchDTO searchDTO = new SearchDTO();
		Map map = new HashMap();
		map.put("issueIds", issueLists);
		searchDTO.setOtherArgs(map);
		ResponseEntity<Page<IssueListDTO>> issueResponse = testCaseFeignClient.listIssueWithoutSub(0, 400, null, projectId, searchDTO);
		;

		Map defectMap = new HashMap();
		for (IssueListDTO issueInfoDTO : issueResponse.getBody()) {
			defectMap.put(issueInfoDTO.getIssueId().longValue(), issueInfoDTO);
		}
		lists.forEach(v -> {
			v.setDefectName(((IssueListDTO) defectMap.get(v.getIssueId().longValue())).getIssueNum());
			v.setDefectStatus(((IssueListDTO) defectMap.get(v.getIssueId().longValue())).getStatusName());
		});

		return lists;
    }


	public void populateDefects(List<TestCycleCaseE> testCycleCaseES) {

	}
}
