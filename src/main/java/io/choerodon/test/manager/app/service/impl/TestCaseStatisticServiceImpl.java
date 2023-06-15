package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.DailyStatisticVO;
import io.choerodon.test.manager.api.vo.TestCaseDailyStatisticVO;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.app.service.TestCaseStatisticService;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.mapper.TestCaseStatisticMapper;
import io.choerodon.test.manager.infra.util.ConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author superlee
 * @since 2022-05-30
 */
@Service
public class TestCaseStatisticServiceImpl implements TestCaseStatisticService {

    @Autowired
    private BaseFeignClient baseFeignClient;
    @Autowired
    private TestCaseStatisticMapper testCaseStatisticMapper;


    private static final List<Long> DEFAULT_PROJECT_IDS =
            Arrays.asList(
                    243470142876237824L,
                    243471092403748864L,
                    243695655897088000L,
                    248104324118196224L,
                    249188481086894080L,
                    261445508798373888L,
                    261899051275677696L,
                    279207558307962880L,
                    281002793740623872L,
                    281826229635350528L,
                    298074647013396480L);


    @Override
    public Map<String, Map<String, Integer>> dailyStatistic(Long projectId,
                                                            TestCaseDailyStatisticVO testCaseDailyStatisticVO) {
        Map<String, Map<String, Integer>> resultMap = new LinkedHashMap<>();
        Long organizationId = ConvertUtils.getOrganizationId(projectId);
        Set<Long> projectIds = testCaseDailyStatisticVO.getProjectIds();
        if (ObjectUtils.isEmpty(projectIds)) {
            projectIds = new HashSet<>(DEFAULT_PROJECT_IDS);
        }
        List<ProjectDTO> projects = baseFeignClient.queryProjects(projectIds).getBody();
        Set<Long> filterProjectIds = new HashSet<>();
        Map<Long, String> projectMap = new HashMap<>();
        projects.forEach(x -> {
            Long thisOrganizationId = x.getOrganizationId();
            if (organizationId.equals(thisOrganizationId)) {
                filterProjectIds.add(x.getId());
                projectMap.put(x.getId(), x.getName());
            }
        });
        List<Long> projectIdList = new ArrayList<>(projectMap.keySet());
        if (projectIdList.isEmpty()) {
            return Collections.emptyMap();
        }
        projectIdList.sort(Long::compareTo);

        Date dailyStartDate;
        Date dailyEndDate;
        if (!ObjectUtils.isEmpty(testCaseDailyStatisticVO.getDailyStartDate())
                && !ObjectUtils.isEmpty(testCaseDailyStatisticVO.getDailyEndDate()) ) {
            dailyStartDate = testCaseDailyStatisticVO.getDailyStartDate();
            dailyEndDate = testCaseDailyStatisticVO.getDailyEndDate();
            if (dailyStartDate.after(dailyEndDate)) {
                throw new CommonException("error.illegal.date.range");
            }
        } else {
            //设置每天统计时间为21：00
            LocalDateTime localDateTime =
                    LocalDateTime.now().withHour(21).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime previousDateTime = localDateTime.minusDays(1);
            dailyStartDate = Date.from(previousDateTime.atZone(ZoneId.systemDefault()).toInstant());
            dailyEndDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        }

        Date startDate = testCaseDailyStatisticVO.getStartDate();
        Date endDate = testCaseDailyStatisticVO.getEndDate();
        if (startDate.after(endDate)) {
            throw new CommonException("error.illegal.date.range");
        }
        //当日新增
        dailyAdded(resultMap, projectMap, projectIdList, dailyStartDate, dailyEndDate, "当日完成用例数", "当日完成步骤数");
        //累计新增
        dailyAdded(resultMap, projectMap, projectIdList, startDate, endDate, "累计测试用例数", "累计用例步骤数");
        //当日执行用例数
        Map<Long, DailyStatisticVO> dailyTestCaseExecMap =
                testCaseStatisticMapper.selectTestCaseExecByDate(projectIdList, dailyStartDate, dailyEndDate)
                        .stream()
                        .collect(Collectors.toMap(DailyStatisticVO::getProjectId, Function.identity()));
        putIntoMap(resultMap, projectMap, projectIdList, "当日用例执行数", null, dailyTestCaseExecMap);
        //当日执行步骤数
        Map<Long, DailyStatisticVO> dailyTestCaseStepExecMap =
                testCaseStatisticMapper.selectTestCaseStepExecByDate(projectIdList, dailyStartDate, dailyEndDate)
                        .stream()
                        .collect(Collectors.toMap(DailyStatisticVO::getProjectId, Function.identity()));
        putIntoMap(resultMap, projectMap, projectIdList, "当日步骤执行数", null, dailyTestCaseStepExecMap);
        //累计用例执行数
        Map<Long, DailyStatisticVO> cumulativeTestCaseExecMap =
                testCaseStatisticMapper.selectTestCaseExecByDate(projectIdList, startDate, endDate)
                        .stream()
                        .collect(Collectors.toMap(DailyStatisticVO::getProjectId, Function.identity()));
        putIntoMap(resultMap, projectMap, projectIdList, "累计用例执行数", null, cumulativeTestCaseExecMap);
        //累计步骤执行数
        Map<Long, DailyStatisticVO> cumulativeTestCaseStepExecMap =
                testCaseStatisticMapper.selectTestCaseStepExecByDate(projectIdList, dailyStartDate, dailyEndDate)
                        .stream()
                        .collect(Collectors.toMap(DailyStatisticVO::getProjectId, Function.identity()));
        putIntoMap(resultMap, projectMap, projectIdList, "累计步骤执行数", null, cumulativeTestCaseStepExecMap);
        return resultMap;
    }

    private void dailyAdded(Map<String, Map<String, Integer>> resultMap,
                            Map<Long, String> projectMap,
                            List<Long> projectIdList,
                            Date dailyStartDate,
                            Date dailyEndDate,
                            String key1,
                            String key2) {
        Map<Long, DailyStatisticVO> dailyTestCaseAddedMap =
                testCaseStatisticMapper.selectTestCaseAddedByDate(projectIdList, dailyStartDate, dailyEndDate)
                        .stream()
                        .collect(Collectors.toMap(DailyStatisticVO::getProjectId, Function.identity()));
        putIntoMap(resultMap, projectMap, projectIdList, key1, key2, dailyTestCaseAddedMap);
    }

    private void putIntoMap(Map<String, Map<String, Integer>> resultMap, Map<Long, String> projectMap, List<Long> projectIdList, String key1, String key2, Map<Long, DailyStatisticVO> dailyTestCaseAddedMap) {
        projectIdList.forEach(thisProjectId -> {
            String projectName = projectMap.get(thisProjectId);
            Map<String, Integer> map =
                    resultMap.computeIfAbsent(projectName, x -> new LinkedHashMap<>());
            DailyStatisticVO vo = dailyTestCaseAddedMap.get(thisProjectId);
            Integer count1 = 0;
            Integer count2 = 0;
            if (!ObjectUtils.isEmpty(vo)) {
                count1 = vo.getTestCaseCount();
                count2 = vo.getTestStepCount();
            }
            if (!ObjectUtils.isEmpty(key1)) {
                map.put(key1, count1);
            }
            if (!ObjectUtils.isEmpty(key2)) {
                map.put(key2, count2);
            }
        });
    }
}
