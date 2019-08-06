package io.choerodon.test.manager.app.service.impl;

import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.test.manager.api.vo.TestAutomationResultVO;
import io.choerodon.test.manager.app.service.TestAutomationResultService;
import io.choerodon.test.manager.infra.dto.TestAutomationResultDTO;
import io.choerodon.test.manager.infra.mapper.TestAutomationResultMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;

@Service
public class TestAutomationResultServiceImpl implements TestAutomationResultService {

    @Autowired
    private TestAutomationResultMapper testAutomationResultMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TestAutomationResultVO> query(TestAutomationResultVO testAutomationResultVO) {
        return modelMapper.map(testAutomationResultMapper.select(modelMapper.map(testAutomationResultVO,
                TestAutomationResultDTO.class)), new TypeToken<List<TestAutomationResultDTO>>() {
        }.getType());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestAutomationResultVO changeAutomationResult(TestAutomationResultVO testAutomationResultVO, Long projectId) {
        Assert.notNull(testAutomationResultVO, "error.change.testAutomationResult.param.not.null");
        TestAutomationResultDTO testAutomationResultDTO = modelMapper.map(testAutomationResultVO, TestAutomationResultDTO.class);
        if (testAutomationResultDTO.getId() == null) {
            testAutomationResultDTO = insert(testAutomationResultDTO);
        } else {
            testAutomationResultDTO = update(testAutomationResultDTO);
        }

        return modelMapper.map(testAutomationResultDTO, TestAutomationResultVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeAutomationResult(TestAutomationResultVO testAutomationResultVO) {
        delete(modelMapper.map(testAutomationResultVO, TestAutomationResultDTO.class));
    }

    private TestAutomationResultDTO insert(TestAutomationResultDTO testAutomationResultDTO) {
        Date now = new Date();
        testAutomationResultDTO.setCreationDate(now);
        testAutomationResultDTO.setLastUpdateDate(now);
        DBValidateUtil.executeAndvalidateUpdateNum(
                testAutomationResultMapper::insertOneResult, testAutomationResultDTO, 1, "error.testAutomationResult.insert");
        return testAutomationResultDTO;
    }

    private TestAutomationResultDTO update(TestAutomationResultDTO testAutomationResultDTO) {
        DBValidateUtil.executeAndvalidateUpdateNum(
                testAutomationResultMapper::updateByPrimaryKeySelective, testAutomationResultDTO, 1, "error.testAutomationResult.update");

        return testAutomationResultMapper.selectByPrimaryKey(testAutomationResultDTO.getId());
    }

    private void delete(TestAutomationResultDTO testAutomationResultDTO) {
        Assert.notNull(testAutomationResultDTO, "error.testAutomationResult.delete.param1.not.null");

        testAutomationResultMapper.delete(testAutomationResultDTO);
    }
}
