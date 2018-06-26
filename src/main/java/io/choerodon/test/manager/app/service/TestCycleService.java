package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleService {
	TestCycleDTO insert(TestCycleDTO testCycleDTO);

	void delete(TestCycleDTO testCycleDTO);

	List<TestCycleDTO> update(List<TestCycleDTO> testCycleDTO);

//	Page<TestCycleDTO> query(TestCycleDTO testCycleDTO, PageRequest pageRequest);

	List<TestCycleDTO> getTestCycle(Long versionId);

	ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(Long projectId, Map<String, Object> searchParamMap);
}
