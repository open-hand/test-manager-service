package io.choerodon.test.manager.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.dto.TestStatusDTO;
import io.choerodon.test.manager.infra.mapper.TestStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @author zhaotianxin
 * @date 2021/11/30 15:05
 */
@Component
public class TestStatusValidator {

    @Autowired
    private TestStatusMapper testStatusMapper;

    public void validateTestStatusColor(TestStatusDTO testStatusDTO) {
        TestStatusDTO oldTestStatus = testStatusMapper.selectByPrimaryKey(testStatusDTO.getStatusId());
        if (!oldTestStatus.getStatusColor().equals(testStatusDTO.getStatusColor())) {
            TestStatusDTO testStatus = new TestStatusDTO(testStatusDTO.getStatusColor(), testStatusDTO.getStatusType(), testStatusDTO.getProjectId(), null);
            if (!ObjectUtils.isEmpty(testStatusMapper.select(testStatus))) {
                throw new CommonException("error.status.color.used");
            }
        }
    }
}
