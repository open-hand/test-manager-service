package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.choerodon.test.manager.domain.service.*;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleCaseServiceImpl implements ITestCycleCaseService {
    @Autowired
    ITestCycleCaseStepService iTestCycleCaseStepService;

    @Autowired
    ITestCycleService iTestCycleService;

    @Autowired
    ITestCycleCaseDefectRelService iTestCycleCaseDefectRelService;




    @Override
    public void delete(TestCycleCaseE testCycleCaseE) {
        List<TestCycleCaseE> removeList = testCycleCaseE.querySelf();
        removeList.forEach(v -> deleteCaseWithSubStep(v));
    }

    private void deleteCaseWithSubStep(TestCycleCaseE testCycleCaseE) {
        iTestCycleCaseStepService.deleteByTestCycleCase(testCycleCaseE);
        testCycleCaseE.deleteSelf();
    }


    @Override
    public Page<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, PageRequest pageRequest) {
        return testCycleCaseE.querySelf(pageRequest);
    }

    @Override
    public List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE) {
        return testCycleCaseE.querySelf();
    }

    @Override
    public TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE) {
        testCycleCaseE = testCycleCaseE.queryOne();
        if (testCycleCaseE != null) {
            testCycleCaseE.setTestCycleCaseStepES(iTestCycleCaseStepService.querySubStep(testCycleCaseE));
            testCycleCaseE.setDefects(iTestCycleCaseDefectRelService.query(testCycleCaseE.getExecuteId(), "CYCLE_CASE"));
        }
        return testCycleCaseE;
    }


    /**
     * 启动测试循环
     *
     * @param testCycleCaseE
     * @return
     */
    @Override
    public TestCycleCaseE runTestCycleCase(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseE testCycleCase = testCycleCaseE.createOneCase();
        iTestCycleCaseStepService.createTestCycleCaseStep(testCycleCase);
        return testCycleCase;
    }

    @Override
    public TestCycleCaseE cloneCycleCase(TestCycleCaseE testCycleCaseE) {
        TestCycleCaseE testCycleCase = testCycleCaseE.addSelf();
        iTestCycleCaseStepService.createTestCycleCaseStep(testCycleCase);
        return testCycleCase;
    }


    @Override
    public TestCycleCaseE changeStep(TestCycleCaseE testCycleCaseE) {
        return testCycleCaseE.changeOneCase();
    }

}
