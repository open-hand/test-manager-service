package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.test.manager.infra.dto.TestProjectInfoDTO;

/**
 * @author: 25499
 * @date: 2019/11/22 9:32
 * @description:
 */
public interface TestProjectInfoService {
    void batchCreate(List<TestProjectInfoDTO> testProjectInfoDTOList);
}
