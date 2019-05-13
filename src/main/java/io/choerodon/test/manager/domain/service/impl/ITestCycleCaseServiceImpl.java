package io.choerodon.test.manager.domain.service.impl;


import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.domain.service.*;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
    ITestStatusService iTestStatusService;

    @Autowired
    ITestCycleCaseDefectRelService iTestCycleCaseDefectRelService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    TestCycleCaseRepository testCycleCaseRepository;

    @Autowired
    TestCaseService productionVersionClient;

    @Autowired
    TestCycleCaseAttachmentRelService attachmentRelService;


    @Override
    public void delete(TestCycleCaseE testCycleCaseE) {
        Optional.ofNullable(testCycleCaseE.querySelf()).ifPresent(m ->
                m.forEach(this::deleteCaseWithSubStep));
    }

    private void deleteCaseWithSubStep(TestCycleCaseE testCycleCaseE) {
        iTestCycleCaseStepService.deleteByTestCycleCase(testCycleCaseE);
        attachmentRelService.delete(testCycleCaseE.getExecuteId(), TestCycleCaseAttachmentRelE.ATTACHMENT_CYCLE_CASE);
        deleteLinkedDefect(testCycleCaseE.getExecuteId());
        testCycleCaseE.deleteSelf();
    }


    private void deleteLinkedDefect(Long executeId) {
        TestCycleCaseDefectRelE caseDefectRelE = TestCycleCaseDefectRelEFactory.create();
        caseDefectRelE.setDefectLinkId(executeId);
        caseDefectRelE.setDefectType(TestCycleCaseDefectRelE.CYCLE_CASE);
        caseDefectRelE.querySelf().forEach(v -> iTestCycleCaseDefectRelService.delete(v));
    }


    @Override
    public Page<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, PageRequest pageRequest) {
        return testCycleCaseRepository.query(testCycleCaseE, pageRequest);
    }

    @Override
    public Page<TestCycleCaseE> queryByFatherCycle(List<TestCycleCaseE> testCycleCaseES, PageRequest pageRequest) {
        return testCycleCaseRepository.queryByFatherCycle(testCycleCaseES, pageRequest);
    }

    @Override
    public List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE) {
        return testCycleCaseE.querySelf();
    }

    @Override
    public TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE) {
        return testCycleCaseE.queryOne();
    }


    /**
     * 启动测试循环
     *
     * @param testCycleCaseE
     * @return
     */
    @Override
    public TestCycleCaseE runTestCycleCase(TestCycleCaseE testCycleCaseE, Long projectId) {
        testCycleCaseE.setProjectId(projectId);
        TestCycleCaseE testCycleCase = testCycleCaseE.createOneCase();
        iTestCycleCaseStepService.createTestCycleCaseStep(testCycleCase, projectId);
        return testCycleCase;
    }

    @Override
    public TestCycleCaseE cloneCycleCase(TestCycleCaseE testCycleCaseE, Long projectId) {
        testCycleCaseE.setProjectId(projectId);
        TestCycleCaseE testCycleCase = testCycleCaseE.addSelf();
        iTestCycleCaseStepService.createTestCycleCaseStep(testCycleCase, projectId);
        return testCycleCase;
    }


    @Override
    public TestCycleCaseE changeStep(TestCycleCaseE testCycleCaseE) {
        return testCycleCaseE.changeOneCase();
    }

    @Override
    public List<Long> getActiveCase(Long range, Long projectId, String day) {
        List<Long> caseCountList = new ArrayList<>();
        LocalDate date = LocalDate.parse(day);
        for (int i = range.intValue() - 1; i >= 0; i--) {
            caseCountList.add(new RedisAtomicLong("summary:" + projectId + ":" + date.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    , redisTemplate.getConnectionFactory()).get());
        }
        return caseCountList;
    }

    @Override
    public List<TestCycleCaseE> queryByIssue(Long versionId) {
        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
        return testCycleCaseE.queryByIssue(versionId);
    }

    @Override
    public List<TestCycleCaseE> queryInIssues(Long[] issuesIds) {
        return testCycleCaseRepository.queryInIssue(issuesIds);
    }


    @Override
    public List<TestCycleCaseE> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds) {
        return testCycleCaseRepository.queryCaseAllInfoInCyclesOrVersions(cycleIds, versionIds);
    }


    /**
     * 获取项目下所有cycleId
     *
     * @param projectId
     * @return
     */
    private List<Long> getCycleIdByProjectId(Long projectId) {
        Long[] versionIds = productionVersionClient.getVersionIds(projectId);
        if (ObjectUtils.isEmpty(versionIds)) {
            return new ArrayList<>();
        }
        return iTestCycleService.selectCyclesInVersions(versionIds);
    }

    @Override
    public Long countCaseNotRun(Long projectId) {
        List<Long> cycleIds = getCycleIdByProjectId(projectId);
        return ObjectUtils.isEmpty(cycleIds) ? 0L : testCycleCaseRepository.countCaseNotRun(cycleIds.stream().toArray(Long[]::new));
    }

    @Override
    public Long countCaseNotPlain(Long projectId) {
        List<Long> cycleIds = getCycleIdByProjectId(projectId);
        return ObjectUtils.isEmpty(cycleIds) ? 0L : testCycleCaseRepository.countCaseNotPlain(cycleIds.stream().toArray(Long[]::new));
    }

    @Override
    public Long countCaseSum(Long projectId) {
        List<Long> cycleIds = getCycleIdByProjectId(projectId);
        return ObjectUtils.isEmpty(cycleIds) ? 0L : testCycleCaseRepository.countCaseSum(cycleIds.stream().toArray(Long[]::new));
    }

}
