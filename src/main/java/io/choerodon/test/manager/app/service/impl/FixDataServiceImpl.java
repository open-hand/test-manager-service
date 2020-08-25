package io.choerodon.test.manager.app.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.FixDataService;
import io.choerodon.test.manager.app.service.TestPriorityService;
import io.choerodon.test.manager.infra.dto.TestPriorityDTO;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestPriorityMapper;
import io.choerodon.test.manager.infra.util.ConvertUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jiaxu.cui@hand-china.com 2020/8/25 上午10:29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FixDataServiceImpl implements FixDataService {

    @Autowired
    private TestCaseMapper testCaseMapper;
    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;
    @Autowired
    private TestPriorityService testPriorityService;
    @Autowired
    private TestPriorityMapper testPriorityMapper;

    @Override
    public void fixDataTestCase() {
        List<Long> caseProjectIdList = Optional.ofNullable(testCaseMapper.selectALLProjectId()).orElse(Collections.emptyList());
        List<Long> cycleCaseProjectIdList =
                Optional.ofNullable(testCycleCaseMapper.selectALLProjectId()).orElse(Collections.emptyList());
        Set<Long> projectIdSet = new HashSet<>();
        projectIdSet.addAll(caseProjectIdList);
        projectIdSet.addAll(cycleCaseProjectIdList);
        Map<Long, Long> projectMap = projectIdSet.stream().collect(Collectors.toMap(Function.identity(),
                ConvertUtils::getOrganizationId));
        TestPriorityDTO orgExist = new TestPriorityDTO();
        Long defaultPriority;
        for (Map.Entry<Long, Long> entry : projectMap.entrySet()) {
            orgExist.setOrganizationId(entry.getValue());
            List<TestPriorityDTO> priorityDTOList = testPriorityMapper.select(orgExist);
            if (CollectionUtils.isEmpty(priorityDTOList)){
                // 如果不存在则创建默认三条高中低，并返回默认优先级id
                defaultPriority = this.createDefaultPriority(entry.getValue());
            }else {
                defaultPriority = priorityDTOList.stream().filter(TestPriorityDTO::getDefaultFlag)
                        .findFirst().map(TestPriorityDTO::getId)
                        .orElseThrow(() -> new CommonException("error.test-priority.organization-default-priority-error"));
            }
            if (caseProjectIdList.contains(entry.getKey())){
                testCaseMapper.updatePriorityByProject(entry.getKey(), defaultPriority);
            }
            if (cycleCaseProjectIdList.contains(entry.getKey())){
                testCycleCaseMapper.updatePriorityByProject(entry.getKey(), defaultPriority);
            }
        }

    }

    private Long createDefaultPriority(Long organizationId){
        Long defaultPriorityId;
        TestPriorityDTO priority = new TestPriorityDTO();
        priority.setOrganizationId(organizationId);
        priority.setEnableFlag(true);
        priority.setColour("#FFB100");
        priority.setName("高");
        priority.setSequence(BigDecimal.ZERO);
        testPriorityService.create(organizationId, priority);
        priority.setId(null);
        priority.setColour("#3575DF");
        priority.setName("中");
        priority.setSequence(BigDecimal.ONE);
        priority.setDefaultFlag(true);
        testPriorityService.create(organizationId, priority);
        defaultPriorityId = priority.getId();
        priority.setId(null);
        priority.setColour("#3575DF");
        priority.setName("低");
        priority.setSequence(new BigDecimal("2"));
        testPriorityService.create(organizationId, priority);
        return defaultPriorityId;
    }
}
