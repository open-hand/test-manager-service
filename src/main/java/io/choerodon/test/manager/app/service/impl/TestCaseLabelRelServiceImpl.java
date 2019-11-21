package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.choerodon.test.manager.app.service.TestCaseLabelService;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.TestCaseLabelDTO;
import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.agile.api.vo.LabelIssueRelFixVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.TestCaseLabelRelService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCaseLabelRelDTO;
import io.choerodon.test.manager.infra.feign.TestIssueLabelRelFeignClient;
import io.choerodon.test.manager.infra.mapper.TestCaseLabelRelMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author: 25499
 * @date: 2019/11/20 13:49
 * @description:
 */
@Service
public class TestCaseLabelRelServiceImpl implements TestCaseLabelRelService {
    private Logger logger = LoggerFactory.getLogger(TestCaseLabelRelServiceImpl.class);

    @Autowired
    private TestIssueLabelRelFeignClient testIssueLabelRelFeignClient;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TestCaseLabelRelMapper testCaseLabelRelMapper;
    @Autowired
    private TestCaseService testCaseService;
    @Autowired
    private TestCaseLabelService testCaseLabelService;

    @Override
    @Transactional
    @DataLog(type = DataLogConstants.LABEL_CREATE)
    public Boolean baseCreate(TestCaseLabelRelDTO testCaseLabelRelDTO) {
        if (testCaseLabelRelMapper.insert(testCaseLabelRelDTO) != 1) {
            throw new CommonException("error.insert.testCaseLabelRel");
        }
        return true;
    }

    @Override
    public void batchInsert(List<TestCaseLabelRelDTO> testCaseLabelRelDTOList) {
        testCaseLabelRelMapper.batchInsert(testCaseLabelRelDTOList);
    }

    @Override
    public List<TestCaseLabelRelDTO> listLabelByCaseId(Long caseId) {
        TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
        testCaseLabelRelDTO.setCaseId(caseId);
        return testCaseLabelRelMapper.select(testCaseLabelRelDTO);
    }

    @Override
    public void deleteByCaseId(Long caseId) {
        TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
        testCaseLabelRelDTO.setCaseId(caseId);
        baseDelete(testCaseLabelRelDTO);
    }

    @Override
    public void copyByCaseId(Long projectId, Long caseId, Long oldCaseId) {
        TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
        testCaseLabelRelDTO.setCaseId(oldCaseId);
        testCaseLabelRelDTO.setProjectId(projectId);
        List<TestCaseLabelRelDTO> olderCaseLabelRels = testCaseLabelRelMapper.select(testCaseLabelRelDTO);
        if (CollectionUtils.isEmpty(olderCaseLabelRels)) {
            return;
        }
        List<TestCaseLabelRelDTO> newCaseLabelRels = new ArrayList<>();
        olderCaseLabelRels.forEach(v -> {
            v.setCaseId(caseId);
            v.setObjectVersionNumber(null);
            v.setProjectId(projectId);
            newCaseLabelRels.add(v);
        });
        batchInsert(newCaseLabelRels);
    }

    @Override
    @DataLog(type = DataLogConstants.LABEL_DELETE)
    public void baseDelete(TestCaseLabelRelDTO testCaseLabelRelDTO) {
        if (ObjectUtils.isEmpty(testCaseLabelRelDTO)) {
            throw new CommonException("error.delete.case.lable.is.not.null");
        }
        testCaseLabelRelMapper.delete(testCaseLabelRelDTO);
    }

    @Override
    public List<TestCaseLabelRelDTO> query(TestCaseLabelRelDTO testCaseLabelRel) {
        return testCaseLabelRelMapper.select(testCaseLabelRel);
    }

    private TestCaseLabelRelDTO caseIssueDtoTocaseDto(LabelIssueRelFixVO labelIssueRelDTO) {
        TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
        BeanUtils.copyProperties(labelIssueRelDTO, testCaseLabelRelDTO);
        testCaseLabelRelDTO.setCaseId(labelIssueRelDTO.getIssueId());
        return testCaseLabelRelDTO;
    }

}
