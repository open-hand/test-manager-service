package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.agile.ProductVersionDTO;
import io.choerodon.test.manager.api.vo.agile.ProductVersionPageDTO;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import org.springframework.http.ResponseEntity;

import io.choerodon.test.manager.api.vo.BatchCloneCycleVO;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleService {
    TestCycleVO insert(Long projectId, TestCycleVO testCycleVO);

    void delete(Long cycleId, Long projectId);

    TestCycleVO update(Long projectId, TestCycleVO testCycleVO);

    void populateVersion(TestCycleVO cycle, Long projectId);

    void populateUsers(List<TestCycleVO> dtos);

    void checkRank(TestCycleVO testCycleVO);

    /**
     * 创建计划时批量创建循环
     *
     * @param testPlanDTO
     * @param testIssueFolderDTOS
     */
    List<TestCycleDTO> batchInsertByFoldersAndPlan(TestPlanDTO testPlanDTO, List<TestIssueFolderDTO> testIssueFolderDTOS);

    List<TestCycleDTO> listByPlanIds(List<Long> planIds,Long projectId);

    void batchDelete(List<Long> needDeleteCycleIds);

    TestIssueFolderVO cycleToIssueFolderVO(TestCycleDTO testCycleDTO);

    void cloneCycleByPlanId(Long copyPlanId, Long newPlanId,Long projectId);

    TestTreeIssueFolderVO queryTreeByPlanId(Long planId,Long projectId);

    String moveCycle(Long projectId, Long targetCycleId,TestCycleVO testCycleVO);

    void baseUpdate(TestCycleDTO testCycleDTO);
}