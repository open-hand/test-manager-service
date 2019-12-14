package io.choerodon.test.manager.app.service;

import java.util.List;
import io.choerodon.test.manager.api.vo.DataLogVO;
import io.choerodon.test.manager.infra.dto.TestDataLogDTO;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
public interface TestDataLogService {
    /**
     * 创建日志
     * @param testDataLogDTO
     */
    void create(TestDataLogDTO testDataLogDTO);

    /**
     * 删除日志
     * @param dataLogDTO
     */
    void delete(TestDataLogDTO dataLogDTO);

    /**
     * 根据caseId 查询记录信息
     * @param projectId
     * @param caseId
     * @return
     */
    List<DataLogVO> queryByCaseId(Long projectId, Long caseId);

    void batchInsert(List<TestDataLogDTO> testDataLogDTOList);
}
