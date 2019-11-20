package io.choerodon.test.manager.infra.mapper;

import java.util.List;
import java.util.Set;
import io.choerodon.agile.api.vo.SearchDTO;
import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.api.vo.TestCaseMigrateVO;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;
import org.apache.ibatis.annotations.Param;

/**
 * @author zhaotianxin
 * @since 2019/11/14
 */
public interface TestCaseMapper extends Mapper<TestCaseDTO> {
    List<TestCaseDTO> listCaseByFolderIds(@Param("projectId") Long projectId,@Param("folderIds") Set<Long> folderIds,@Param("searchDTO") SearchDTO searchDTO);

    List<TestCaseDTO> listCopyCase(@Param("projectId") Long projectId ,@Param("caseIds") Long[] caseIds);

    void batchInsertTestCase(@Param("testCaseMigrateVO")TestCaseMigrateVO testCaseMigrateVO);

    List<Long> listIssueIds();
}
