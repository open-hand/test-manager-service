package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.domain.service.*;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.*;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDO;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
	ITestStatusService iTestStatusService;

	@Autowired
	ITestCycleCaseDefectRelService iTestCycleCaseDefectRelService;

	@Autowired
	RedisTemplate redisTemplate;

	@Autowired
	TestCycleCaseRepository testCycleCaseRepository;

	@Autowired
	ProductionVersionClient productionVersionClient;

	@Autowired
	TestCycleCaseAttachmentRelService attachmentRelService;


	@Override
	public void delete(TestCycleCaseE testCycleCaseE, Long projectId) {
		List<TestCycleCaseE> removeList = testCycleCaseE.querySelf();
		removeList.forEach(v -> deleteCaseWithSubStep(v, projectId));
	}

	private void deleteCaseWithSubStep(TestCycleCaseE testCycleCaseE, Long projectId) {
		iTestCycleCaseStepService.deleteByTestCycleCase(testCycleCaseE);
		deleteLinkedAttachment(testCycleCaseE.getExecuteId());
		deleteLinkedDefect(testCycleCaseE.getExecuteId());
		countCaseToRedis(testCycleCaseE, projectId);
		testCycleCaseE.deleteSelf();
	}

	private void countCaseToRedis(TestCycleCaseE testCycleCaseE, Long projectId) {
		if (testCycleCaseE.getExecutionStatus().equals(iTestStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE))) {
			LocalDateTime time = LocalDateTime.ofInstant(testCycleCaseE.getLastUpdateDate().toInstant(), ZoneId.systemDefault());
			RedisAtomicLong entityIdCounter = new RedisAtomicLong("summary:" + projectId + ":" + time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), redisTemplate.getConnectionFactory());
			entityIdCounter.decrementAndGet();
		}
	}


	private void deleteLinkedAttachment(Long executeId) {
		TestCycleCaseAttachmentRelE attachmentRelE = TestCycleCaseAttachmentRelEFactory.create();
		attachmentRelE.setAttachmentLinkId(executeId);
		attachmentRelE.setAttachmentType(TestCycleCaseAttachmentRelE.ATTACHMENT_CYCLE_CASE);
		attachmentRelE.querySelf().forEach(v -> attachmentRelService.delete(TestCycleCaseAttachmentRelE.ATTACHMENT_BUCKET, v.getId()));
	}

	private void deleteLinkedDefect(Long executeId) {
		TestCycleCaseDefectRelE caseDefectRelE = TestCycleCaseDefectRelEFactory.create();
		caseDefectRelE.setDefectLinkId(executeId);
		caseDefectRelE.setDefectType(TestCycleCaseDefectRelE.CYCLE_CASE);
		caseDefectRelE.querySelf().forEach(v -> iTestCycleCaseDefectRelService.delete(v));
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
		TestCycleCaseE testCycleCase = testCycleCaseE.createOneCase();
		iTestCycleCaseStepService.createTestCycleCaseStep(testCycleCase, projectId);
		return testCycleCase;
	}

	@Override
	public TestCycleCaseE cloneCycleCase(TestCycleCaseE testCycleCaseE, Long projectId) {
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
			date.minusDays(i);
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
	public Long countCaseNotRun(Long projectId) {
		Long[] versionIds = productionVersionClient.listByProjectId(projectId).getBody().stream().map(v -> v.getVersionId()).toArray(Long[]::new);
		if (versionIds != null && versionIds.length > 0) {
			List<Long> cycleIds = iTestCycleService.selectCyclesInVersions(versionIds);
			if (cycleIds != null && cycleIds.size() > 0) {
				return testCycleCaseRepository.countCaseNotRun(cycleIds.stream().toArray(Long[]::new));
			}
			return new Long(0);
		} else {
			return new Long(0);
		}
	}

	@Override
	public Long countCaseNotPlain(Long projectId) {
		Long[] versionIds = productionVersionClient.listByProjectId(projectId).getBody().stream().map(v -> v.getVersionId()).toArray(Long[]::new);
		if (versionIds != null && versionIds.length > 0) {
			List<Long> cycleIds = iTestCycleService.selectCyclesInVersions(versionIds);
			return testCycleCaseRepository.countCaseNotPlain(cycleIds.stream().toArray(Long[]::new));
		} else {
			return new Long(0);
		}
	}

	@Override
	public Long countCaseSum(Long projectId) {
		Long[] versionIds = productionVersionClient.listByProjectId(projectId).getBody().stream().map(v -> v.getVersionId()).toArray(Long[]::new);
		if (versionIds != null && versionIds.length > 0) {
			List<Long> cycleIds = iTestCycleService.selectCyclesInVersions(versionIds);
			return testCycleCaseRepository.countCaseSum(cycleIds.stream().toArray(Long[]::new));
		} else {
			return new Long(0);
		}
	}

	@Override
	public void validateCycleCaseInCycle(Long cycleId, Long issueId) {
		TestCycleCaseDO testCycleCase = new TestCycleCaseDO();
		testCycleCase.setCycleId(cycleId);
		testCycleCase.setIssueId(issueId);
		testCycleCaseRepository.validateCycleCaseInCycle(testCycleCase);
	}
}
