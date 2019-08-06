package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.github.pagehelper.PageInfo;

import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.app.service.TestAutomationHistoryService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.enums.TestAutomationHistoryEnums;
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import io.choerodon.test.manager.infra.util.PageUtil;

@Component
public class TestAutomationHistoryServiceImpl implements TestAutomationHistoryService {

    @Autowired
    private UserService userService;

    @Autowired
    private DevopsService devopsService;

    @Autowired
    private TestCycleMapper testCycleMapper;

    @Autowired
    private TestAutomationHistoryMapper testAutomationHistoryMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public PageInfo<TestAutomationHistoryVO> queryWithInstance(Map map, PageRequest pageRequest, Long projectId) {
        map.put("projectId", projectId);
        if (map.containsKey("filter")) {
            List<Long> versionId = devopsService.getAppVersionId(map.get("filter").toString(), projectId, Long.valueOf(map.get("appId").toString()));
            if (versionId.isEmpty()) {
                return new PageInfo<>(new ArrayList<>());
            }
            map.put("appVersionId", versionId);
        }
        PageInfo<TestAutomationHistoryDTO> serviceDOPage = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> testAutomationHistoryMapper.queryWithInstance(map));
        PageInfo<TestAutomationHistoryVO> list = modelMapper.map(serviceDOPage, new TypeToken<List<TestAutomationHistoryVO>>() {
        }.getType());
        populateAPPVersion(projectId, list);
        userService.populateTestAutomationHistory(list);
        populateCycles(list);
        return list;
    }

    public void populateAPPVersion(Long projectId, PageInfo<TestAutomationHistoryVO> page) {
        if (ObjectUtils.isEmpty(page.getList()))
            return;
        Map<Long, ApplicationVersionRepDTO> map =
                devopsService.getAppversion(projectId, page.getList().stream().filter(u -> !ObjectUtils.isEmpty(u.getTestAppInstanceVO()))
                        .map(v -> v.getTestAppInstanceVO().getAppVersionId()).distinct().collect(Collectors.toList()));

        page.getList().stream().filter(u -> !ObjectUtils.isEmpty(u.getTestAppInstanceVO())).forEach(v ->

                v.getTestAppInstanceVO().setAppVersionName(map.get(v.getTestAppInstanceVO().getAppVersionId()).getVersion()));
    }

    private void populateCycles(PageInfo<TestAutomationHistoryVO> page) {
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
            List<TestCycleDTO> cycleDTOS = testCycleMapper.queryByIds(cycleStrIds.stream().map(x -> Long.valueOf(x)).collect(Collectors.toList()));
            List<TestCycleVO> list = modelMapper.map(cycleDTOS, new TypeToken<List<TestCycleVO>>() {
            }.getType());
            Map<Long, TestCycleVO> cycleMap = list.stream().collect(Collectors.toMap(TestCycleVO::getCycleId, x -> x));
            page.getList().forEach(x -> {
                String[] ids = map.get(x.getId());
                List<TestCycleVO> dtos = new ArrayList<>();
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
        TestAutomationHistoryDTO testAutomationHistoryDTO = new TestAutomationHistoryDTO();
        testAutomationHistoryDTO.setResultId(resultId);
        testAutomationHistoryDTO.setProjectId(projectId);
        List<TestAutomationHistoryDTO> list = testAutomationHistoryMapper.select(testAutomationHistoryDTO);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0).getFramework();
    }

    @Override
    public void shutdownInstance(Long instanceId, Long status) {
        TestAutomationHistoryDTO testAutomationHistoryDTO = new TestAutomationHistoryDTO();
        testAutomationHistoryDTO.setInstanceId(instanceId);
        testAutomationHistoryDTO.setTestStatus(TestAutomationHistoryEnums.Status.NONEXECUTION);
        testAutomationHistoryDTO.setLastUpdateDate(new Date());
        testAutomationHistoryMapper.shutdownInstance(testAutomationHistoryDTO);
    }
}
