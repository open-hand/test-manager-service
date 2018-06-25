package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
@Component
public class TestCycleServiceImpl implements TestCycleService {
	@Autowired
	ITestCycleService iTestCycleService;

	@Autowired
	ProductionVersionClient productionVersionClient;

	@Override
	public TestCycleDTO insert(TestCycleDTO testCycleDTO) {
		return ConvertHelper.convert(iTestCycleService.insert(ConvertHelper.convert(testCycleDTO, TestCycleE.class)), TestCycleDTO.class);

	}

	@Override
	public void delete(TestCycleDTO testCycleDTO) {
		iTestCycleService.delete(ConvertHelper.convert(testCycleDTO, TestCycleE.class));

	}

	@Override
	public List<TestCycleDTO> update(List<TestCycleDTO> testCycleDTO) {
		return ConvertHelper.convertList(iTestCycleService.update(ConvertHelper.convertList(testCycleDTO, TestCycleE.class)), TestCycleDTO.class);

	}

//	@Override
//	public Page<TestCycleDTO> query(TestCycleDTO testCycleDTO, PageRequest pageRequest) {
//		Page<TestCycleE> serviceEPage = iTestCycleService.query(ConvertHelper.convert(testCycleDTO, TestCycleE.class), pageRequest);
//		return ConvertPageHelper.convertPage(serviceEPage, TestCycleDTO.class);
//	}

	@Override
	public List<TestCycleDTO> getTestCycle(Long versionId) {
		return ConvertHelper.convertList(iTestCycleService.getTestCycle(versionId), TestCycleDTO.class);
	}

	@Override
	public ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(Long projectId, Map<String, Object> searchParamMap) {
		return productionVersionClient.listByOptions(projectId, searchParamMap);
	}
}
