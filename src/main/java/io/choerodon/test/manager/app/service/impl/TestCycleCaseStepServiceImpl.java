package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.checkerframework.checker.units.qual.C;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.choerodon.test.manager.infra.dto.TestCaseStepDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseStepServiceImpl implements TestCycleCaseStepService {

    @Value("${spring.datasource.url}")
    private String dsUrl;

    @Autowired
    private TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Autowired
    private TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCycleCaseStepVO> update(List<TestCycleCaseStepVO> testCycleCaseStepVO) {
        return modelMapper.map(baseUpdate(modelMapper.map(testCycleCaseStepVO, new TypeToken<List<TestCycleCaseStepDTO>>() {
        }.getType())), new TypeToken<List<TestCycleCaseStepVO>>() {
        }.getType());
    }

    @Override
    public PageInfo<TestCycleCaseStepVO> queryCaseStep(Long cycleCaseId, Long projectId, Pageable pageable) {
        PageInfo<TestCycleCaseStepDTO> cycleCaseStepDTOPageInfo = PageHelper.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() ->
                testCycleCaseStepMapper.querListByexecuteId(cycleCaseId));
        PageInfo<TestCycleCaseStepVO> testCycleCaseStepVOList = modelMapper.map(cycleCaseStepDTOPageInfo, PageInfo.class);
        return testCycleCaseStepVOList;
    }

    @Override
    public List<TestCycleCaseStepVO> querySubStep(Long cycleCaseId, Long projectId, Long organizationId) {
        if (cycleCaseId == null) {
            throw new CommonException("error.test.cycle.case.step.caseId.not.null");
        }
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteId(cycleCaseId);
        List<TestCycleCaseStepDTO> testCycleCaseStepDTOS = testCycleCaseStepMapper.queryWithTestCaseStep(testCycleCaseStepDTO, null, null);
        if (testCycleCaseStepDTOS != null && !testCycleCaseStepDTOS.isEmpty()) {
//            List<TestCycleCaseStepVO> testCycleCaseStepVOS = modelMapper.map(testCycleCaseStepDTOS, new TypeToken<List<TestCycleCaseStepVO>>() {
//            }.getType());
            List<TestCycleCaseStepVO> testCycleCaseStepVOS = new ArrayList<>();
            testCycleCaseStepDTOS.forEach(testCycleCaseStep -> {
                TestCycleCaseStepVO testCycleCaseStepVO = modelMapper.map(testCycleCaseStep, TestCycleCaseStepVO.class);
                testCycleCaseStepVO.setDefects(modelMapper.map(testCycleCaseStep.getDefects(), new TypeToken<List<TestCycleCaseDefectRelVO>>(){}.getType()));
                testCycleCaseStepVO.setStepAttachment(modelMapper.map(testCycleCaseStep.getStepAttachment(), new TypeToken<List<TestCycleCaseAttachmentRelVO>>(){}.getType()));
                testCycleCaseStepVOS.add(testCycleCaseStepVO);
            });
            testCycleCaseDefectRelService.populateCaseStepDefectInfo(testCycleCaseStepVOS, projectId, organizationId);
            return testCycleCaseStepVOS;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void batchUpdate(Long executeId, List<TestCaseStepDTO> testCaseStepDTOS) {
        if(CollectionUtils.isEmpty(testCaseStepDTOS)){
            return;
        }
        testCaseStepDTOS.forEach(v -> {
            TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
            testCycleCaseStepDTO.setExecuteId(executeId);
            testCycleCaseStepDTO.setStepId(v.getStepId());
            testCycleCaseStepDTO.setTestStep(v.getTestStep());
            testCycleCaseStepDTO.setExpectedResult(v.getExpectedResult());
            testCycleCaseStepDTO.setTestData(v.getTestData());
            // TODO 测试循环步骤的初始化状态
            baseInsert(testCycleCaseStepDTO);
        });
    }

    @Override
    public void batchInsert(Long executeId, List<TestCaseStepDTO> testCaseStepDTOS) {
        if(CollectionUtils.isEmpty(testCaseStepDTOS)){
           return;
        }
        testCaseStepDTOS.forEach(v -> {
            TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
            testCycleCaseStepDTO.setExecuteId(executeId);
            testCycleCaseStepDTO.setStepId(v.getStepId());
            testCycleCaseStepDTO.setTestStep(v.getTestStep());
            testCycleCaseStepDTO.setExpectedResult(v.getExpectedResult());
            testCycleCaseStepDTO.setTestData(v.getTestData());
            // TODO 测试循环步骤的初始化状态
            baseInsert(testCycleCaseStepDTO);
        });
    }

    @Override
    public void delete(Long executeStepId) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteStepId(executeStepId);
       if(testCycleCaseStepMapper.delete(testCycleCaseStepDTO)!=1){
           throw new CommonException("error delete step");
       }
    }

    @Override
    public void create(List<TestCycleCaseStepVO> testCycleCaseStepVO) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = modelMapper.map(testCycleCaseStepVO, TestCycleCaseStepDTO.class);
        if(testCycleCaseStepMapper.insert(testCycleCaseStepDTO)!=1){
            throw new CommonException("error insert step");
        }
    }

    private List<TestCycleCaseStepDTO> baseUpdate(List<TestCycleCaseStepDTO> list) {
        List<TestCycleCaseStepDTO> res = new ArrayList<>();
        list.forEach(v -> res.add(updateSelf(v)));

        return res;
    }

    private TestCycleCaseStepDTO updateSelf(TestCycleCaseStepDTO testCycleCaseStepDTO) {
        if (testCycleCaseStepMapper.updateByPrimaryKeySelective(testCycleCaseStepDTO) != 1) {
            throw new CommonException("error.testStepCase.update");
        }
        return testCycleCaseStepMapper.selectByPrimaryKey(testCycleCaseStepDTO.getExecuteStepId());
    }

    private void baseInsert(TestCycleCaseStepDTO testCycleCaseStepDTO) {
        if(ObjectUtils.isEmpty(testCycleCaseStepDTO)){
            throw new CommonException("error.insert.cycle.case.step.is.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseStepMapper::insertSelective,testCycleCaseStepDTO,1,"error.insert.cycle.case.step");
    }
}
