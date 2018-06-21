package com.test.devops.domain.service;

import com.test.devops.api.dto.TestCycleDTO;
import com.test.devops.domain.entity.TestCycleE;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface ITestCycleService {
	TestCycleE insert(TestCycleE testCycleE);

	void delete(TestCycleE testCycleE);

	List<TestCycleE> update(List<TestCycleE> testCycleE);

	Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest);

	List<TestCycleE> querySubCycle(TestCycleE testCycleE);

	List<TestCycleE> getTestCycle(Long versionId);
}
