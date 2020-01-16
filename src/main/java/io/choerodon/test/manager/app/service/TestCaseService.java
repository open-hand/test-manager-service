package io.choerodon.test.manager.app.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.choerodon.test.manager.api.vo.agile.*;
import org.springframework.data.domain.Pageable;
import io.choerodon.test.manager.api.vo.devops.AppServiceDeployVO;
import io.choerodon.test.manager.api.vo.devops.AppServiceVersionRespVO;
import io.choerodon.test.manager.api.vo.devops.ApplicationRepDTO;
import io.choerodon.test.manager.api.vo.devops.InstanceValueVO;
import io.choerodon.test.manager.api.vo.IssueInfosVO;
import io.choerodon.test.manager.api.vo.TestCaseInfoVO;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseService {

    ResponseEntity<PageInfo<IssueListTestVO>> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId);

    ResponseEntity<PageInfo<IssueComponentDetailVO>> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId);

    ResponseEntity<IssueDTO> queryIssue(Long projectId, Long issueId, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId);

    <T> Map<Long, IssueInfosVO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, Pageable pageable, Page page, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, Long[] issueIds, Pageable pageable, Long organizationId);

    List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId);

    List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId);

    List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, List<Long> issueId);

    Map<Long, ProductVersionDTO> getVersionInfo(Long projectId);

    Long[] getVersionIds(Long projectId);

    ProjectDTO getProjectInfo(Long projectId);

    List<Long> queryIssueIdsByOptions(SearchDTO searchDTO, Long projectId);

    IssueDTO createTest(IssueCreateDTO issueCreateDTO, Long projectId, String applyType);

    void batchDeleteIssues(Long projectId, List<Long> issueIds);

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
    TestCaseRepVO createTestCase(Long projectId, TestCaseVO testCaseVO);

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
     * @param searchDTO
     * @return
     */
    PageInfo<TestCaseRepVO> listAllCaseByFolderId(Long projectId, Long folderId, Pageable pageable,SearchDTO searchDTO,Long planId);

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
    TestCaseRepVO  updateCase(Long projectId,TestCaseRepVO testCaseRepVO,String[] fieldList);

    /**
     * 移动测试用例
     * @param projectId
     * @param folderId
     * @param testCaseRepVOS
     */
    void batchMove(Long projectId,Long folderId,List<TestCaseRepVO> testCaseRepVOS);

    /**
     * 克隆测试用例
     * @param projectId
     * @param folderId
     * @param testCaseRepVOS
     */
    void batchCopy(Long projectId,Long folderId,List<TestCaseRepVO> testCaseRepVOS);

    /**
     * 更新version_num
     * @param caseId
     */
    void updateVersionNum(Long caseId);


    TestCaseDTO importTestCase(IssueCreateDTO issueCreateDTO, Long projectId, String applyType);

    /**
     * 查询文件夹下所有用例（不分页）
     * @param projectId
     * @param folderId
     * @return
     */
    List<Long> listAllCaseByFolderId(Long projectId, Long folderId);


    /**
     * 根据caseIds 查询用例
     * @param projectId
     * @param caseIds
     * @return
     */
    List<TestCaseDTO> listByCaseIds(Long projectId,List<Long> caseIds);

    TestCaseInfoVO queryCaseRep(Long caseId);

    TestCaseDTO baseUpdate(TestCaseDTO testCase);
}
