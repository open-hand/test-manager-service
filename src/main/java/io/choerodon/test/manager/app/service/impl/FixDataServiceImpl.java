package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.service.ITestStatusService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Transactional
@Component
public class FixDataServiceImpl implements FixDataService {

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    TestIssueFolderRelService testIssueFolderRelService;

    @Autowired
    TestIssueFolderService testIssueFolderService;

    @Autowired
    TestCycleCaseStepService testCycleCaseStepService;

    @Autowired
    TestCaseStepService testCaseStepService;

    private static final String CYCLE = "cycle";

    Log log = LogFactory.getLog(this.getClass());


    @Override
    public void fixCycleData(Long projectId) {
        log.info("start fix data...now thread :" + Thread.currentThread().getName());
        TestCycleE testCycleE = TestCycleEFactory.create();
        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();

        //从敏捷查询所有type是issue_test的issue
        log.info("query all issue data under project...: ");
        List<IssueProjectDTO> issueProjectDTOS = testCaseService.queryIssueTestGroupByProject(projectId);
//        if(Thread.interrupted()){
//            return;
//        }
        //根据projectId查找version
        for (IssueProjectDTO issueProjectDTO : issueProjectDTOS) {
            //旧数据放到当前项目（min version）最小版本的一个叫做旧数据的文件夹下
            log.info("query versions under project...");
            Long[] versionIds = testCaseService.getVersionIds(issueProjectDTO.getProjectId());

            if (ObjectUtils.isEmpty(versionIds)) {
                //无version的全删除掉
                log.info("delete issue without version under project...");
                //testCaseService.batchDeleteIssues(issueProjectDTO.getProjectId(), issueProjectDTO.getIssueIdList());
            } else {
                //创建文件夹
                log.info("insert folder to store old data under project...");
                TestIssueFolderDTO needTestIssueFolderDTO = new TestIssueFolderDTO(null, "旧数据", versionIds[0], issueProjectDTO.getProjectId(), CYCLE, null);
                TestIssueFolderDTO needFolder = testIssueFolderService.insert(needTestIssueFolderDTO);
                //有version的将他们放到目标文件夹
                List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();
                for (Long issueId : issueProjectDTO.getIssueIdList()) {
                    if (needFolder != null) {
                        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(needFolder.getFolderId(), needFolder.getVersionId(), issueProjectDTO.getProjectId(), issueId, null);
                        testIssueFolderRelDTOS.add(testIssueFolderRelDTO);
                    }
                }
                log.info("establish relationship of issue and folder under project...");
                testIssueFolderRelService.insertBatchRelationship(issueProjectDTO.getProjectId(), testIssueFolderRelDTOS);
            }
        }

        log.info("query all cycles...");
        List<TestCycleE> testCycleES = testCycleE.queryAll();
        //用于设置IssueFolder
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        //修正所有的cycle数据
        for (TestCycleE resTestCycleE : testCycleES) {
            Long tempCycleId = resTestCycleE.getCycleId();
            //设置修正数据
            TestCycleE needTestCycleE = resTestCycleE;
            if (resTestCycleE.getType().equals("folder")) {
                //TestCycleE的type为folder的情况
                testIssueFolderDTO.setType(CYCLE);
            } else {
                if (resTestCycleE.getType().equals("temp")) {
                    resTestCycleE.setType(CYCLE);
                    resTestCycleE.updateSelf();
                }
                resTestCycleE.setType("folder");
                resTestCycleE.setParentCycleId(resTestCycleE.getCycleId());
                resTestCycleE.setCycleId(null);
                //如果是cycle或者temp类型就新增一个cycle为其子folder
                needTestCycleE.setCycleName(needTestCycleE.getCycleName() + "阶段");
                needTestCycleE = resTestCycleE.addSelf();
                needTestCycleE.setObjectVersionNumber(1L);
                testIssueFolderDTO.setType(CYCLE);
            }

            log.info("query project by version...");
            Long needProjectId = testCaseService.queryProjectIdByVersionId(needTestCycleE.getVersionId());
            testIssueFolderDTO.setProjectId(needProjectId);
            testIssueFolderDTO.setVersionId(needTestCycleE.getVersionId());

            //如果有父节点的话，将folder的名字设置为如：父名称_子名称
            if (needTestCycleE.getParentCycleId() != null) {
                testCycleE.setCycleId(needTestCycleE.getParentCycleId());
                log.info("query father cycle...");
                TestCycleE fatherCycleE = testCycleE.queryOne();
                //如果父名字和子名字是相同的就说明这个名字是唯一的只需要给folder设置此名即可，中间不需要加 _
                log.info("father:" + fatherCycleE.getCycleName());
                log.info("son:" + needTestCycleE.getCycleName());
                if (fatherCycleE.getCycleName().equals(needTestCycleE.getCycleName())) {
                    testIssueFolderDTO.setName(needTestCycleE.getCycleName() + "阶段");
                } else {
                    testIssueFolderDTO.setName(fatherCycleE.getCycleName() + "_" + needTestCycleE.getCycleName());
                }
                needTestCycleE.setVersionId(fatherCycleE.getVersionId());
                testIssueFolderDTO.setVersionId(fatherCycleE.getVersionId());
            }

            //插入folder表，更新cycle表
            log.info("insert folder ...");
            Long folderId = testIssueFolderService.insert(testIssueFolderDTO).getFolderId();
            needTestCycleE.setFolderId(folderId);
            needTestCycleE.updateSelf();

            //查询原来的cycle在cycleCase表中的数据
            testCycleCaseE.setCycleId(tempCycleId);
            List<TestCycleCaseE> testCycleCaseES = testCycleCaseE.querySelf();

            //用于设置folderRel数据
            List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();
            //没有case存在的话就不进行cyclecase，各个step和folderRel表的操作
            if (ObjectUtils.isEmpty(testCycleCaseES)) {
                continue;
            }
            List<Long> issueIds = testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), testCycleCaseES.stream().map(TestCycleCaseE::getIssueId).toArray(Long[]::new));

            //将原来的case关联的issue改成新克隆出来的issue，并修改各个step关系
            int i = 0;
            for (TestCycleCaseE cycleCaseE : testCycleCaseES) {
                //根据以前的issueId找到case_step
                TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
                testCaseStepE.setIssueId(cycleCaseE.getIssueId());
                List<TestCaseStepE> oldCaseSteps = testCaseStepE.queryByParameter();

                cycleCaseE.setIssueId(issueIds.get(i));
                cycleCaseE.updateSelf();

                //根据issueId克隆caseStep
                TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
                testCaseStepDTO.setIssueId(cycleCaseE.getIssueId());
                List<TestCaseStepDTO> clonedCaseStepDTO = testCaseStepService.batchClone(testCaseStepDTO, issueIds.get(i++), needProjectId);

                List<TestCycleCaseStepE> cycleCaseStepES = new ArrayList<>();
                TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
                int j = 0;
                for (TestCaseStepE v : oldCaseSteps) {
                    //根据以前的case_step去将cycle_case_step更新为前面克隆得到的step
                    //查找以前stepId对应的CycleCaseStep
                    testCycleCaseStepE.setStepId(v.getStepId());
                    List<TestCycleCaseStepE> testCycleCaseStepES = testCycleCaseStepE.querySelf();
                    //将CycleCaseStep对应的stepId修改为新克隆出来的stepId
                    for (TestCycleCaseStepE cs : testCycleCaseStepES) {
                        if (!ObjectUtils.isEmpty(clonedCaseStepDTO)) {
                            cs.setStepId(clonedCaseStepDTO.get(j).getStepId());
                            cycleCaseStepES.add(cs);
                        }
                    }
                    j++;
                }
                testCycleCaseStepService.update(ConvertHelper.convertList(cycleCaseStepES, TestCycleCaseStepDTO.class));
            }

            for (Long v : issueIds) {
                TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
                testIssueFolderRelDTO.setFolderId(folderId);
                testIssueFolderRelDTO.setProjectId(needProjectId);
                testIssueFolderRelDTO.setVersionId(needTestCycleE.getVersionId());
                testIssueFolderRelDTO.setIssueId(v);
                testIssueFolderRelDTOS.add(testIssueFolderRelDTO);
            }
            testIssueFolderRelService.insertBatchRelationship(needProjectId, testIssueFolderRelDTOS);
        }
    }

}
