package com.test.devops.app.service;

import com.test.devops.api.dto.TestCycleCaseHistoryDTO;
import com.test.devops.api.dto.TestCycleCaseStepDTO;
import com.test.devops.domain.entity.TestCycleCaseHistoryE;
import com.test.devops.domain.entity.TestCycleCaseStepE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseHistoryService {
	TestCycleCaseHistoryDTO insert(TestCycleCaseHistoryDTO testCycleCaseHistoryDTO);

	void delete(List<TestCycleCaseHistoryDTO> testCycleCaseHistoryDTO);

	List<TestCycleCaseHistoryDTO> update(List<TestCycleCaseHistoryDTO> testCycleCaseHistoryDTO);

	Page<TestCycleCaseHistoryDTO> query(TestCycleCaseHistoryDTO testCycleCaseHistoryDTO, PageRequest pageRequest);
}
