package io.choerodon.test.manager.app.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.choerodon.agile.api.vo.*;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.devops.api.vo.AppServiceDeployVO;
import io.choerodon.devops.api.vo.AppServiceVersionRespVO;
import io.choerodon.devops.api.vo.ApplicationRepDTO;
import io.choerodon.devops.api.vo.InstanceValueVO;
import io.choerodon.test.manager.api.vo.IssueInfosVO;
import io.choerodon.test.manager.api.vo.TestCaseInfoVO;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseService {

    ResponseEntity<PageInfo<IssueListTestVO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    ResponseEntity<PageInfo<IssueComponentDetailVO>> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    <T> Map<Long, IssueInfosVO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Page page, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, Long[] issueIds, PageRequest pageRequest, Long organizationId);

    List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId);

    List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId);

    List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, List<Long> issueId);

    Map<Long, ProductVersionDTO> getVersionInfo(Long projectId);

    ResponseEntity<PageInfo<ProductVersionPageDTO>> getTestCycleVersionInfo(Long projectId, Map<String, Object> searchParamMap);

    Long[] getVersionIds(Long projectId);

    ProjectDTO getProjectInfo(Long projectId);

    List<Long> queryIssueIdsByOptions(SearchDTO searchDTO, Long projectId);

    IssueDTO createTest(IssueCreateDTO issueCreateDTO, Long projectId, String applyType);

    List<IssueSearchDTO> batchIssueToVersion(Long projectId, Long versionId, List<Long> issueIds);

    List<Long> batchCloneIssue(Long projectId, Long versionId, Long[] issueIds);

    ResponseEntity batchIssueToVersionTest(Long projectId, Long versionId, List<Long> issueIds);

    ResponseEntity batchDeleteIssues(Long projectId, List<Long> issueIds);

    List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds);

    PageInfo<ComponentForListDTO> listByProjectId(Long projectId);

    List<IssueLabelDTO> listIssueLabel(Long projectId);

    LookupTypeWithValuesDTO queryLookupValueByCode(String typeCode);

    List<IssueStatusDTO> listStatusByProjectId(Long projectId);

    String getVersionValue(Long projectId, Long appVersionId);

    ApplicationRepDTO queryByAppId(Long projectId, Long applicationId);

    List<AppServiceVersionRespVO> getAppversion(Long projectId, List<Long> appVersionId);

    InstanceValueVO previewValues(Long projectId, InstanceValueVO replaceResult, Long appVersionId);

    void deployTestApp(Long projectId, AppServiceDeployVO appServiceDeployVO);

    /**
     * 创建用例
     * @param projectId
     * @param testCaseVO
     * @return
     */
    TestCaseVO createTestCase(Long projectId, TestCaseVO testCaseVO);

    /**
     * 查询用例详情
     * @param projectId
     * @param caseId
     * @return
     */
    TestCaseInfoVO queryCaseInfo(Long projectId, Long caseId);

    /**
     * 删除测试用例
     * @param projectId
     * @param caseId
     */
    void deleteCase(Long projectId, Long caseId);

    /**
     * 查询当前文件夹下面所有子文件夹的用例
     * @param projectId
     * @param folderId
     * @param pageable
     * @return
     */
    PageInfo<TestCaseRepVO> listAllCaseByFolderId(Long projectId, Long folderId, Pageable pageable);

    /**
     * 查询单个文件夹下的用例
     * @param folderId
     * @return
     */
    List<TestCaseDTO> listCaseByFolderId(Long folderId);

    /**
     * 修改测试用例的信息
     * @param projectId
     * @param testCaseRepVO
     * @return
     */
    TestCaseRepVO  updateCase(Long projectId,TestCaseRepVO testCaseRepVO);
}
