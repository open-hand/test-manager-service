package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.IssueProjectDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseService {
    //
    ResponseEntity<Page<IssueListDTO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId);

    ResponseEntity<Page<IssueComponentDetailDTO>> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId);

    ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId,Long organizationId);

    Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId);

    <T> Map<Long, IssueInfosDTO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Page<T> page,Long organizationId);

    Object getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail,Long organizationId);

    Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail,Long organizationId);

    Map<Long, IssueInfosDTO> getIssueInfoMap(Long projectId, Long[] issueIds, PageRequest pageRequest,Long organizationId);

    List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId);

    List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId);

    List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, List<Long> issueId);

    Map<Long, ProductVersionDTO> getVersionInfo(Long projectId);

    ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersionInfo(Long projectId,Map<String, Object> searchParamMap);

    Long[] getVersionIds(Long projectId);

    ProjectDTO getProjectInfo(Long projectId);

    List<Long> queryIssueIdsByOptions(SearchDTO searchDTO, Long projectId);

    IssueDTO createTest(IssueCreateDTO issueCreateDTO, Long projectId,String applyType);

    List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds);

    List<Long> batchCloneIssue(Long projectId, Long versionId, Long[] issueIds);

    ResponseEntity batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds);

    ResponseEntity batchDeleteIssues(Long projectId, List<Long> issueIds);

    Long queryProjectIdByVersionId(Long versionId);

    List<IssueProjectDTO> queryIssueTestGroupByProject(Long projectId);

    List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds);

    Page<ComponentForListDTO> listByProjectId(Long projectId, Long componentId, Boolean noIssueTest, SearchDTO searchDTO, PageRequest pageRequest);

    List<IssueLabelDTO> listIssueLabel(Long projectId);

    LookupTypeWithValuesDTO queryLookupValueByCode(Long projectId, String typeCode);

    List<IssueStatusDTO> listStatusByProjectId(Long projectId);
}
