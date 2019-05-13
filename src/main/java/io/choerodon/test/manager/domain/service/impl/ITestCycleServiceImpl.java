package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.BatchCloneCycleDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.app.service.NotifyService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.service.ITestFileLoadHistoryService;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderRelEFactory;
import io.choerodon.test.manager.infra.common.utils.TestDateUtil;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleServiceImpl implements ITestCycleService {

    @Autowired
    ProductionVersionClient productionVersionClient;

    @Autowired
    @Lazy
    TestCycleCaseService testCycleCaseService;

    @Autowired
    @Lazy
    ITestCycleCaseService iTestCycleCaseService;

    @Autowired
    TestCycleRepository testCycleRepository;

    @Autowired
    ITestStatusService iTestStatusService;

    @Autowired
    ITestFileLoadHistoryService iLoadHistoryService;

    @Autowired
    NotifyService notifyService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ITestCycleServiceImpl.class);


    private static final String NOTIFYCYCLECODE = "test-cycle-batch-clone";
    private static final String CYCLE_DATE_NULL_ERROR = "error.clone.cycle.date.not.be.null";

    @Override
    public TestCycleE insert(TestCycleE testCycleE) {
        testCycleE.checkRank();
        testCycleE.setRank(RankUtil.Operation.INSERT.getRank(testCycleRepository.getLastedRank(testCycleE), null));
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
        if (testCycleE.getFolderId() != null) {
            TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
            testCycleCaseE.setCycleId(testCycleE.getCycleId());
            testCycleCaseE.querySelf().forEach(v -> testCycleCaseService.delete(v.getExecuteId(), 0L));
            insertCaseToFolder(testCycleE.getFolderId(), testCycleE.getCycleId());
        }
        return testCycleE.updateSelf();
    }

    @Override
    public List<TestCycleE> queryChildCycle(TestCycleE testCycleE) {
        return testCycleE.queryChildCycle();
    }


    @Override
    public List<TestCycleE> queryCycleWithBar(Long projectId,Long[] versionId, Long assignedTo) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        return countStatus(testCycleE.querySelfWithBar(projectId, versionId, assignedTo));
    }

    @Override
    public List<TestCycleE> queryCycleWithBarOneCycle(Long cycleId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        return countStatus(testCycleE.querySelfWithBarOneCycle(cycleId));
    }

    private List<TestCycleE> countStatus(List<TestCycleE> testCycleES) {
        Map<Long, List<TestCycleE>> parentGroup = testCycleES.stream().filter(x -> x.getParentCycleId() != null && TestCycleE.FOLDER.equals(x.getType())).collect(Collectors.groupingBy(TestCycleE::getParentCycleId));
        testCycleES.parallelStream().filter(v -> StringUtils.equals(v.getType(), TestCycleE.CYCLE))
                .forEach(u -> u.countChildStatus(parentGroup.get(u.getCycleId())));
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
        protoTestCycleE.setProjectId(projectId);
        newTestCycleE.checkRank();
        TestCycleE parentCycleE = TestCycleEFactory.create();
        parentCycleE.setCycleId(newTestCycleE.getParentCycleId());
        if (!protoTestCycleE.getType().equals(TestCycleE.CYCLE)) {
            List<TestCycleE> parentCycleES = parentCycleE.querySelf();

            Date parentFromDate = parentCycleES.get(0).getFromDate();
            Date parentToDate = parentCycleES.get(0).getToDate();

            Date oldFolderFromDate = protoTestCycleE.getFromDate();
            Date oldFolderToDate = protoTestCycleE.getToDate();

            Assert.notNull(parentFromDate, CYCLE_DATE_NULL_ERROR);
            Assert.notNull(parentToDate, CYCLE_DATE_NULL_ERROR);
            Assert.notNull(oldFolderFromDate, CYCLE_DATE_NULL_ERROR);
            Assert.notNull(oldFolderToDate, CYCLE_DATE_NULL_ERROR);

            int differentDaysOldFolder = TestDateUtil.differentDaysByMillisecond(oldFolderFromDate, oldFolderToDate);
            int differentDaysParent = TestDateUtil.differentDaysByMillisecond(parentFromDate, parentToDate);

            protoTestCycleE.setFromDate(parentFromDate);

            if (differentDaysOldFolder > differentDaysParent) {
                protoTestCycleE.setToDate(parentToDate);
            } else {
                protoTestCycleE.setToDate(TestDateUtil.increaseDaysOnDate(parentFromDate, differentDaysOldFolder));
            }
        }
        newTestCycleE.setRank(RankUtil.Operation.INSERT.getRank(testCycleRepository.getLastedRank(newTestCycleE), null));
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
        protoTestCycleE.getChildFolderByRank().forEach(v -> {
            TestCycleE testCycleE = TestCycleEFactory.create();
            testCycleE.setParentCycleId(parentCycle.getCycleId());
            testCycleE.setVersionId(parentCycle.getVersionId());
            testCycleE.setType(TestCycleE.FOLDER);
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

    @Override
    public List<String> queryUpdateRank(TestCycleE testCycleE) {
        return testCycleRepository.queryUpdateRank(testCycleE);
    }

    @Override
    public void insertCaseToFolder(Long issueFolderId, Long cycleId) {
        TestIssueFolderRelE folder = TestIssueFolderRelEFactory.create();
        folder.setFolderId(issueFolderId);
        List<TestIssueFolderRelE> list = folder.queryAllUnderProject();
        TestCycleCaseDTO dto = new TestCycleCaseDTO();
        dto.setCycleId(cycleId);
        list.forEach(v -> {
            dto.setIssueId(v.getIssueId());
            testCycleCaseService.create(dto, v.getProjectId());
        });
    }

    @Override
    public Boolean checkSameNameCycleForBatchClone(Long versionId, List<BatchCloneCycleDTO> list) {
        list.forEach(v -> {
            TestCycleE oldTestCycleE = TestCycleEFactory.create();
            oldTestCycleE.setCycleId(v.getCycleId());
            TestCycleE protoTestCycleE = oldTestCycleE.queryOne();
            protoTestCycleE.setVersionId(versionId);

            testCycleRepository.validateCycle(protoTestCycleE);
        });

        return false;
    }

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void batchCloneCycleAndFolders(Long projectId, Long versionId, List<BatchCloneCycleDTO> list, Long userId) {
        TestFileLoadHistoryE testFileLoadHistoryE = initBatchCloneFileLoadHistory(projectId, versionId);

        int sum = 0;
        int offset = 0;

        for (BatchCloneCycleDTO batchCloneCycleDTO : list) {
            sum = sum + batchCloneCycleDTO.getFolderIds().length;
        }

        try {
            for (BatchCloneCycleDTO batchCloneCycleDTO : list) {
                offset = cloneCycleWithSomeFolder(projectId, versionId, batchCloneCycleDTO,
                        testFileLoadHistoryE, sum, offset, userId);
            }

            testFileLoadHistoryE.setLastUpdateDate(new Date());
            testFileLoadHistoryE.setSuccessfulCount(Integer.toUnsignedLong(sum));
            testFileLoadHistoryE.setStatus(TestFileLoadHistoryE.Status.SUCCESS);
        } catch (Exception e) {
            LOGGER.error(e.toString());

            testFileLoadHistoryE.setLastUpdateDate(new Date());
            testFileLoadHistoryE.setFailedCount(Integer.toUnsignedLong(sum));
            testFileLoadHistoryE.setStatus(TestFileLoadHistoryE.Status.FAILURE);

            notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId),
                    JSON.toJSONString(testFileLoadHistoryE));
            throw new CommonException(CYCLE_DATE_NULL_ERROR);
        }

        iLoadHistoryService.update(testFileLoadHistoryE);
    }

    private int cloneCycleWithSomeFolder(Long projectId, Long versionId, BatchCloneCycleDTO batchCloneCycleDTO,
                                         TestFileLoadHistoryE testFileLoadHistoryE, int sum, int offset, Long userId) {
        TestCycleE oldTestCycleE = TestCycleEFactory.create();
        oldTestCycleE.setCycleId(batchCloneCycleDTO.getCycleId());
        TestCycleE protoTestCycleE = oldTestCycleE.queryOne();

        TestCycleE newTestCycleE = TestCycleEFactory.create();
        newTestCycleE.setCycleName(protoTestCycleE.getCycleName());
        newTestCycleE.setVersionId(versionId);
        newTestCycleE.setType(TestCycleE.CYCLE);

        TestCycleE parentCycle = cloneFolder(protoTestCycleE, newTestCycleE, projectId);

        if (sum == 0) {
            testFileLoadHistoryE.setRate(1.0);
            notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId),
                    JSON.toJSONString(testFileLoadHistoryE));

            return 0;
        }

        for (Long folderId : batchCloneCycleDTO.getFolderIds()) {
            TestCycleE oldFolderTestCycleE = TestCycleEFactory.create();
            oldFolderTestCycleE.setCycleId(folderId);
            TestCycleE protoFolderTestCycleE = oldFolderTestCycleE.queryOne();

            TestCycleE newFolderTestCycleE = TestCycleEFactory.create();
            newFolderTestCycleE.setParentCycleId(parentCycle.getCycleId());
            newFolderTestCycleE.setVersionId(versionId);
            newFolderTestCycleE.setType(TestCycleE.FOLDER);

            cloneFolder(protoFolderTestCycleE, newFolderTestCycleE, projectId);

            offset++;
            testFileLoadHistoryE.setRate(offset * 1.0 / sum);
            notifyService.postWebSocket(NOTIFYCYCLECODE, String.valueOf(userId),
                    JSON.toJSONString(testFileLoadHistoryE));
        }
        return offset;
    }

    private TestFileLoadHistoryE initBatchCloneFileLoadHistory(Long projectId, Long versionId) {
        TestFileLoadHistoryE loadHistoryE = new TestFileLoadHistoryE(projectId,
                TestFileLoadHistoryE.Action.CLONE_CYCLES, TestFileLoadHistoryE.Source.VERSION,
                versionId, TestFileLoadHistoryE.Status.SUSPENDING);
        return iLoadHistoryService.insertOne(loadHistoryE);
    }
}
