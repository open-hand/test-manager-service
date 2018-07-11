package io.choerodon.test.manager.app.service.impl;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseHistoryService;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseHistoryServiceImpl implements TestCycleCaseHistoryService {
    @Autowired
    ITestCycleCaseHistoryService iTestCycleCaseHistoryService;

	@Autowired
	UserService userFeignClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseHistoryDTO insert(TestCycleCaseHistoryDTO testCycleCaseHistoryDTO) {
        return ConvertHelper.convert(iTestCycleCaseHistoryService.insert(ConvertHelper.convert(testCycleCaseHistoryDTO, TestCycleCaseHistoryE.class)), TestCycleCaseHistoryDTO.class);

    }

//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void delete(List<TestCycleCaseHistoryDTO> testCycleCaseHistoryDTO) {
//        iTestCycleCaseHistoryService.delete(ConvertHelper.convertList(testCycleCaseHistoryDTO, TestCycleCaseHistoryE.class));
//    }
//
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public List<TestCycleCaseHistoryDTO> update(List<TestCycleCaseHistoryDTO> testCycleCaseHistoryDTO) {
//        return ConvertHelper.convertList(iTestCycleCaseHistoryService.update(ConvertHelper.convertList(testCycleCaseHistoryDTO, TestCycleCaseHistoryE.class)), TestCycleCaseHistoryDTO.class);
//    }

    @Override
    public Page<TestCycleCaseHistoryDTO> query(Long cycleCaseId, PageRequest pageRequest) {
        TestCycleCaseHistoryDTO historyDTO = new TestCycleCaseHistoryDTO();
        historyDTO.setExecuteId(cycleCaseId);
        Page<TestCycleCaseHistoryE> serviceEPage = iTestCycleCaseHistoryService.query(ConvertHelper.convert(historyDTO, TestCycleCaseHistoryE.class), pageRequest);
		Page<TestCycleCaseHistoryDTO> dto = ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseHistoryDTO.class);
		Long[] users = dto.stream().map(v -> v.getLastUpdatedBy()).toArray(Long[]::new);
		Map user = userFeignClient.query(users);
		setUser(dto, user);
//		dto.forEach(v -> setUser(v));
		return dto;
	}


	private void setUser(List<TestCycleCaseHistoryDTO> dto, Map<Long, UserDO> users) {
		dto.forEach(v -> {
			if (v.getLastUpdatedBy() != null && v.getLastUpdatedBy().longValue() != 0) {
				v.setUser(users.get(v.getLastUpdatedBy()));
			}
		});
    }
}
