package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.app.service.TestApiTaskService;
import io.choerodon.test.manager.infra.dto.TestApiAssertionDTO;
import io.choerodon.test.manager.infra.dto.TestApiCaseDTO;
import io.choerodon.test.manager.infra.mapper.TestApiAssertionMapper;
import io.choerodon.test.manager.infra.mapper.TestApiCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestApiTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TestApiTaskServiceImpl implements TestApiTaskService {

    @Autowired
    private TestApiTaskMapper testApiTaskMapper;

    @Autowired
    private TestApiAssertionMapper testApiAssertionMapper;

    @Autowired
    private TestApiCaseMapper testApiCaseMapper;

    @Override
    public void executeTask(Long projectId, Long taskId) {

    }

    public void generateJmx(Long projectId, Long taskId) {
        // 根据taskId查处所有case
        List<TestApiCaseDTO> testApiCaseDTOS = testApiCaseMapper.queryByTaskId(taskId);

        List<Long> caseIds = testApiCaseDTOS.stream().map(TestApiCaseDTO::getId).collect(Collectors.toList());

        // 根据case查处所有的assertion
        List<TestApiAssertionDTO> testApiAssertionDTOS = testApiAssertionMapper.listByCaseIds(caseIds);

        // 根据case查处所有的config


        // 根据case拼接完整的请求对象
        // 构建jmx对象
        // 生成jmx文件
    }
}
