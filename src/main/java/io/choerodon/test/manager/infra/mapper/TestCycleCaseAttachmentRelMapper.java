package io.choerodon.test.manager.infra.mapper;

import java.util.List;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseAttachmentRelMapper extends BaseMapper<TestCycleCaseAttachmentRelDTO> {
    void batchInsert(@Param("list") List<TestCycleCaseAttachmentRelDTO> attachmentRelDTOS);

    void batchDeleteByExecutIds(List<Long> executeIds);

    List<TestCycleCaseAttachmentRelDTO> listByExecuteIds(@Param("list") List<Long> olderExecuteId,@Param("type") String attachmentCaseStep);

    void batchDeleteByLinkIdsAndType(@Param("list") List<Long> needDeleteExecutedStepIds, @Param("type") String attachmentCaseStep);

    List<TestCycleCaseAttachmentRelDTO> selectByCycleCaseIds(@Param("projectId") Long projectId, @Param("cycleCaseIds") List<Long> cycleCaseIds);
}
