package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.agile.api.vo.LabelFixVO;
import io.choerodon.test.manager.app.service.TestCaseLabelService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;
import io.choerodon.test.manager.infra.feign.TestIssueLabelFeignClient;
import io.choerodon.test.manager.infra.mapper.TestCaseLabelMapper;
import org.springframework.util.ObjectUtils;

/**
 * @author: 25499
 * @date: 2019/11/20 10:53
 * @description:
 */
@Component
public class TestCaseLabelServiceImpl implements TestCaseLabelService {
    private Logger logger = LoggerFactory.getLogger(TestCaseLabelServiceImpl.class);
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestIssueLabelFeignClient testIssueLabelFeignClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TestCaseLabelMapper testCaseLabelMapper;

    @Override
    public void fixLabel() {
        List<LabelFixVO> issueLabelDTOS = testIssueLabelFeignClient.listAllLabel(0L).getBody();
        List<TestCaseLabelDTO> testCaseLabelDTOList = modelMapper.map(issueLabelDTOS, new TypeToken<List<TestCaseLabelDTO>>() {
        }.getType());
        batchInsert(testCaseLabelDTOList);
        logger.info("===========label=============> copy successed");
    }

    @Override
    public void batchInsert(List<TestCaseLabelDTO> testCaseLabelDTOList) {
        testCaseLabelMapper.batchInsert(testCaseLabelDTOList);
    }

    @Override
    public List<TestCaseLabelDTO> listByProjectIds(Long projectId) {
        TestCaseLabelDTO testCaseLabelDTO = new TestCaseLabelDTO();
        testCaseLabelDTO.setProjectId(projectId);
        List<TestCaseLabelDTO> select = testCaseLabelMapper.select(testCaseLabelDTO);
        return select;
    }

    @Override
    public List<TestCaseLabelDTO> listLabelByLabelIds(List<Long> labelIds) {
        return testCaseLabelMapper.listByIds(labelIds);
    }

    @Override
    public TestCaseLabelDTO createOrUpdate(Long projectId, TestCaseLabelDTO testCaseLabelDTO) {
        testCaseLabelDTO.setProjectId(projectId);
        if (ObjectUtils.isEmpty(testCaseLabelDTO.getLabelId())) {
            return baseInsert(testCaseLabelDTO);
        }
        return baseUpdate(testCaseLabelDTO);
    }

    @Override
    public void baseDelete(Long v) {
         testCaseLabelMapper.deleteByPrimaryKey(v);
    }

    private TestCaseLabelDTO baseInsert(TestCaseLabelDTO testCaseLabelDTO) {
        if (ObjectUtils.isEmpty(testCaseLabelDTO)) {
            throw new CommonException("error.insert.label.is.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseLabelMapper::insertSelective, testCaseLabelDTO, 1, "error.insert.label");
        return testCaseLabelDTO;
    }

    private TestCaseLabelDTO baseUpdate(TestCaseLabelDTO testCaseLabelDTO) {
        if (ObjectUtils.isEmpty(testCaseLabelDTO)) {
            throw new CommonException("error.update.label.is.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testCaseLabelMapper::updateByPrimaryKeySelective, testCaseLabelDTO, 1, "error.update.label");
        return testCaseLabelDTO;
    }
}
