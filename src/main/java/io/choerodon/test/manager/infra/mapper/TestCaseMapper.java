package io.choerodon.test.manager.infra.mapper;

import java.util.*;

import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
public interface TestCaseMapper extends BaseMapper<TestCaseDTO> {
    List<TestCaseDTO> listCaseByFolderIds(@Param("projectId") Long projectId, @Param("folderIds") Set<Long> folderIds, @Param("searchDTO") SearchDTO searchDTO);

    List<TestCaseDTO> listCopyCase(@Param("projectId") Long projectId ,@Param("caseIds") List<Long> caseIds);

    void batchInsertTestCase(TestCaseMigrateDTO testCaseMigrateDTO);

    List<Long> listIssueIds();

    void updateTestCaseFolder();

    List<Long> listCaseIds(@Param("projectId") Long projectId, @Param("folderIds") Set<Long> folderIds, @Param("searchDTO") SearchDTO searchDTO);

    List<TestCaseDTO> listCase(@Param("projectId") Long projectId, @Param("folderIds") Set<Long> folderIds, @Param("searchDTO") SearchDTO searchDTO);

    List<Long> queryFolderId(@Param("projectId") Long projectId);

    List<TestCaseDTO> listByCaseIds(@Param("projectId") Long projectId ,@Param("caseIds") List<Long> caseIds,@Param("isDesc")Boolean isDesc);

    List<ExcelCaseVO> excelCaseList(@Param("projectId") Long projectId , @Param("caseIds") List<Long> caseIds);

    List<TestCaseDTO> listByProject(@Param("projectId") Long projectId);

    @MapKey("caseId")
    Map<Long, CaseCompareVO> queryTestCaseMap(@Param("list") List<Long> caseIds,@Param("executedIds") List<Long> executedId);

    void updateAuditFields(@Param("projectId") Long projectId,@Param("caseIds") Long[] caseIds,@Param("userId") Long userId,@Param("date") Date date);

    List<Long> listUnSelectCaseId(@Param("projectId") Long projectId,@Param("caseIds") List<Long> unSelectCaseIds,@Param("folderIds") Set<Long> unSelectFolderIds);

    int countByProjectIdAndCaseIds(@Param("projectId") Long projectId, @Param("caseIds") List<Long> caseIds);

    void batchDeleteCases(@Param("projectId") Long projectId, @Param("caseIds") List<Long> caseIds);

    void batchUpdateCasePriority(@Param("priorityId") Long priorityId, @Param("changePriorityId") Long changePriorityId, @Param("userId") Long userId, @Param("projectIds") List<Long> projectIds);

    long checkPriorityDelete(@Param("priorityId") Long priorityId, @Param("projectIds") List<Long> projectIds);

    List<Long> selectALLProjectId();

    int updatePriorityByProject(@Param("projectIds") List<Long> projectIds, @Param("priorityId") Long priorityId);

    List<TestCaseVO> queryCaseByContent(@Param("projectId") Long projectId, @Param("content") String content, @Param("issueId") Long issueId);

    List<TestCaseLinkVO> listByLinkCaseIds(@Param("projectId") Long projectId, @Param("linkCaseIds") List<Long> linkCaseIds);
}
