package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.common.utils.TestDateUtil;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleServiceImpl implements ITestCycleService {

    @Autowired
    ProductionVersionClient productionVersionClient;

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Autowired
    ITestCycleCaseService iTestCycleCaseService;

    @Autowired
    TestCycleRepository testCycleRepository;

    @Autowired
    ITestStatusService iTestStatusService;

    @Override
    public TestCycleE insert(TestCycleE testCycleE) {
        return testCycleE.addSelf();
    }

    @Override
    public void delete(TestCycleE testCycleE, Long projectId) {

        List<TestCycleE> testCycleES = testCycleE.querySelf();
        testCycleES.forEach(v -> {
            if (v.getType().equals(TestCycleE.CYCLE)) {
                TestCycleE testCycle = TestCycleEFactory.create();
                testCycle.setParentCycleId(v.getCycleId());
                delete(testCycle, projectId);
            }
            deleteCycleWithCase(v, projectId);
        });
    }

    private void deleteCycleWithCase(TestCycleE testCycleE, Long projectId) {
        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
        testCycleCaseE.setCycleId(testCycleE.getCycleId());
        testCycleCaseE.querySelf().forEach(v -> testCycleCaseService.delete(v.getExecuteId(), projectId));
        testCycleE.deleteSelf();
    }

    @Override
    public TestCycleE update(TestCycleE testCycleE) {
        return testCycleE.updateSelf();
    }

    @Override
    public List<TestCycleE> queryChildCycle(TestCycleE testCycleE) {
        return testCycleE.queryChildCycle();
    }


    @Override
    public List<TestCycleE> queryCycleWithBar(Long[] versionId, Long assignedTo) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        return countStatus(testCycleE.querySelfWithBar(versionId, assignedTo));
    }

    @Override
    public List<TestCycleE> queryCycleWithBarOneCycle(Long cycleId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        return countStatus(testCycleE.querySelfWithBarOneCycle(cycleId));
    }

    private List<TestCycleE> countStatus(List<TestCycleE> testCycleES) {
        testCycleES.stream().filter(v -> StringUtils.equals(v.getType(), TestCycleE.CYCLE))
                .forEach(u -> u.countChildStatus(u.getChildFolder(testCycleES)));
        return testCycleES;
    }


    /**
     * 克隆一个cycle和其拥有的cycleCase
     *
     * @param protoTestCycleE
     * @param newTestCycleE
     * @param projectId
     * @return
     */
    @Override
    public TestCycleE cloneFolder(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId) {
        TestCycleE parentCycleE = TestCycleEFactory.create();
        parentCycleE.setCycleId(newTestCycleE.getParentCycleId());
        List<TestCycleE> parentCycleES = parentCycleE.querySelf();

        Date oldFolderFromDate = protoTestCycleE.getFromDate();
        Date oldFolderToDate = protoTestCycleE.getToDate();
        int differentDaysOldFolder = TestDateUtil.differentDaysByMillisecond(oldFolderFromDate, oldFolderToDate);

        Date parentFromDate = parentCycleES.get(0).getFromDate();
        Date parentToDate = parentCycleES.get(0).getToDate();
        int differentDaysParent = TestDateUtil.differentDaysByMillisecond(parentFromDate, parentToDate);

        protoTestCycleE.setFromDate(parentFromDate);

        if (differentDaysOldFolder > differentDaysParent) {
            protoTestCycleE.setToDate(parentToDate);
        } else {
            protoTestCycleE.setToDate(TestDateUtil.increaseDaysOnDate(parentFromDate, differentDaysOldFolder));
        }

        TestCycleE newCycleE = newTestCycleE.cloneCycle(protoTestCycleE);
        cloneSubCycleCase(protoTestCycleE.getCycleId(), newCycleE.getCycleId(), projectId);

        return newCycleE;
    }

    /**
     * 克隆一个循环和起子文件夹
     *
     * @param protoTestCycleE
     * @param newTestCycleE
     * @param projectId
     * @return
     */
    @Override
    public TestCycleE cloneCycle(TestCycleE protoTestCycleE, TestCycleE newTestCycleE, Long projectId) {
        TestCycleE parentCycle = cloneFolder(protoTestCycleE, newTestCycleE, projectId);
        protoTestCycleE.getChildFolder().forEach(v -> {
            TestCycleE testCycleE = TestCycleEFactory.create();
            testCycleE.setParentCycleId(parentCycle.getCycleId());
            testCycleE.setVersionId(parentCycle.getVersionId());
            cloneFolder(v, testCycleE, projectId);
        });
        return parentCycle;
    }

    /**
     * 克隆循环下的cycleCase
     *
     * @param protoTestCycleId
     * @param newCycleId
     * @param projectId
     */
    private void cloneSubCycleCase(Long protoTestCycleId, Long newCycleId, Long projectId) {
        Assert.notNull(protoTestCycleId, "error.clone.cycle.protoCycleId.not.be.null");
        Assert.notNull(newCycleId, "error.clone.cycle.newCycleId.not.be.null");

        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
        testCycleCaseE.setCycleId(protoTestCycleId);
        Long defaultStatus = iTestStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE);
        final String[] lastRank = new String[1];
        lastRank[0] = testCycleCaseE.getLastedRank(protoTestCycleId);
        //查询出cycle下所有case将其创建到新的cycle下并执行
        iTestCycleCaseService.query(testCycleCaseE).forEach(v ->
                lastRank[0] = iTestCycleCaseService.cloneCycleCase(
                        v.getCloneCase(RankUtil.Operation.INSERT.getRank(lastRank[0], null), newCycleId, defaultStatus)
                        , projectId).getRank()
        );
    }


    @Override
    public List<Long> selectCyclesInVersions(Long[] versionIds) {
        return testCycleRepository.selectCyclesInVersions(versionIds);
    }
}
