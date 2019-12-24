package io.choerodon.test.manager.infra.mapper;

import java.util.*;

import io.choerodon.test.manager.api.vo.ExcelCaseVO;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.api.vo.CaseCompareVO;
import io.choerodon.test.manager.api.vo.TestCaseMigrateDTO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
public interface TestCaseMapper extends Mapper<TestCaseDTO> {
    List<TestCaseDTO> listCaseByFolderIds(@Param("projectId") Long projectId, @Param("folderIds") Set<Long> folderIds, @Param("searchDTO") SearchDTO searchDTO);

    List<TestCaseDTO> listCopyCase(@Param("projectId") Long projectId ,@Param("caseIds") List<Long> caseIds);

    void batchInsertTestCase(TestCaseMigrateDTO testCaseMigrateDTO);

    List<Long> listIssueIds();

    void updateTestCaseFolder();

    List<Long> listCaseIds(@Param("projectId") Long projectId, @Param("folderIds") Set<Long> folderIds, @Param("searchDTO") SearchDTO searchDTO);

    List<Long> queryFolderId(@Param("projectId") Long projectId);

    List<TestCaseDTO> listByCaseIds(@Param("projectId") Long projectId ,@Param("caseIds") List<Long> caseIds);

    List<ExcelCaseVO> excelCaseList(@Param("projectId") Long projectId , @Param("caseIds") List<Long> caseIds);

    List<TestCaseDTO> listByProject(@Param("projectId") Long projectId);

    @MapKey("caseId")
    Map<Long, CaseCompareVO> queryTestCaseMap(@Param("list") List<Long> caseIds,@Param("executedIds") List<Long> executedId);

    void updateAuditFields(@Param("projectId") Long projectId,@Param("caseIds") Long[] caseIds,@Param("userId") Long userId,@Param("date") Date date);

    List<Long> listUnSelectCaseId(@Param("projectId") Long projectId,@Param("caseIds") List<Long> unSelectCaseIds,@Param("folderIds") Set<Long> unSelectFolderIds);

    int countByProjectIdAndCaseIds(@Param("projectId") Long projectId, @Param("caseIds") List<Long> caseIds);

    void batchDeleteCases(@Param("projectId") Long projectId, @Param("caseIds") List<Long> caseIds);
}
