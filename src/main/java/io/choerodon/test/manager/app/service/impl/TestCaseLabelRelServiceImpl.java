package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.A;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.agile.api.vo.LabelIssueRelDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.TestCaseLabelRelService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCaseLabelRelDTO;
import io.choerodon.test.manager.infra.feign.TestIssueLabelRelFeignClient;
import io.choerodon.test.manager.infra.mapper.TestCaseLabelRelMapper;
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
    @Override
    public void fixLabelCaseRel() {
        List<TestCaseDTO> testCaseDTOS = testCaseService.queryAllCase();
        Set<Long> projectIds = testCaseDTOS.stream().map(TestCaseDTO::getProjectId).collect(Collectors.toSet());
        projectIds.forEach(projectId->{
            List<LabelIssueRelDTO> labelIssueRelDTOS = testIssueLabelRelFeignClient.queryIssueLabelRelList(projectId).getBody();
            List<TestCaseLabelRelDTO> testCaseLabelRelDTOS = labelIssueRelDTOS.stream().map(this::caseIssueDtoTocaseDto).collect(Collectors.toList());
            testCaseLabelRelMapper.batchInsert(testCaseLabelRelDTOS);
            logger.info("========================> project: {} copy successed", projectId);
        });

    }

    @Override
    public Boolean baseCreate(TestCaseLabelRelDTO testCaseLabelRelDTO){
       if( testCaseLabelRelMapper.insert(testCaseLabelRelDTO)!=1){
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
    public void change(Long projectId,Long caseId, Long[] labelIds) {
        // 查询已有的标签

        // 比较 差集

        // 删除没有的，添加新增的

    }

    private TestCaseLabelRelDTO caseIssueDtoTocaseDto( LabelIssueRelDTO labelIssueRelDTO){
        TestCaseLabelRelDTO testCaseLabelRelDTO = new TestCaseLabelRelDTO();
        BeanUtils.copyProperties(labelIssueRelDTO,testCaseLabelRelDTO);
        testCaseLabelRelDTO.setCaseId(labelIssueRelDTO.getIssueId());
        return testCaseLabelRelDTO;
    }

    private void baseDelete(TestCaseLabelRelDTO testCaseLabelRelDTO){
        if(ObjectUtils.isEmpty(testCaseLabelRelDTO)){
         throw  new CommonException("error.delete.case.lable.is.not.null");
        }
        testCaseLabelRelMapper.delete(testCaseLabelRelDTO);
    }
}
