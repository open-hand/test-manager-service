package io.choerodon.test.manager.app.service.impl;


import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.devops.api.dto.ApplicationRepDTO;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.devops.api.dto.DevopsApplicationDeployDTO;
import io.choerodon.devops.api.dto.ReplaceResult;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.feign.ApplicationFeignClient;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.feign.ProjectFeignClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    ApplicationFeignClient applicationFeignClient;

    @Override
    public ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithoutSub.param.projectId.not.null");
        Assert.notNull(pageRequest, "error.TestCaseService.listIssueWithoutSub.param.pageRequest.not.null");
        return testCaseFeignClient.listIssueWithoutSubToTestComponent(projectId, searchDTO,organizationId, pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString());
    }

    @Override
    public ResponseEntity<Page<IssueComponentDetailDTO>> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithoutSubDetail.param.projectId.not.null");
        Assert.notNull(pageRequest, "error.TestCaseService.listIssueWithoutSubDetail.param.pageRequest.not.null");
        return testCaseFeignClient.listIssueWithoutSubDetail(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString(), projectId, searchDTO,organizationId);
    }

    @Override
    public ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId,Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.queryIssue.param.projectId.not.null");
        Assert.notNull(issueId, "error.TestCaseService.queryIssue.param.issueId.not.null");
        return testCaseFeignClient.queryIssue(projectId, issueId,organizationId);
    }

    @Override
    public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId) {
        return listIssueWithoutSub(projectId, searchDTO, pageRequest,organizationId).getBody().stream().collect(Collectors.toMap(IssueListDTO::getIssueId, IssueInfosDTO::new));
    }

    /**
     * 获取issue信息并且更新分页信息
     *
     * @param projectId
     * @param searchDTO
     * @param pageRequest
     * @return
     */
    public <T> Map<Long, IssueInfosDTO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Page<T> page,Long organizationId) {
        Assert.notNull(page, "error.TestCaseService.getIssueInfoMapAndPopulatePageInfo.param.page.not.be.null");
        Page<IssueListDTO> returnDto = listIssueWithLinkedIssues(projectId, searchDTO, pageRequest,organizationId).getBody();

        page.setTotalElements(returnDto.getTotalElements());
        page.setSize(returnDto.getSize());
        page.setNumber(returnDto.getNumber());
        page.setTotalPages((int) (returnDto.getTotalElements() - 1L) / returnDto.getSize() + 1);

        return returnDto.stream().collect(Collectors.toMap(IssueListDTO::getIssueId, IssueInfosDTO::new));

    }

    @Override
    public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail,Long organizationId) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(999999999);
        pageRequest.setPage(0);
        pageRequest.setSort(new Sort(Sort.Direction.DESC, "issueId"));
        if (needDetail) {
            return listIssueWithoutSubDetail(projectId, searchDTO, pageRequest,organizationId).getBody().stream().collect(Collectors.toMap(IssueComponentDetailDTO::getIssueId, IssueInfosDTO::new));
        } else {
            return listIssueWithoutSub(projectId, searchDTO, pageRequest,organizationId).getBody().stream().collect(Collectors.toMap(IssueListDTO::getIssueId, IssueInfosDTO::new));
        }
    }

    @Override
    public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail,Long organizationId) {
        if (ObjectUtils.isEmpty(issueIds)) {
            return new HashMap<>();
        }
        return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), needDetail,organizationId);
    }

    @Override
    public Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, PageRequest pageRequest,Long organizationId) {
        if (ObjectUtils.isEmpty(issueIds)) {
            return new HashMap<>();
        }
        return getIssueInfoMap(projectId, buildIdsSearchDTO(issueIds), pageRequest,organizationId);
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

    public List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds){
        return testCaseFeignClient.listByIssueIds(projectId, issueIds).getBody();
    }

    @Override
    public Page<ComponentForListDTO> listByProjectId(Long projectId, Long componentId, Boolean noIssueTest, SearchDTO searchDTO, PageRequest pageRequest) {
        return testCaseFeignClient.listByProjectId(projectId,null,null,new SearchDTO(),
                pageRequest.getPage(),pageRequest.getSize(),pageRequest.getSort().toString()).getBody();
    }

    @Override
    public List<IssueLabelDTO> listIssueLabel(Long projectId) {
        return testCaseFeignClient.listIssueLabel(projectId).getBody();
    }

    @Override
    public LookupTypeWithValuesDTO queryLookupValueByCode(Long projectId, String typeCode) {
        return testCaseFeignClient.queryLookupValueByCode(projectId,typeCode).getBody();
    }


    @Override
    public List<IssueStatusDTO> listStatusByProjectId(Long projectId) {
        return testCaseFeignClient.listStatusByProjectId(projectId).getBody();
    }

    @Override
    public String getVersionValue(Long projectId, Long appVersionId) {
        return applicationFeignClient.getVersionValue(projectId,appVersionId).getBody();
    }

    @Override
    public ApplicationRepDTO queryByAppId(Long projectId, Long applicationId) {
        return applicationFeignClient.queryByAppId(projectId,applicationId).getBody();
    }

    @Override
    public List<ApplicationVersionRepDTO> getAppversion(Long projectId, List<Long> appVersionId) {
        return applicationFeignClient.getAppversion(projectId,appVersionId).getBody();
    }

    @Override
    public ReplaceResult previewValues(Long projectId, ReplaceResult replaceResult, Long appVersionId) {
        return applicationFeignClient.previewValues(projectId,replaceResult,appVersionId).getBody();
    }

    @Override
    public void deployTestApp(Long projectId, DevopsApplicationDeployDTO applicationDeployDTO) {
        applicationFeignClient.deployTestApp(projectId,applicationDeployDTO);
    }

    @Override
    public List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId) {
        return listIssueLinkByIssueId(projectId, issueId).stream()
                .filter(u -> u.getTypeCode().equals("issue_test")).collect(Collectors.toList());
    }

    @Override
    public List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId) {
        return listIssueLinkByIssueId(projectId, issueId).stream().collect(Collectors.toList());
    }

    @Override
    public Map<Long, ProductVersionDTO> getVersionInfo(Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.getVersionInfo.param.projectId.not.be.null");
        return productionVersionClient.listByProjectId(projectId).getBody().stream().collect(Collectors.toMap(ProductVersionDTO::getVersionId, Function.identity()));
    }

    @Override
    public ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersionInfo(Long projectId, Map<String, Object> searchParamMap) {
        return productionVersionClient.listByOptions(projectId, searchParamMap);
    }

    public Long[] getVersionIds(Long projectId) {
        Assert.notNull(projectId, "error.TestCaseService.getVersionIds.param.projectId.not.be.null");
        return productionVersionClient.listByProjectId(projectId).getBody().stream().map(ProductVersionDTO::getVersionId).distinct().toArray(Long[]::new);

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
    public IssueDTO createTest(IssueCreateDTO issueCreateDTO, Long projectId,String applyType) {
        Assert.notNull(projectId, "error.TestCaseService.createTest.param.projectId.not.be.null");
        return testCaseFeignClient.createIssue(projectId,applyType, issueCreateDTO).getBody();
    }

    @Override
    public List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchIssueToVersion.param.projectId.not.be.null");
        return testCaseFeignClient.batchIssueToVersion(projectId, versionId, issueIds).getBody();
    }


    @Override
    public List<Long> batchCloneIssue(Long projectId, Long versionId, Long[] issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchCloneIssue.param.projectId.not.be.null");
        return testCaseFeignClient.batchCloneIssue(projectId, versionId, issueIds).getBody();
    }

    @Override
    public ResponseEntity batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchIssueToVersionTest.param.projectId.not.be.null");
        return testCaseFeignClient.batchIssueToVersionTest(projectId, versionId, issueIds);
    }

    @Override
    public ResponseEntity batchDeleteIssues(Long projectId, List<Long> issueIds) {
        Assert.notNull(projectId, "error.TestCaseService.batchDeleteIssues.param.projectId.not.be.null");
        return testCaseFeignClient.batchDeleteIssues(projectId, issueIds);
    }

    ResponseEntity<Page<IssueListDTO>> listIssueWithLinkedIssues(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId) {
        Assert.notNull(projectId, "error.TestCaseService.listIssueWithLinkedIssues.param.projectId.not.null");
        Assert.notNull(pageRequest, "error.TestCaseService.listIssueWithLinkedIssues.param.pageRequest.not.null");
        return testCaseFeignClient.listIssueWithLinkedIssues(pageRequest.getPage(), pageRequest.getSize(), pageRequest.getSort().toString(), projectId, searchDTO,organizationId);

    }
}
