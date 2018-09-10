package io.choerodon.test.manager.app.service.impl;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.feign.ProjectFeignClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */

@Component
public class TestCaseServiceImpl implements TestCaseService {


    @Autowired
    TestCaseFeignClient testCaseFeignClient;

    @Autowired
    ProductionVersionClient productionVersionClient;

    @Autowired
    ProjectFeignClient projectFeignClient;

    @Override
    public ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithoutSub.param.projectId.not.null");
        Assert.notNull(pageRequest, "error.TestCaseService.listIssueWithoutSub.param.pageRequest.not.null");
        return testCaseFeignClient.listIssueWithoutSubToTestComponent(projectId, searchDTO, pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString());
    }

    @Override
    public ResponseEntity<Page<IssueComponentDetailDTO>> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithoutSubDetail.param.projectId.not.null");
        Assert.notNull(pageRequest, "error.TestCaseService.listIssueWithoutSubDetail.param.pageRequest.not.null");
        return testCaseFeignClient.listIssueWithoutSubDetail(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString(), projectId, searchDTO);
    }

    @Override
    public ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId) {
        Assert.notNull(projectId, "error.TestCaseService.queryIssue.param.projectId.not.null");
        Assert.notNull(issueId, "error.TestCaseService.queryIssue.param.issueId.not.null");
        return testCaseFeignClient.queryIssue(projectId, issueId);
    }

    @Override
    public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest) {
        return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueCommonDTO::getIssueId, IssueInfosDTO::new));
    }

    /**
     * 获取issue信息并且更新分页信息
     *
     * @param projectId
     * @param searchDTO
     * @param pageRequest
     * @return
     */
    public <T> Map<Long, IssueInfosDTO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Page<T> page) {
        Assert.notNull(page, "error.TestCaseService.getIssueInfoMapAndPopulatePageInfo.param.page.not.be.null");
        Page<IssueCommonDTO> returnDto = listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody();

        page.setTotalElements(returnDto.getTotalElements());
        page.setSize(returnDto.getSize());
        page.setNumber(returnDto.getNumber());
        page.setTotalPages((int) (returnDto.getTotalElements() - 1L) / returnDto.getSize() + 1);

        return returnDto.stream().collect(Collectors.toMap(IssueCommonDTO::getIssueId, IssueInfosDTO::new));

    }

    @Override
    public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(999999999);
        pageRequest.setPage(0);
        pageRequest.setSort(new Sort(Sort.Direction.ASC, "issueId"));
        if (needDetail) {
            return listIssueWithoutSubDetail(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueComponentDetailDTO::getIssueId, IssueInfosDTO::new));
        } else {
            return listIssueWithoutSub(projectId, searchDTO, pageRequest).getBody().stream().collect(Collectors.toMap(IssueCommonDTO::getIssueId, IssueInfosDTO::new));
        }
    }

    @Override
    public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail) {
        if (ObjectUtils.isEmpty(issueIds)) {
            return new HashMap<>();
        }
        return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), needDetail);
    }

    @Override
    public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, PageRequest pageRequest) {
        if (ObjectUtils.isEmpty(issueIds)) {
            return new HashMap<>();
        }
        return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), pageRequest);
    }

    private SearchDTO buildIdsSearchDTO(Long[] issueIds) {
        SearchDTO searchDTO = new SearchDTO();
        Map map = new HashMap();
        map.put("issueIds", issueIds);
        searchDTO.setOtherArgs(map);
        return searchDTO;
    }


    @Override
    public List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, List<Long> issueId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueLinkByIssueId.param.projectId.not.null");
        if (ObjectUtils.isEmpty(issueId)) {
            return new ArrayList<>();
        }
        return testCaseFeignClient.listIssueLinkByBatch(projectId, issueId).getBody();
    }

    @Override
    public List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId) {
        return listIssueLinkByIssueId(projectId, issueId).stream()
                .filter(u -> u.getTypeCode().equals("issue_test") && u.getWard().equals("被阻塞")).collect(Collectors.toList());
    }

    @Override
    public List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId) {
        return listIssueLinkByIssueId(projectId, issueId).stream()
                .filter(u -> u.getWard().equals("阻塞")).collect(Collectors.toList());
    }

    @Override
    public Map<Long, ProductVersionDTO> getVersionInfo(Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.getVersionInfo.param.projectId.not.be.null");
        return productionVersionClient.listByProjectId(projectId).getBody().stream().collect(Collectors.toMap(ProductVersionDTO::getVersionId, Function.identity()));
    }

    @Override
    public ProjectDTO getProjectInfo(Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.getProjectInfo.param.projectId.not.be.null");
        return projectFeignClient.query(projectId).getBody();
    }

    @Override
    public List<Long> queryIssueIdsByOptions(SearchDTO searchDTO, Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.queryIssueIdsByOptions.param.projectId.not.be.null");
        return testCaseFeignClient.queryIssueIdsByOptions(projectId, searchDTO).getBody();
    }

    @Override
    public IssueDTO createTest(IssueCreateDTO issueCreateDTO, Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.createTest.param.projectId.not.be.null");
        return testCaseFeignClient.createIssue(projectId, issueCreateDTO).getBody();
    }

    @Override
    public List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchIssueToVersion.param.projectId.not.be.null");
        return testCaseFeignClient.batchIssueToVersion(projectId, versionId, issueIds).getBody();
    }

    @Override
    public IssueDTO cloneIssueByIssueId(Long projectId, Long issueId, CopyConditionDTO copyConditionDTO) {
        Assert.notNull(projectId, "error.TestCaseService.cloneIssueByIssueId.param.projectId.not.be.null");
        return testCaseFeignClient.cloneIssueByIssueId(projectId,issueId,copyConditionDTO).getBody();
    }

    @Override
    public List<Long> batchCloneIssue(Long projectId, Long versionId, Long[] issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchCloneIssue.param.projectId.not.be.null");
        return testCaseFeignClient.batchCloneIssue(projectId,versionId,issueIds).getBody();
    }
}
