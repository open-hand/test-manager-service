package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseService {

    void delete(Long cycleCaseId);

//    List<TestCycleCaseDTO> update(List<TestCycleCaseDTO> testCycleCaseDTO);

	Page<TestCycleCaseDTO> query(TestCycleCaseDTO testCycleCaseDTO, PageRequest pageRequest, Long projectId);

	Page<TestCycleCaseDTO> queryByCycle(Long cycleId, PageRequest pageRequest, Long projectId);

	Page<TestCycleCaseDTO> queryByCycleWithFilterArgs(Long cycleId, PageRequest pageRequest, Long projectId, TestCycleCaseDTO searchDTO);

	TestCycleCaseDTO queryOne(Long cycleCaseId, Long projectId);

	List<TestCycleCaseDTO> queryByIssuse(Long issuseId, Long projectId);

	void populateIssue(List<TestCycleCaseDTO> dots, Long projectId);

    /**
     * 启动一个测试例
     *
     * @param testCycleCaseDTO
     * @return
     */
    TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId);

	List<Long> getActiveCase(Long range, Long projectId, String day);

    /**
	 * 修改一个case
     *
     * @param testCycleCaseDTO
     */
	TestCycleCaseDTO changeOneCase(TestCycleCaseDTO testCycleCaseDTO, Long projectId);


	boolean createFilteredCycleCaseInCycle(Long projectId, Long fromCycleId, Long toCycleId, Long assignee, SearchDTO searchDTO);


	Long countCaseNotRun(Long projectId);

	Long countCaseNotPlain(Long projectId);

	Long countCaseSum(Long projectId);
}
