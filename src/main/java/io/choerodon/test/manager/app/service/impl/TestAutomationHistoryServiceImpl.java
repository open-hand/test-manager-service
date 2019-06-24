package io.choerodon.test.manager.app.service.impl;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.app.service.TestAutomationHistoryService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.domain.service.ITestAutomationHistoryService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationHistoryE;
import io.choerodon.test.manager.infra.dataobject.TestCycleDO;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TestAutomationHistoryServiceImpl implements TestAutomationHistoryService {

    @Autowired
    ITestAutomationHistoryService iTestAutomationHistoryService;
    @Autowired
    TestCaseService testCaseService;
    @Autowired
    UserService userService;
    @Autowired
    DevopsService devopsService;
    @Autowired
    TestCycleMapper testCycleMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public PageInfo<TestAutomationHistoryDTO> queryWithInstance(Map map, PageRequest pageRequest, Long projectId) {
        map.put("projectId", projectId);
        if (map.containsKey("filter")) {
            List<Long> versionId = devopsService.getAppVersionId(map.get("filter").toString(), projectId, Long.valueOf(map.get("appId").toString()));
            if (versionId.isEmpty()) {
                return new PageInfo<>(new ArrayList<>());
            }
            map.put("appVersionId", versionId);
        }
        PageInfo<TestAutomationHistoryDTO> list = iTestAutomationHistoryService.queryWithInstance(map, pageRequest);
        populateAPPVersion(projectId, list);
        userService.populateTestAutomationHistory(list);
        populateCycles(list);
        return list;
    }

    public void populateAPPVersion(Long projectId, PageInfo<TestAutomationHistoryDTO> page) {
        if (ObjectUtils.isEmpty(page.getList()))
            return;
        Map<Long, ApplicationVersionRepDTO> map =
                devopsService.getAppversion(projectId, page.getList().stream().filter(u -> !ObjectUtils.isEmpty(u.getTestAppInstanceDTO()))
                        .map(v -> v.getTestAppInstanceDTO().getAppVersionId()).distinct().collect(Collectors.toList()));

        page.getList().stream().filter(u -> !ObjectUtils.isEmpty(u.getTestAppInstanceDTO())).forEach(v ->

                v.getTestAppInstanceDTO().setAppVersionName(map.get(v.getTestAppInstanceDTO().getAppVersionId()).getVersion()));
    }

    private void populateCycles(PageInfo<TestAutomationHistoryDTO> page) {
        //填充cycleDTO
        Map<Long, String[]> map = new HashMap<>(page.getSize());
        List<String> cycleStrIds = new ArrayList<>();
        page.getList().forEach(x -> {
            String cycleIdsStr = x.getCycleIds();
            if (cycleIdsStr != null && !cycleIdsStr.equals("")) {
                String[] cycleIds = x.getCycleIds().split(",");
                if (cycleIds.length <= 1) {
                    x.setMoreCycle(false);
                } else {
                    x.setMoreCycle(true);
                    map.put(x.getId(), cycleIds);
                    cycleStrIds.addAll(Arrays.asList(cycleIds));
                }
            }
        });
        if (!cycleStrIds.isEmpty()) {
            List<TestCycleDO> cycleDTOS = testCycleMapper.queryByIds(cycleStrIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList()));
            List<TestCycleDTO> list = modelMapper.map(cycleDTOS, new TypeToken<List<TestCycleDTO>>() {
            }.getType());
            Map<Long, TestCycleDTO> cycleMap = list.stream().collect(Collectors.toMap(TestCycleDTO::getCycleId, x -> x));
            page.getList().forEach(x -> {
                String[] ids = map.get(x.getId());
                List<TestCycleDTO> dtos = new ArrayList<>();
                if (ids != null) {
                    for (String s : ids) {
                        dtos.add(cycleMap.get(Long.valueOf(s)));
                    }
                }
                x.setCycleDTOS(dtos);
            });
        }
    }

    @Override
    public String queryFrameworkByResultId(Long projectId, Long resultId) {
        TestAutomationHistoryE testAutomationHistory = new TestAutomationHistoryE();
        testAutomationHistory.setResultId(resultId);
        testAutomationHistory.setProjectId(projectId);
        List<TestAutomationHistoryE> list = iTestAutomationHistoryService.query(testAutomationHistory);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0).getFramework();
    }
}
