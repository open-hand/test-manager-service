package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleDTO;
import io.choerodon.test.manager.infra.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import io.choerodon.test.manager.infra.enums.TestPlanStatus;
import io.choerodon.test.manager.infra.mapper.TestPlanMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import io.reactivex.internal.functions.Functions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author: 25499
 * @date: 2019/11/26 14:17
 * @description:
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestPlanServiceImpl implements TestPlanServcie {
    @Autowired
    private TestPlanMapper testPlanMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestIssueFolderService testIssueFolderService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleService testCycleService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;
    @Override
    public TestPlanDTO create(Long projectId,TestPlanVO testPlanVO) {
        // 创建计划
        TestPlanDTO testPlan = modelMapper.map(testPlanVO, TestPlanDTO.class);
        testPlan.setProjectId(projectId);
        testPlan.setStatusCode(TestPlanStatus.NOTSTARTED.getStatus());
        TestPlanDTO testPlanDTO = baseCreate(testPlan);

        // 获取用例和文件夹信息
        List<TestIssueFolderDTO> testIssueFolderDTOS = new ArrayList<>();
        List<TestCaseDTO> testCaseDTOS = new ArrayList<>();
        // 是否自选
        if(!testPlanVO.getOptional()){
            testCaseDTOS = testCaseService.listCaseByProjectId(projectId);
            if(CollectionUtils.isEmpty(testCaseDTOS)){
                return testPlanDTO;
            }
            List<Long> folderIds = testCaseDTOS.stream().map(TestCaseDTO::getFolderId).collect(Collectors.toList());
            testIssueFolderDTOS = testIssueFolderService.listFolderByFolderIds(folderIds);
        }
        else{
            testIssueFolderDTOS = testPlanVO.getFolders();
            testCaseDTOS = testPlanVO.getTestCases();
            if(CollectionUtils.isEmpty(testIssueFolderDTOS) || CollectionUtils.isEmpty(testCaseDTOS)){
             return testPlanDTO;
            }
        }
        // 创建测试循环
        List<TestCycleDTO> testCycleDTOS = testCycleService.batchInsertByFoldersAndPlan(testPlanDTO, testIssueFolderDTOS);
        // 创建测试循环用例
        Map<Long, TestCycleDTO> testCycleMap = testCycleDTOS.stream().collect(Collectors.toMap(TestCycleDTO::getFolderId, Function.identity()));
        testCycleCaseService.batchInsertByTestCase(testCycleMap,testCaseDTOS);


        //  是否同步
        return testPlanDTO;
    }


    private TestPlanDTO baseCreate(TestPlanDTO testPlanDTO){
        if(ObjectUtils.isEmpty(testPlanDTO)){
           throw  new CommonException("error.test.plan.is.not.null");
        }
        DBValidateUtil.executeAndvalidateUpdateNum(testPlanMapper::insertSelective,testPlanDTO,1,"error.insert.test.plan");
        return testPlanDTO;
    }
}
