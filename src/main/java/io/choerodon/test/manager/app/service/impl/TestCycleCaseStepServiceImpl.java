package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.util.ConvertUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.omg.CORBA.COMM_FAILURE;
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
    private final static double AVG_NUM = 500.00;

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
    public void update(TestCycleCaseStepVO testCycleCaseStepVO) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = modelMapper.map(testCycleCaseStepVO, TestCycleCaseStepDTO.class);
        testCycleCaseStepMapper.updateByPrimaryKeySelective(testCycleCaseStepDTO);
    }

    @Override
    public void baseUpdate(TestCycleCaseStepDTO testCycleCaseStepDTO) {
       if( testCycleCaseStepMapper.updateByPrimaryKeySelective(testCycleCaseStepDTO)!=1){
           throw new CommonException("error.update.cycle.case.step");
       }
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
                testCycleCaseStepVO.setDefects(modelMapper.map(testCycleCaseStep.getDefects(), new TypeToken<List<TestCycleCaseDefectRelVO>>() {
                }.getType()));
                testCycleCaseStepVO.setStepAttachment(modelMapper.map(testCycleCaseStep.getStepAttachment(), new TypeToken<List<TestCycleCaseAttachmentRelVO>>() {
                }.getType()));
                testCycleCaseStepVOS.add(testCycleCaseStepVO);
            });
            testCycleCaseDefectRelService.populateCaseStepDefectInfo(testCycleCaseStepVOS, projectId, organizationId);
            return testCycleCaseStepVOS;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void batchUpdate(Long executeId, List<TestCycleCaseStepDTO> testCycleCaseStepDTOS) {
        testCycleCaseStepDTOS.forEach(e->{
            testCycleCaseStepMapper.updateByPrimaryKeySelective(e);
        });

    }

    @Override
    public void batchCreate(List<TestCycleCaseStepDTO> testCycleCaseStepDTOS) {
        testCycleCaseStepMapper.batchInsertTestCycleCaseSteps(testCycleCaseStepDTOS);
    }

    @Override
    public void batchDelete(List<Long> executeStepIds) {
        testCycleCaseStepMapper.batchDeleteTestCycleCaseSteps(executeStepIds);
    }

    @Override
    public void batchInsert(List<TestCycleCaseDTO> testCycleCaseDTOList, Map<Long, List<TestCaseStepDTO>> caseStepMap) {
        if (CollectionUtils.isEmpty(testCycleCaseDTOList)) {
            return;
        }
        List<TestCycleCaseStepDTO> list = new ArrayList<>();
        testCycleCaseDTOList.forEach(v -> {
            List<TestCaseStepDTO> testCaseStepDTOS = caseStepMap.get(v.getCaseId());
            if (CollectionUtils.isEmpty(testCaseStepDTOS)) {
                return;
            }
            testCaseStepDTOS.forEach(testCaseStepDTO -> {
                TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO(v.getExecuteId(), testCaseStepDTO.getStepId()
                        , v.getCreatedBy(), v.getLastUpdatedBy(), testCaseStepDTO.getTestStep(), testCaseStepDTO.getTestData(), testCaseStepDTO.getExpectedResult());
                list.add(testCycleCaseStepDTO);
            });
        });
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<List<TestCycleCaseStepDTO>> lists = ConvertUtils.averageAssign(list, (int)Math.ceil(testCycleCaseDTOList.size()/AVG_NUM));
        lists.forEach(v -> testCycleCaseStepMapper.batchInsertTestCycleCaseSteps(v));
    }

    @Override
    public void delete(Long executeStepId) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteStepId(executeStepId);
       if(testCycleCaseStepMapper.delete(testCycleCaseStepDTO)!=1){
           throw new CommonException("error.delete.cycle.step");
       }
    }

    @Override
    public void create(TestCycleCaseStepVO testCycleCaseStepVO) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = modelMapper.map(testCycleCaseStepVO, TestCycleCaseStepDTO.class);
        testCycleCaseStepDTO.setStepStatus(4L);
        if(testCycleCaseStepMapper.insert(testCycleCaseStepDTO)!=1){
            throw new CommonException("error.insert.cycle.step");
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
        if (ObjectUtils.isEmpty(testCycleCaseStepDTO)) {
            throw new CommonException("error.insert.cycle.case.step.is.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseStepMapper::insertSelective, testCycleCaseStepDTO, 1, "error.insert.cycle.case.step");
    }
}
