package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestStatusVO;
import io.choerodon.test.manager.app.service.TestStatusService;
import io.choerodon.test.manager.infra.dto.TestStatusDTO;
import io.choerodon.test.manager.infra.enums.TestStatusType;
import io.choerodon.test.manager.infra.mapper.TestStatusMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import org.springframework.util.ObjectUtils;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestStatusServiceImpl implements TestStatusService {

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TestStatusVO> query(Long projectId,TestStatusVO testStatusVO) {
        testStatusVO.setProjectId(projectId);
        return modelMapper.map(testStatusMapper.queryAllUnderProject(modelMapper
                .map(testStatusVO, TestStatusDTO.class)), new TypeToken<List<TestStatusVO>>() {
        }.getType());
    }

    @Override
    public TestStatusVO insert(TestStatusVO testStatusVO) {
        if (testStatusVO == null || testStatusVO.getStatusId() != null) {
            throw new CommonException("error.status.insert.statusId.should.be.null");
        }
        TestStatusDTO test = new TestStatusDTO(null, testStatusVO.getStatusType(), testStatusVO.getProjectId(), testStatusVO.getStatusName());
        if (!ObjectUtils.isEmpty(testStatusMapper.select(test))) {
            throw new CommonException("error.status.name.exist");
        }
        TestStatusDTO testStatus = new TestStatusDTO(testStatusVO.getStatusColor(), testStatusVO.getStatusType(), testStatusVO.getProjectId(), null);
        if (!ObjectUtils.isEmpty(testStatusMapper.select(testStatus))) {
            throw new CommonException("error.status.color.exist");
        }
        TestStatusDTO testStatusDTO = modelMapper.map(testStatusVO, TestStatusDTO.class);
        if (testStatusMapper.insert(testStatusDTO) != 1) {
            throw new CommonException("error.test.status.insert");
        }
        return modelMapper.map(testStatusDTO, TestStatusVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean delete(TestStatusVO testStatusVO) {
        TestStatusDTO testStatusDTO = testStatusMapper.selectByPrimaryKey(testStatusVO.getStatusId());
        boolean canDelete = true;
        if (StringUtils.equals(testStatusDTO.getStatusType(), TestStatusType.STATUS_TYPE_CASE)) {
            canDelete = validateDeleteCycleCaseAllow(testStatusDTO.getStatusId());
        } else {
            canDelete = validateDeleteCaseStepAllow(testStatusDTO.getStatusId());
        }
        if(canDelete){
            testStatusMapper.delete(testStatusDTO);
        }
        return canDelete;
    }

    @Override
    public TestStatusVO update(TestStatusVO testStatusVO) {
        TestStatusDTO testStatusDTO = modelMapper.map(testStatusVO, TestStatusDTO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testStatusMapper::updateByPrimaryKey, testStatusDTO, 1, "error.test.status.update");
        return modelMapper.map(testStatusMapper.selectByPrimaryKey(testStatusDTO.getStatusId()), TestStatusVO.class);
    }

    public void populateStatus(TestCycleCaseVO testCycleCaseVO) {
        Assert.notNull(testCycleCaseVO.getExecutionStatus(), "error.populateStatus.statusId.not.null");
        TestStatusDTO testStatusDTO = testStatusMapper.selectByPrimaryKey(testCycleCaseVO.getExecutionStatus());
        if (testStatusDTO != null)
            testCycleCaseVO.setExecutionStatusName(testStatusDTO.getStatusName());
    }

    @Override
    public TestStatusVO queryDefaultStatus(String type, String statusName) {
        Assert.notNull(type, "error.get.default.id.param.not.");
        return modelMapper.map(testStatusMapper.queryDefaultStatus(type, statusName),TestStatusVO.class);
    }

    @Override
    public Long getDefaultStatusId(String type) {
        Assert.notNull(type, "error.get.default.id.param.not.");
        return testStatusMapper.getDefaultStatus(type);
    }

    private Boolean validateDeleteCycleCaseAllow(Long statusId) {
        Assert.notNull(statusId, "error.validate.delete.allow.parameter.statusId.not.null");

        if (testStatusMapper.ifDeleteCycleCaseAllow(statusId) > 0) {
            return false;
        }
        return true;
    }

    private Boolean validateDeleteCaseStepAllow(Long statusId) {
        Assert.notNull(statusId, "error.validate.delete.allow.parameter.statusId.not.null");
        if (testStatusMapper.ifDeleteCaseStepAllow(statusId) > 0) {
            return false;
        }
        return true;
    }
}
