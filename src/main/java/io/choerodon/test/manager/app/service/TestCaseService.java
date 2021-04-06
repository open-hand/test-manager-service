package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.IssueInfosVO;
import io.choerodon.test.manager.api.vo.TestCaseInfoVO;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.api.vo.agile.*;
import io.choerodon.test.manager.api.vo.devops.AppServiceDeployVO;
import io.choerodon.test.manager.api.vo.devops.AppServiceVersionRespVO;
import io.choerodon.test.manager.api.vo.devops.ApplicationRepDTO;
import io.choerodon.test.manager.api.vo.devops.InstanceValueVO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseService {

    Page<IssueListTestVO> listIssueWithoutSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    Page<IssueComponentDetailVO> listIssueWithoutSubDetail(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    IssueDTO queryIssue(Long projectId, Long issueId, Long organizationId);

    <T> Map<Long, IssueInfosVO> getIssueInfoMapAndPopulatePageInfo(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Page page, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, SearchDTO searchDTO, boolean needDetail, Long organizationId);

    Map<Long, IssueInfosVO> getIssueInfoMap(Long projectId, Long[] issueIds, boolean needDetail, Long organizationId);

    List<IssueLinkDTO> getLinkIssueFromIssueToTest(Long projectId, List<Long> issueId);

    List<IssueLinkDTO> getLinkIssueFromTestToIssue(Long projectId, List<Long> issueId);

    List<IssueLinkDTO> listIssueLinkByIssueId(Long projectId, List<Long> issueId);

//    Map<Long, ProductVersionDTO> getVersionInfo(Long projectId);
//
//    Long[] getVersionIds(Long projectId);

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
     *
     * @param projectId
     * @param testCaseVO
     * @return
     */
    TestCaseRepVO createTestCase(Long projectId, TestCaseVO testCaseVO);

    /**
     * 创建用例
     *
     * @param projectId
     * @param testCaseVO
     * @param outsideCount 是否使用外部计数，若为null则不使用， 使用则交给外部独立计数
     * @return
     */
    TestCaseRepVO createTestCase(Long projectId, TestCaseVO testCaseVO, AtomicLong outsideCount);

    /**
     * 查询用例详情
     *
     * @param projectId
     * @param caseId
     * @return
     */
    TestCaseInfoVO queryCaseInfo(Long projectId, Long caseId);

    /**
     * 删除测试用例
     *
     * @param projectId
     * @param caseId
     */
    void deleteCase(Long projectId, Long caseId);

    /**
     * 查询当前文件夹下面所有子文件夹的用例
     *
     * @param projectId
     * @param folderId
     * @param pageRequest
     * @param searchDTO
     * @return
     */
    Page<TestCaseRepVO> listAllCaseByFolderId(Long projectId, Long folderId, PageRequest pageRequest, SearchDTO searchDTO, Long planId);

    /**
     * 查询单个文件夹下的用例
     *
     * @param folderId
     * @return
     */
    List<TestCaseDTO> listCaseByFolderId(Long folderId);

    /**
     * 修改测试用例的信息
     *
     * @param projectId
     * @param testCaseRepVO
     * @return
     */
    TestCaseRepVO updateCase(Long projectId, TestCaseRepVO testCaseRepVO, String[] fieldList);

    /**
     * 移动测试用例
     *
     * @param projectId
     * @param folderId
     * @param testCaseRepVOS
     */
    void batchMove(Long projectId, Long folderId, List<TestCaseRepVO> testCaseRepVOS);

    /**
     * 克隆测试用例
     *
     * @param projectId
     * @param folderId
     * @param testCaseRepVOS
     */
    List<TestCaseDTO> batchCopy(Long projectId, Long folderId, List<TestCaseRepVO> testCaseRepVOS);

    /**
     * 克隆测试用例
     *
     * @param projectId
     * @param folderId
     * @param testCaseRepVOS
     * @param outsideCount 使用外部计数
     */
    List<TestCaseDTO> batchCopy(Long projectId, Long folderId, List<TestCaseRepVO> testCaseRepVOS, AtomicLong outsideCount);

    /**
     * 更新version_num
     *
     * @param caseId
     */
    void updateVersionNum(Long caseId);


    TestCaseDTO importTestCase(IssueCreateDTO issueCreateDTO, Long projectId, String applyType);

    /**
     * 查询文件夹下所有用例（不分页）
     *
     * @param projectId
     * @param folderId
     * @return
     */
    List<Long> listAllCaseByFolderId(Long projectId, Long folderId);


    /**
     * 根据caseIds 查询用例
     *
     * @param projectId
     * @param caseIds
     * @return
     */
    List<TestCaseDTO> listByCaseIds(Long projectId, List<Long> caseIds);

    TestCaseInfoVO queryCaseRep(Long caseId);

    TestCaseDTO baseUpdate(TestCaseDTO testCase);

    /**
     * 分页搜索查询issue列表
     *
     * @param projectId
     * @param issueId
     * @param issueNum
     * @param self
     * @param content
     * @param pageRequest
     * @return
     */
    Page<IssueNumDTO> queryIssueByOptionForAgile(Long projectId, Long issueId, String issueNum, Boolean self, String content, PageRequest pageRequest);

    /**
     * 获取文件夹下的所有文件夹id（包括传入的id）
     * @param projectId 项目id
     * @param folderId 当前文件夹
     * @return idset
     */
    Set<Long> selectFolderIds(Long projectId, Long folderId);

    void batchUpdateCasePriority(Long organizationId, Long priorityId, Long changePriorityId, Long userId, List<Long> projectIds);

    Page<TestCaseVO> queryCaseByContent(Long projectId, PageRequest pageRequest,String content, Long issueId);

    /**
     * 更新用例的版本但不更新乐观锁的版本号
     * @param caseId 用例id
     * @param userId 用户id
     */
    void updateVersionNumNotObjectVersion(Long caseId, Long userId);

    /**
     * 查询未测试用例未关联的问题
     *
     * @param caseId 测试用例id
     * @param projectId 项目id
     * @param searchDTO 查询参数
     * @param pageRequest 分页参数
     * @param organizationId 组织id
     * @return 测试用例未关联的问题
     */
    Page<IssueListFieldKVVO> listUnLinkIssue(Long caseId, Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    List<TestCaseDTO> queryByCustomNum(Long projectId, String customNum);

//    Boolean checkCustomNumExist(Long projectId, String customNum);
}
