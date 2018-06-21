package com.test.devops.app.service;

import com.test.devops.api.dto.TestCycleCaseDefectRelDTO;
import com.test.devops.domain.entity.TestCycleCaseDefectRelE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCycleCaseDefectRelService {
	TestCycleCaseDefectRelDTO insert(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO);

	void delete(List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelDTO);

	List<TestCycleCaseDefectRelDTO> update(List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelDTO);

	Page<TestCycleCaseDefectRelDTO> query(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO, PageRequest pageRequest);
}
