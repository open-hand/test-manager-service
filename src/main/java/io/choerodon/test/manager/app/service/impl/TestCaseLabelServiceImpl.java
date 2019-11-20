package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.agile.api.vo.IssueLabelDTO;
import io.choerodon.test.manager.app.service.TestCaseLabelService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;
import io.choerodon.test.manager.infra.feign.TestIssueLabelFeignClient;
import io.choerodon.test.manager.infra.mapper.TestCaseLabelMapper;

/**
 * @author: 25499
 * @date: 2019/11/20 10:53
 * @description:
 */
@Service
public class TestCaseLabelServiceImpl implements TestCaseLabelService {
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestIssueLabelFeignClient testIssueLabelFeignClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TestCaseLabelMapper testCaseLabelMapper;

    @Override
    public void labelFix() {
        List<IssueLabelDTO> issueLabelDTOS = testIssueLabelFeignClient.listAllLabel().getBody();
        List<TestCaseLabelDTO> testCaseLabelDTOList = modelMapper.map(issueLabelDTOS, new TypeToken<List<TestCaseLabelDTO>>() {
        }.getType());
        batchInsert(testCaseLabelDTOList);
    }

    @Override
    public void batchInsert(List<TestCaseLabelDTO> testCaseLabelDTOList) {
        testCaseLabelMapper.batchInsert(testCaseLabelDTOList);
    }
}
