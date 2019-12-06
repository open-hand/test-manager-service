package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import org.springframework.http.ResponseEntity;

import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.agile.api.vo.ProductVersionPageDTO;
import io.choerodon.test.manager.api.vo.BatchCloneCycleVO;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleService {
    TestCycleVO insert(Long projectId, TestCycleVO testCycleVO);

    TestCycleVO insertWithoutSyncFolder(Long projectId, TestCycleVO testCycleVO);

    boolean synchroFolder(Long cycleId, Long folderId, Long projectId);

    boolean synchroFolderInCycle(Long cycleId, Long projectId);

    boolean synchroFolderInVersion(Long versionId, Long projectId);

    void delete(Long cycleId, Long projectId);

    TestCycleVO update(Long projectId, TestCycleVO testCycleVO);

    TestCycleVO cloneCycle(Long cycleId, Long versionId, String cycleName, Long projectId);

    TestCycleVO cloneFolder(Long cycleId, TestCycleVO testCycleVO, Long projectId);

    JSONObject getTestCycle(Long versionId, Long assignedTo);

    JSONArray getTestCycleCaseCountInVersion(Long versionId, Long projectId, Long cycleId);

    TestCycleVO getOneCycle(Long cycleId);

    ResponseEntity<PageInfo<ProductVersionPageDTO>> getTestCycleVersion(Long projectId, Map<String, Object> searchParamMap);

    List<TestCycleVO> getFolderByCycleId(Long cycleId);

    void populateVersion(TestCycleVO cycle, Long projectId);

    void populateUsers(List<TestCycleVO> dtos);

    void initVersionTree(Long projectId, JSONArray versionStatus, List<ProductVersionDTO> versionDTOList, List<TestCycleVO> cycleDTOList);

    List<TestCycleVO> getCyclesInVersion(Long versionId);

    void batchChangeAssignedInOneCycle(Long projectId, Long userId, Long cycleId);

    void batchCloneCycles(Long projectId, Long versionId, List<BatchCloneCycleVO> list);

    JSONObject getTestCycleInVersionForBatchClone(Long versionId, Long projectId);

    TestFileLoadHistoryVO queryLatestBatchCloneHistory(Long projectId);

    void checkRank(TestCycleVO testCycleVO);

    Boolean checkName(Long projectId, String type, String cycleName, Long versionId, Long parentCycleId);

    /**
     * 创建计划时批量创建循环
     * @param testPlanDTO
     * @param testIssueFolderDTOS
     */
    List<TestCycleDTO>  batchInsertByFoldersAndPlan(TestPlanDTO testPlanDTO, List<TestIssueFolderDTO> testIssueFolderDTOS);

    List<TestCycleDTO> listByPlanIds(List<Long> planIds);

    void batchDelete(List<Long> needDeleteCycleIds);

    TestIssueFolderVO cycleToIssueFolderVO(TestCycleDTO testCycleDTO);

    void syncByCaseFolder(Long folderId, Long cycleId);

    void moveCycle(Long projectId, Long targetCycleId,Long cycleId,String lastRank,String nextRank);
}