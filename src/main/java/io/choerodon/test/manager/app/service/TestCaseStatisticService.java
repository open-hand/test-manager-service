package io.choerodon.test.manager.app.service;

import io.choerodon.test.manager.api.vo.TestCaseDailyStatisticVO;

import java.util.Map;

/**
 * @author superlee
 * @since 2022-05-30
 */
public interface TestCaseStatisticService {

    Map<String, Map<String, Integer>> dailyStatistic(Long projectId, TestCaseDailyStatisticVO testCaseDailyStatisticVO);
}
