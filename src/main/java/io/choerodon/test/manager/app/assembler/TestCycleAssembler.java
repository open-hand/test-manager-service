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
        List<TestCycleDTO> collect = testCycleDTOS.stream().map(v -> {
            if (v.getParentCycleId() == null) {
                v.setParentCycleId(0L);
            }
            return v;
        }).collect(Collectors.toList());
        Map<Long, List<TestCycleDTO>> cycleMap = collect.stream().collect(Collectors.groupingBy(TestCycleDTO::getParentCycleId));
        Map<Long, TestCycleDTO> map = collect.stream().collect(Collectors.toMap(TestCycleDTO::getCycleId, Function.identity()));
        lookUp(map, cycleDTO, testPlanVO);
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
        if (isChange) {
            testCycleService.baseUpdate(compareCycle);
        }
        return isChange;
    }

    private void checkPlanTime(TestCycleDTO cycleDTO, TestPlanVO testPlanDTO) {
        Boolean isChange = true;
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
