package io.choerodon.test.manager.infra.mapper;

import java.util.List;
import io.choerodon.mybatis.common.Mapper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import org.apache.ibatis.annotations.Param;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCycleCaseAttachmentRelMapper extends Mapper<TestCycleCaseAttachmentRelDTO> {
    void batchInsert(@Param("list") List<TestCycleCaseAttachmentRelDTO> attachmentRelDTOS);
}
