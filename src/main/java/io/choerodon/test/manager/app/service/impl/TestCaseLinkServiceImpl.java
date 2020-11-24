package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.test.manager.api.vo.TestCaseLinkVO;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.test.manager.api.vo.agile.IssueInfoDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.app.service.TestCaseLinkService;
import io.choerodon.test.manager.infra.dto.TestCaseLinkDTO;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import io.choerodon.test.manager.infra.mapper.TestCaseLinkMapper;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseLinkServiceImpl implements TestCaseLinkService {

    @Autowired
    private TestCaseLinkMapper testCaseLinkMapper;

    @Autowired
    private IssueFeignClient issueFeignClient;

    @Autowired
    private TestCaseFeignClient testCaseFeignClient;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCaseMapper testCaseMapper;


    @Override
    public void delete(Long project, Long linkId) {
        testCaseLinkMapper.deleteByPrimaryKey(linkId);
    }

    @Override
    public TestCaseLinkDTO create(Long project, TestCaseLinkDTO testCaseLinkDTO) {
        if (!ObjectUtils.isEmpty(testCaseLinkDTO.getLinkId())) {
            throw new CommonException("error.insert.link.id.not.null");
        }
        testCaseLinkDTO.setProjectId(project);
        if (!testCaseLinkMapper.select(testCaseLinkDTO).isEmpty()) {
            return new TestCaseLinkDTO();
        }
        baseInsert(testCaseLinkDTO);
        return testCaseLinkDTO;
    }

    @Override
    public List<IssueInfoDTO> listIssueInfo(Long projectId, Long caseId) {
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        testCaseLinkDTO.setProjectId(projectId);
        testCaseLinkDTO.setLinkCaseId(caseId);
        List<TestCaseLinkDTO> testCaseLinkDTOS = testCaseLinkMapper.select(testCaseLinkDTO);
        if (CollectionUtils.isEmpty(testCaseLinkDTOS)) {
            return new ArrayList<>();
        }
        List<Long> collect = testCaseLinkDTOS.stream().map(TestCaseLinkDTO::getLinkCaseId).collect(Collectors.toList());
        return testCaseFeignClient.listByIssueIds(projectId, collect).getBody();
    }

    @Override
    public void copyByCaseId(Long projectId, Long caseId, Long oldCaseId) {
        // 查询原关联的问题链接信息
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        testCaseLinkDTO.setLinkCaseId(oldCaseId);
        testCaseLinkDTO.setProjectId(projectId);
        List<TestCaseLinkDTO> list = testCaseLinkMapper.select(testCaseLinkDTO);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        // 替换新的测试用例ID，并插入数据库
        for (TestCaseLinkDTO testCaseLink : list) {
            testCaseLink.setLinkId(null);
            testCaseLink.setObjectVersionNumber(null);
            testCaseLink.setLinkCaseId(caseId);
            testCaseLinkMapper.insert(testCaseLink);
        }
    }

    @Override
    public List<IssueLinkVO> queryLinkIssues(Long projectId, Long caseId) {
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        testCaseLinkDTO.setLinkCaseId(caseId);
        testCaseLinkDTO.setProjectId(projectId);
        List<TestCaseLinkDTO> caseLinkList = testCaseLinkMapper.select(testCaseLinkDTO);
        if (CollectionUtils.isEmpty(caseLinkList)) {
            return new ArrayList<>();
        }
        List<Long> issueIds = caseLinkList.stream().map(TestCaseLinkDTO::getIssueId).collect(Collectors.toList());
        List<IssueLinkVO> issueInfos = issueFeignClient.queryIssues(projectId, issueIds).getBody();
        Map<Long, List<IssueLinkVO>> collect = issueInfos.stream().collect(Collectors.groupingBy(IssueLinkVO::getIssueId));
        List<IssueLinkVO> result = new ArrayList<>();
        caseLinkList.forEach(v -> {
            if(ObjectUtils.isEmpty(collect.get(v.getIssueId()))){
              return;
             }
            IssueLinkVO issueLinkVO = collect.get(v.getIssueId()).get(0);
            modelMapper.map(v,issueLinkVO);
            result.add(issueLinkVO);
        });

        return result;
    }

    @Override
    public List<TestCaseLinkDTO> create(Long projectId, Long caseId, List<Long> issueIds) {
        if (CollectionUtils.isEmpty(issueIds)) {
            return new ArrayList<>();
        }
        List<TestCaseLinkDTO> list = new ArrayList<>();
        issueIds.forEach(v -> {
            TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
            testCaseLinkDTO.setProjectId(projectId);
            testCaseLinkDTO.setLinkCaseId(caseId);
            testCaseLinkDTO.setIssueId(v);
            list.add(create(projectId, testCaseLinkDTO));
        });
        return list;
    }

    @Override
    public void batchInsert(List<TestCaseLinkDTO> testCaseLinkDTOList) {
        testCaseLinkMapper.batchInsert(testCaseLinkDTOList);
    }

    private void baseInsert(TestCaseLinkDTO testCaseLinkDTO) {
        if (testCaseLinkMapper.insertSelective(testCaseLinkDTO) != 1) {
            throw new CommonException("error.insert.case.link");
        }
    }

    @Override
    public List<TestCaseLinkDTO> createAndLink(Long projectId, Long issueId, TestCaseVO testCaseVO) {
        Long caseId = testCaseService.createTestCase(projectId, testCaseVO, null).getCaseId();
        return create(projectId, caseId, Collections.singletonList(issueId));
    }

    @Override
    public void createByIssue(Long projectId, Long issueId, List<Long> caseIds) {
        if (CollectionUtils.isEmpty(caseIds)) {
            return;
        }
        caseIds.forEach(v -> {
            TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
            testCaseLinkDTO.setProjectId(projectId);
            testCaseLinkDTO.setLinkCaseId(v);
            testCaseLinkDTO.setIssueId(issueId);
            create(projectId, testCaseLinkDTO);
        });
    }

    @Override
    public List<TestCaseLinkVO> queryLinkCases(Long projectId, Long issueId) {
        TestCaseLinkDTO testCaseLinkDTO = new TestCaseLinkDTO();
        testCaseLinkDTO.setIssueId(issueId);
        testCaseLinkDTO.setProjectId(projectId);
        List<TestCaseLinkDTO> caseLinkList = testCaseLinkMapper.select(testCaseLinkDTO);
        if (CollectionUtils.isEmpty(caseLinkList)) {
            return new ArrayList<>();
        }
        List<Long> linkCaseIds = caseLinkList.stream().map(TestCaseLinkDTO::getLinkCaseId).collect(Collectors.toList());

        List<TestCaseLinkVO> testCases = testCaseMapper.listByLinkCaseIds(projectId, linkCaseIds);
        Map<Long, TestCaseLinkVO> collect = testCases.stream().collect(Collectors.toMap(TestCaseLinkVO::getCaseId, Function.identity()));
        List<TestCaseLinkVO> result = new ArrayList<>();
        caseLinkList.forEach(v -> {
            if(ObjectUtils.isEmpty(collect.get(v.getLinkCaseId()))){
                return;
            }
            TestCaseLinkVO testCaseLinkVO = collect.get(v.getLinkCaseId());
            modelMapper.map(v,testCaseLinkVO);
            result.add(testCaseLinkVO);
        });
        return result;
    }
}
