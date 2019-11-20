package io.choerodon.test.manager.app.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.test.manager.app.service.TestCaseLabelService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;

/**
 * @author: 25499
 * @date: 2019/11/20 10:53
 * @description:
 */
@Service
public class TestCaseLabelServiceImpl implements TestCaseLabelService {
    @Autowired
    private TestCaseService testCaseService;
    @Override
    public void labelFix() {
        List<TestCaseDTO> testCaseDTOS = testCaseService.queryAllCase();
        Set<Long> projectIds = testCaseDTOS.stream().map(TestCaseDTO::getProjectId).collect(Collectors.toSet());
        projectIds.forEach(projectId->{

        });
    }
}
