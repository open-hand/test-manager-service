package io.choerodon.test.manager.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.devops.api.vo.AppServiceVersionRespVO;
import io.choerodon.test.manager.api.vo.TestAppInstanceVO;
import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
import io.choerodon.test.manager.api.vo.TestCycleVO;
import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.app.service.TestAutomationHistoryService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.enums.TestAutomationHistoryEnums;
import io.choerodon.test.manager.infra.mapper.TestAutomationHistoryMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import io.choerodon.test.manager.infra.util.PageUtil;
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
    private UserService userService;

    @Autowired
    private DevopsService devopsService;

    @Autowired
    private TestCycleMapper testCycleMapper;

    @Autowired
    private TestAutomationHistoryMapper testAutomationHistoryMapper;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PageInfo<TestAutomationHistoryVO> queryWithInstance(Map map, Pageable pageable, Long projectId) {
        map.put("projectId", projectId);
        if (map.containsKey("filter")) {
            List<Long> versionId = devopsService.getAppVersionId(map.get("filter").toString(), projectId, Long.valueOf(map.get("appId").toString()));
            if (versionId.isEmpty()) {
                return new PageInfo<>(new ArrayList<>());
            }
            map.put("appVersionId", versionId);
        }
        PageInfo<TestAutomationHistoryDTO> serviceDOPage = PageHelper.startPage(pageable.getPageNumber(),
                pageable.getPageSize(), PageUtil.sortToSql(pageable.getSort())).doSelectPageInfo(() -> testAutomationHistoryMapper.queryWithInstance(map));
        List<TestAutomationHistoryDTO> dtos = serviceDOPage.getList();
        Map<Long, TestAutomationHistoryDTO> dtoMap = dtos.stream().collect(Collectors.toMap(TestAutomationHistoryDTO::getId, x -> x));
        List<TestAutomationHistoryVO> vos = modelMapper.map(serviceDOPage.getList(), new TypeToken<List<TestAutomationHistoryVO>>() {
        }.getType());
        for (TestAutomationHistoryVO vo : vos) {
            if (dtoMap.get(vo.getId()).getTestAppInstanceDTO() != null) {
                vo.setTestAppInstanceVO(modelMapper.map(dtoMap.get(vo.getId()).getTestAppInstanceDTO(), TestAppInstanceVO.class));
            }
        }
        PageInfo<TestAutomationHistoryVO> list = PageUtil.buildPageInfoWithPageInfoList(serviceDOPage, vos);
        populateAPPVersion(projectId, list);
        userService.populateTestAutomationHistory(list);
        populateCycles(list);
        return list;
    }

    public void populateAPPVersion(Long projectId, PageInfo<TestAutomationHistoryVO> page) {
        if (ObjectUtils.isEmpty(page.getList()))
            return;
        Map<Long, AppServiceVersionRespVO> map =
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
