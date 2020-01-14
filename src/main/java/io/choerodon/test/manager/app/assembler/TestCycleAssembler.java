package io.choerodon.test.manager.app.assembler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.TestPlanServcie;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * @author zhaotianxin
 * @since 2020/1/13
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class TestCycleAssembler {
    @Autowired
    private TestCycleService testCycleService;
    @Autowired
    private TestCycleMapper cycleMapper;
    @Autowired
    private TestPlanServcie testPlanServcie;

    public void updatePlanTime(Long projectId,TestPlanVO testPlanVO){
        List<TestCycleDTO> testCycleDTOS = cycleMapper.listByPlanIdAndProjectId(projectId, testPlanVO.getPlanId());
        if (CollectionUtils.isEmpty(testCycleDTOS)) {
            return;
        }
        Map<Long, List<TestCycleDTO>> cycleMap = testCycleDTOS.stream().collect(Collectors.groupingBy(TestCycleDTO::getParentCycleId));
        List<TestCycleDTO> testCycle = cycleMap.get(0L);
        if (CollectionUtils.isEmpty(testCycle)) {
            return;
        }
        testCycle.forEach(v -> planLookDown(cycleMap,testPlanVO,v));
    }
    private void planLookDown(Map<Long, List<TestCycleDTO>> cycleMap,TestPlanVO testPlanVO,TestCycleDTO testCycleDTO){
        Boolean isChange = checkPlanTimeToLookDown(testCycleDTO, testPlanVO);
        if(isChange){
            List<TestCycleDTO> testCycleDTOS = cycleMap.get(testCycleDTO.getCycleId());
            if(!CollectionUtils.isEmpty(testCycleDTOS)){
                testCycleDTOS.forEach(v -> planLookDown(cycleMap,testPlanVO,v));
            }
        }
    }
    public void updateTime(Long projectId, TestCycleDTO cycleDTO) {
        if (cycleDTO.getFromDate() == null && cycleDTO.getToDate() == null) {
            return;
        }
        TestPlanVO testPlanVO = testPlanServcie.queryPlan(projectId, cycleDTO.getPlanId());
        List<TestCycleDTO> testCycleDTOS = cycleMapper.listByPlanIdAndProjectId(projectId, cycleDTO.getPlanId());
        if (CollectionUtils.isEmpty(testCycleDTOS)) {
            checkPlanTime(cycleDTO, testPlanVO);
            return;
        }
        Map<Long, List<TestCycleDTO>> cycleMap = testCycleDTOS.stream().collect(Collectors.groupingBy(TestCycleDTO::getParentCycleId));
        Map<Long, TestCycleDTO> map = testCycleDTOS.stream().collect(Collectors.toMap(TestCycleDTO::getCycleId, Function.identity()));
        // 往父文件夹验证日期
        lookUp(map, cycleDTO, testPlanVO);
        // 往子文件夹验证日期
        lookDown(cycleMap, cycleDTO);
    }

    private void lookDown(Map<Long, List<TestCycleDTO>> cycleMap, TestCycleDTO cycleDTO) {
        List<TestCycleDTO> testCycleDTOS = cycleMap.get(cycleDTO.getCycleId());
        if (!CollectionUtils.isEmpty(testCycleDTOS)) {
            testCycleDTOS.forEach(compareCycle -> {
                Boolean isChange = checkTime(cycleDTO, compareCycle, false);
                if (isChange) {
                    lookDown(cycleMap, compareCycle);
                }
            });
        }

    }

    private void lookUp(Map<Long, TestCycleDTO> map, TestCycleDTO cycleDTO, TestPlanVO testPlanDTO) {
        Long parentCycleId = cycleDTO.getParentCycleId();
        if (parentCycleId == 0L) {
            checkPlanTime(cycleDTO, testPlanDTO);
        } else {
            TestCycleDTO testCycleDTO = map.get(parentCycleId);
            Boolean isChange = checkTime(cycleDTO, testCycleDTO, true);
            if (isChange) {
                lookUp(map, testCycleDTO, testPlanDTO);
            }
        }
    }

    private Boolean checkTime(TestCycleDTO cycleDTO, TestCycleDTO compareCycle, Boolean isUp) {
        Boolean isChange = false;
        if (compareCycle.getFromDate() == null || compareCycle.getToDate() == null) {
            // 更新
            isChange = true;
            compareCycle.setFromDate(cycleDTO.getFromDate());
            compareCycle.setFromDate(cycleDTO.getFromDate());
        }
        else {
            if (isUp) {
                if (cycleDTO.getFromDate().before(compareCycle.getFromDate())) {
                    isChange = true;
                    compareCycle.setFromDate(cycleDTO.getFromDate());
                }
                if (cycleDTO.getToDate().after(compareCycle.getToDate())) {
                    isChange = true;
                    compareCycle.setToDate(cycleDTO.getToDate());
                }
            } else {
                if (cycleDTO.getFromDate().after(compareCycle.getFromDate())) {
                    isChange = true;
                    compareCycle.setFromDate(cycleDTO.getFromDate());
                }
                if (cycleDTO.getToDate().before(compareCycle.getToDate())) {
                    isChange = true;
                    compareCycle.setToDate(cycleDTO.getToDate());
                }
            }
        }
        if (isChange) {
            testCycleService.baseUpdate(compareCycle);
        }
        return isChange;
    }
    private Boolean checkPlanTimeToLookDown(TestCycleDTO cycleDTO, TestPlanVO testPlanDTO){
        Boolean isChange = false;
        if (cycleDTO.getFromDate() == null || cycleDTO.getToDate() == null) {
            // 更新
            isChange = true;
            cycleDTO.setFromDate(testPlanDTO.getStartDate());
            cycleDTO.setToDate(testPlanDTO.getEndDate());
        }
        else {
            if (testPlanDTO.getStartDate().after(cycleDTO.getFromDate())) {
                isChange = true;
                cycleDTO.setFromDate(testPlanDTO.getStartDate());
            }
            if (testPlanDTO.getEndDate().before(cycleDTO.getToDate())) {
                isChange = true;
                cycleDTO.setToDate(testPlanDTO.getEndDate());
            }
        }
        if (isChange) {
            testCycleService.baseUpdate(cycleDTO);
        }
        return isChange;
    }

    private void checkPlanTime(TestCycleDTO cycleDTO, TestPlanVO testPlanDTO) {
        Boolean isChange = false;
        if (cycleDTO.getFromDate().before(testPlanDTO.getStartDate())) {
            isChange = true;
            testPlanDTO.setStartDate(cycleDTO.getFromDate());
        }
        if (cycleDTO.getToDate().after(testPlanDTO.getEndDate())) {
            isChange = true;
            testPlanDTO.setEndDate(cycleDTO.getToDate());
        }
        if (isChange) {
            testPlanServcie.update(testPlanDTO.getProjectId(), testPlanDTO);
        }
    }
}
