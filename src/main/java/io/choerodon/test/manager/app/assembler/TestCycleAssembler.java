package io.choerodon.test.manager.app.assembler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import io.choerodon.test.manager.infra.mapper.TestPlanMapper;
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
    private TestCycleMapper cycleMapper;
    @Autowired
    private TestPlanMapper testPlanMapper;

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
        if(Boolean.TRUE.equals(isChange)){
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
        TestPlanDTO testPlanVO = testPlanMapper.selectByPrimaryKey(cycleDTO.getPlanId());
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
                if (Boolean.TRUE.equals(isChange)) {
                    lookDown(cycleMap, compareCycle);
                }
            });
        }
    }

    private void lookUp(Map<Long, TestCycleDTO> map, TestCycleDTO cycleDTO, TestPlanDTO testPlanDTO) {
        Long parentCycleId = cycleDTO.getParentCycleId();
        if (parentCycleId == 0L) {
            checkPlanTime(cycleDTO, testPlanDTO);
        } else {
            TestCycleDTO testCycleDTO = map.get(parentCycleId);
            Boolean isChange = checkTime(cycleDTO, testCycleDTO, true);
            if (Boolean.TRUE.equals(isChange)) {
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
            compareCycle.setToDate(cycleDTO.getToDate());
        }
        else {
            isChange = replaceTime(cycleDTO,compareCycle,isUp);
        }
        if (Boolean.TRUE.equals(isChange)) {
            updateCycle(compareCycle);
        }
        return isChange;
    }

    private Boolean replaceTime(TestCycleDTO cycleDTO, TestCycleDTO compareCycle,Boolean isUp){
        Boolean isChange = false;
        if (Boolean.TRUE.equals(isUp)) {
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
        if (Boolean.TRUE.equals(isChange)) {
            updateCycle(cycleDTO);
        }
        return isChange;
    }

    private void checkPlanTime(TestCycleDTO cycleDTO, TestPlanDTO testPlanDTO) {
        Boolean isChange = false;
        if (cycleDTO.getFromDate().before(testPlanDTO.getStartDate())) {
            isChange = true;
            testPlanDTO.setStartDate(cycleDTO.getFromDate());
        }
        if (cycleDTO.getToDate().after(testPlanDTO.getEndDate())) {
            isChange = true;
            testPlanDTO.setEndDate(cycleDTO.getToDate());
        }
        if (Boolean.TRUE.equals(isChange)) {
            updatePlan(testPlanDTO);
        }
    }

     private void updatePlan(TestPlanDTO testPlanDTO){
         if (testPlanMapper.updateByPrimaryKeySelective(testPlanDTO) != 1) {
             throw new CommonException("error.update.plan");
         }
     }

    private void updateCycle(TestCycleDTO cycleDTO){
        if (cycleMapper.updateByPrimaryKeySelective(cycleDTO) != 1) {
            throw new CommonException("error.update.cycle");
        }
    }

    public void assignmentTime(TestCycleVO testCycleVO, TestCycleDTO testCycleDTO){
        if (testCycleDTO.getFromDate() != null && testCycleDTO.getToDate() != null) {
            testCycleVO.setFromDate(testCycleDTO.getFromDate());
            testCycleVO.setToDate(testCycleDTO.getToDate());
        } else {
            if (testCycleDTO.getParentCycleId() == null || testCycleDTO.getParentCycleId() == 0L) {
                TestPlanDTO testPlanDTO = testPlanMapper.selectByPrimaryKey(testCycleVO.getPlanId());
                testCycleVO.setFromDate(testPlanDTO.getStartDate());
                testCycleVO.setToDate(testPlanDTO.getEndDate());
            } else {
                TestCycleDTO testCycle = cycleMapper.selectByPrimaryKey(testCycleDTO.getParentCycleId());
                assignmentTime(testCycleVO, testCycle);
            }
        }
    }
}
