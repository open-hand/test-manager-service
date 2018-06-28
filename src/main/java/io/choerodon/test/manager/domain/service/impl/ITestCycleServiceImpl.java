package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleServiceImpl implements ITestCycleService {

    @Autowired
    ProductionVersionClient productionVersionClient;

    @Autowired
    ITestCycleCaseService iTestCycleCaseService;


	private final String TEMP_CYCLE_NAME = "临时";
	private final String TEMP_CYCLE_TYPE = "CYCLE";

    @Override
    public TestCycleE insert(TestCycleE testCycleE) {
        return testCycleE.addSelf();
    }

    @Override
    public void delete(TestCycleE testCycleE) {
//        TestCycleE cycle = TestCycleEFactory.create();
//        cycle.setVersionId(testCycleE.getVersionId());
//        cycle.setParentCycleId(testCycleE.getCycleId());
		testCycleE.querySelf().forEach(this::deleteCycleWithCase);
    }

    private void deleteCycleWithCase(TestCycleE testCycleE) {
        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
        testCycleCaseE.setCycleId(testCycleE.getCycleId());
        iTestCycleCaseService.delete(testCycleCaseE);
        testCycleE.deleteSelf();
    }

    @Override
    public List<TestCycleE> update(List<TestCycleE> testCycleE) {
        List<TestCycleE> testCycleES = new ArrayList<>();
        testCycleE.forEach(v -> testCycleES.add(v.updateSelf()));
        return testCycleES;
    }

    @Override
    public Page<TestCycleE> query(TestCycleE testCycleE, PageRequest pageRequest) {
        return testCycleE.querySelf(pageRequest);
    }

    @Override
    public List<TestCycleE> querySubCycle(TestCycleE testCycleE) {
        return testCycleE.querySelf();
    }


//    @Override
//    public List<TestCycleE> getTestCycle(Long versionId) {
//        TestCycleE testCycleE = TestCycleEFactory.create();
//        testCycleE.setVersionId(versionId);
//        return querySubCycle(testCycleE);
//    }

    @Override
    public List<TestCycleE> queryCycleWithBar(Long versionId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setVersionId(versionId);
		return countStatus(testCycleE.querySelfWithBar());
    }

	private List<TestCycleE> countStatus(List<TestCycleE> testCycleES) {
		testCycleES.stream().filter(v -> StringUtils.equals(v.getType(), TestCycleE.CYCLE))
				.forEach(u -> u.countChildStatus(u.getChildFolder(testCycleES)));
		return testCycleES;
	}


	@Override
	public Long findDefaultCycle(Long projectId) {
		ResponseEntity<Page<ProductVersionPageDTO>> rs = productionVersionClient.listByOptions(projectId, Maps.asMap(Sets.newHashSet("statusCode"), v -> "version_planning"));
		List<ProductVersionPageDTO> lists = rs.getBody().getContent();
		switch (lists.size()) {
			case 1:
				TestCycleE cycle = TestCycleEFactory.create();
				cycle.setVersionId(lists.get(0).getVersionId());
				cycle.setCycleName(TEMP_CYCLE_NAME);
				return cycle.querySelf().stream().findFirst()
						.orElseThrow(() -> new CommonException("error.folder.version_planning.notFound")).getCycleId();
			default:
				throw new CommonException("error.folder.version_planning.notFound");
		}
	}

	public TestCycleE cloneFolder(TestCycleE protoTestCycleE, TestCycleE newTestCycleE) {
		TestCycleE newCycleE = newTestCycleE.cloneCycle(protoTestCycleE);

		TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
		testCycleCaseE.setCycleId(protoTestCycleE.getCycleId());
		iTestCycleCaseService.query(testCycleCaseE).forEach(v -> {
			v.setExecuteId(null);
			v.setCycleId(newCycleE.getCycleId());
			v.setObjectVersionNumber(null);
			iTestCycleCaseService.cloneCycleCase(v);
		});
		return newCycleE;
	}

	@Override
	public TestCycleE cloneCycle(TestCycleE protoTestCycleE, TestCycleE newTestCycleE) {
		TestCycleE parentCycle = cloneFolder(protoTestCycleE, newTestCycleE);
		protoTestCycleE.getChildFolder().forEach(v -> {
			TestCycleE testCycleE = TestCycleEFactory.create();
			testCycleE.setParentCycleId(parentCycle.getCycleId());
			cloneFolder(v, testCycleE);
		});
		return parentCycle;
	}


}
