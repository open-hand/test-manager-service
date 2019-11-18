package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import io.choerodon.agile.api.vo.IssueInfoDTO;
import io.choerodon.test.manager.app.service.TestCaseLinkService;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.test.manager.infra.mapper.TestCaseLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
@Service
public class TestCaseLinkServiceImpl implements TestCaseLinkService {
    @Autowired
    private TestCaseLinkMapper testCaseLinkMapper;

    @Autowired
    private TestCaseFeignClient testCaseFeignClient;
    @Override
    public void delete(Long project, Long linkId) {

    }

    @Override
    public void create(Long project, TestCaseLinkDTO testCaseLinkDTO) {

    }

    @Override
    public List<IssueInfoDTO> listIssueInfo(Long projectId, Long caseId) {
        TestCaseLinkDTO  testCaseLinkDTO = new TestCaseLinkDTO();
        testCaseLinkDTO.setProjectId(projectId);
        testCaseLinkDTO.setLinkCaseId(caseId);
        List<TestCaseLinkDTO> testCaseLinkDTOS = testCaseLinkMapper.select(testCaseLinkDTO);
        if (CollectionUtils.isEmpty(testCaseLinkDTOS)){
            return new ArrayList<>();
        }
        List<Long> collect = testCaseLinkDTOS.stream().map(TestCaseLinkDTO::getLinkCaseId).collect(Collectors.toList());
        return testCaseFeignClient.listByIssueIds(projectId, collect).getBody();
    }
}
