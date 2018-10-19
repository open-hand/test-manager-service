package io.choerodon.test.manager.app.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleService {
	TestCycleDTO insert(TestCycleDTO testCycleDTO);

	boolean synchroFolder(Long cycleId,Long folderId,Long projectId);

	boolean synchroFolderInCycle(Long cycleId,Long projectId);

	boolean synchroFolderInVersion(Long versionId,Long projectId);

	void delete(TestCycleDTO testCycleDTO, Long projectId);

	TestCycleDTO update(TestCycleDTO testCycleDTO);

	TestCycleDTO cloneCycle(Long cycleId, Long versionId, String cycleName, Long projectId);

	TestCycleDTO cloneFolder(Long cycleId, TestCycleDTO testCycleDTO, Long projectId);

	JSONObject getTestCycle(Long versionId,Long assignedTo);

	JSONArray getTestCycleCaseCountInVersion(Long versionId,Long projectId);

	TestCycleDTO getOneCycle(Long cycleId);

	ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(Long projectId, Map<String, Object> searchParamMap);

	List<TestCycleDTO> getFolderByCycleId(Long cycleId);

	void populateVersion(TestCycleDTO cycle, Long projectId);

	void populateUsers(List<TestCycleDTO> dtos);

	void initVersionTree(JSONArray versionStatus, List<ProductVersionDTO> versionDTOList, List<TestCycleDTO> cycleDTOList);

	void fixCycleData(Long projectId);
}
