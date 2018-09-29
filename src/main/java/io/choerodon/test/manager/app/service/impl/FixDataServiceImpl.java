package io.choerodon.test.manager.app.service.impl;

import feign.codec.DecodeException;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        //从敏捷查询所有type是issue_test的issue
        log.info("query all issue data under project...: ");
        List<IssueProjectDTO> issueProjectDTOS = testCaseService.queryIssueTestGroupByProject(projectId);
        step1(issueProjectDTOS);

        log.info("query all cycles...");
        List<TestCycleE> testCycleES = testCycleE.queryAll();

        log.info("start fix all cycles data...");
        //修正所有的cycle数据
        for (TestCycleE resTestCycleE : testCycleES) {
            step2(resTestCycleE, testCycleE);
        }
    }


    //整理旧数据
    private void step1(List<IssueProjectDTO> issueProjectDTOS) {
        //旧数据放到当前项目（min version）最小版本的一个叫做旧数据的文件夹下---做旧数据的归档操作
        log.info("do archiving opration of old issue data...");
        for (IssueProjectDTO issueProjectDTO : issueProjectDTOS) {
            //根据projectId查找version
            log.info("query versions under project...");
            Long[] versionIds = testCaseService.getVersionIds(issueProjectDTO.getProjectId());
            if (ObjectUtils.isEmpty(versionIds)) {
                //无version的全删除掉
                log.info("delete issue without version...");
                testCaseService.batchDeleteIssues(issueProjectDTO.getProjectId(), issueProjectDTO.getIssueIdList());
            } else {
                for (Long versionId : versionIds) {
                    TestIssueFolderDTO tempTestIssueFolderDTO = new TestIssueFolderDTO(null, "临时", versionId, issueProjectDTO.getProjectId(), TestIssueFolderE.TYPE_TEMP, null);
                    testIssueFolderService.insert(tempTestIssueFolderDTO);
                }
                //创建文件夹
                log.info("create folder named \"旧数据\" to store old data...");
                TestIssueFolderDTO needTestIssueFolderDTO = new TestIssueFolderDTO(null, "旧数据", versionIds[0], issueProjectDTO.getProjectId(), CYCLE, null);
                TestIssueFolderDTO needFolder = testIssueFolderService.insert(needTestIssueFolderDTO);
                //有version的将他们放到目标文件夹
                List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();
                for (Long issueId : issueProjectDTO.getIssueIdList()) {
                    TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(needFolder.getFolderId(), needFolder.getVersionId(), issueProjectDTO.getProjectId(), issueId, null);
                    testIssueFolderRelDTOS.add(testIssueFolderRelDTO);
                }
                //批量修改issue的version
                log.info("change  old data into this folder...");
                testCaseService.batchIssueToVersionTest(needFolder.getProjectId(), needFolder.getVersionId(), issueProjectDTO.getIssueIdList());
                log.info("put old issue data into this folder...");
                log.info("establish relationship of old issue data and this folder...");
                testIssueFolderRelService.insertBatchRelationship(issueProjectDTO.getProjectId(), testIssueFolderRelDTOS);
            }
        }
    }

    private void step2(TestCycleE resTestCycleE, TestCycleE testCycleE) {
        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
        Long tempCycleId = resTestCycleE.getCycleId();
        log.info("start fix cycle,cycleId:" + tempCycleId);

        log.info("query project by version...");
        Long needProjectId = null;
        try {
            needProjectId = testCaseService.queryProjectIdByVersionId(resTestCycleE.getVersionId());
        } catch (DecodeException e) {
            log.info("当前Id为" + resTestCycleE.getVersionId() + "的版本已经找不到项目！请手动删除脏数据");
            return;
        }

        TestCycleE needTestCycleE = step3(resTestCycleE, testCycleE, needProjectId);

        //查询原来的cycle在cycleCase表中的数据
        log.info("query all old cycle...");
        testCycleCaseE.setCycleId(tempCycleId);
        List<TestCycleCaseE> testCycleCaseES = testCycleCaseE.querySelf();

        //用于设置folderRel数据
        List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();

        //没有case存在的话就不进行cyclecase，各个step和folderRel表的操作
        if (ObjectUtils.isEmpty(testCycleCaseES)) {
            log.info("no cycleCase exist...");
            return;
        }

        List<Long> issueIds = new ArrayList<>();
        List<Long> acceptIssues = new ArrayList<>();
        List<Long> allIssues = testCycleCaseES.stream().map(TestCycleCaseE::getIssueId).collect(Collectors.toList());

        log.info("clone issues associated with cycleCase...");
        cloneIssue(issueIds, acceptIssues, allIssues, needProjectId, needTestCycleE);

        //将原来的case关联的issue改成新克隆出来的issue，并修改各个step关系
        log.info("fix cycleCase - cycleCaseStep - caseStep data...");
        int i = 0;
        for (TestCycleCaseE cycleCaseE : testCycleCaseES) {
            //根据以前的issueId找到case_step
            log.info("query caseStep by issue...");
            TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
            testCaseStepE.setIssueId(cycleCaseE.getIssueId());
            List<TestCaseStepE> oldCaseSteps = testCaseStepE.queryByParameter();
            cycleCaseE.setCycleId(needTestCycleE.getCycleId());
            cycleCaseE.setIssueId(issueIds.get(i));
            log.info("fix cycleCase data...");
            cycleCaseE.updateSelf();

            //根据issueId克隆caseStep
            log.info("clone caseStep by issue...");
            TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
            testCaseStepDTO.setIssueId(testCaseStepE.getIssueId());
            List<TestCaseStepDTO> clonedCaseStepDTO = testCaseStepService.batchClone(testCaseStepDTO, issueIds.get(i), needProjectId);

            List<TestCycleCaseStepE> cycleCaseStepES = new ArrayList<>();
            TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
            int j = 0;
            //根据以前的case_step去将cycle_case_step更新为前面克隆得到的step
            log.info("start add cloned caseStep...");
            for (TestCaseStepE v : oldCaseSteps) {
                //查找以前stepId对应的CycleCaseStep
                log.info("query cycleCaseStep by old stepId...");
                testCycleCaseStepE.setStepId(v.getStepId());
                List<TestCycleCaseStepE> testCycleCaseStepES = testCycleCaseStepE.querySelf();
                //将CycleCaseStep对应的stepId修改为新克隆出来的stepId
                for (TestCycleCaseStepE cs : testCycleCaseStepES) {
                    if (!ObjectUtils.isEmpty(clonedCaseStepDTO)) {
                        cs.setStepId(clonedCaseStepDTO.get(j).getStepId());
                        cycleCaseStepES.add(cs);
                        log.info("executeStepId: "+cs.getExecuteStepId()+"waiting to be added");
                    }
                }
                j++;
            }
            i++;
            log.info("add batch cloned caseStep...");
            testCycleCaseStepService.update(ConvertHelper.convertList(cycleCaseStepES, TestCycleCaseStepDTO.class));
        }

        for (Long v : issueIds) {
            TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
            testIssueFolderRelDTO.setFolderId(needTestCycleE.getFolderId());
            testIssueFolderRelDTO.setProjectId(needProjectId);
            testIssueFolderRelDTO.setVersionId(needTestCycleE.getVersionId());
            testIssueFolderRelDTO.setIssueId(v);
            testIssueFolderRelDTOS.add(testIssueFolderRelDTO);
            log.info("issueId: "+v+"waiting to be added");
        }
        log.info("add batch Relationship between issueFolder and issue...");
        testIssueFolderRelService.insertBatchRelationship(needProjectId, testIssueFolderRelDTOS);
    }


    //插入folder表，更新cycle表
    public TestCycleE step3(TestCycleE resTestCycleE, TestCycleE testCycleE, Long needProjectId) {
        //用于设置IssueFolder
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();

        //设置修正数据
        log.info("correct cycle and folder data...");
        TestCycleE needTestCycleE;
        if (resTestCycleE.getType().equals("folder")) {
            //TestCycleE的type为folder的情况
            needTestCycleE = resTestCycleE;
            testIssueFolderDTO.setType(CYCLE);
        } else {
            if (resTestCycleE.getType().equals("temp")) {
                resTestCycleE.setType(CYCLE);
                log.info("change cycle which type is temp to cycle...");
                resTestCycleE.updateSelf();
            }
            resTestCycleE.setType("folder");
            resTestCycleE.setParentCycleId(resTestCycleE.getCycleId());
            resTestCycleE.setCycleId(null);
            resTestCycleE.setCycleName(resTestCycleE.getCycleName() + "阶段");
            //如果是cycle或者temp类型就新增一个cycle为其子folder
            log.info("add a child cycle for cycle...");
            needTestCycleE = resTestCycleE.addSelf();
            needTestCycleE.setObjectVersionNumber(1L);
            testIssueFolderDTO.setType(CYCLE);
        }

        testIssueFolderDTO.setProjectId(needProjectId);
        testIssueFolderDTO.setVersionId(needTestCycleE.getVersionId());

        //如果有父节点的话，将folder的名字设置为如：父名称_子名称
        log.info("change cycle name which type is folder...");
        if (needTestCycleE.getParentCycleId() != null) {
            testCycleE.setCycleId(needTestCycleE.getParentCycleId());
            log.info("query father cycle...");
            TestCycleE fatherCycleE = testCycleE.queryOne();
            //如果父名字和子名字是相同的就说明这个名字是唯一的只需要给folder设置此名即可，中间不需要加 _
            log.info("father name:" + fatherCycleE.getCycleName());
            log.info("son name:" + needTestCycleE.getCycleName());
            if (fatherCycleE.getCycleName().equals(needTestCycleE.getCycleName())) {
                testIssueFolderDTO.setName(needTestCycleE.getCycleName() + "阶段");
            } else {
                testIssueFolderDTO.setName(fatherCycleE.getCycleName() + "_" + needTestCycleE.getCycleName());
            }
            needTestCycleE.setVersionId(fatherCycleE.getVersionId());
            testIssueFolderDTO.setVersionId(fatherCycleE.getVersionId());
        }

        //插入folder表，更新cycle表
        log.info("insert issueFolder which is associated cycle...");
        Long folderId = testIssueFolderService.insert(testIssueFolderDTO).getFolderId();
        needTestCycleE.setFolderId(folderId);
        log.info("update relationship of cycle and issueFolder...");
        return needTestCycleE.updateSelf();
    }


    public void cloneIssue(List<Long> issueIds, List<Long> acceptIssues, List<Long> allIssues, Long needProjectId, TestCycleE needTestCycleE) {
        //取第一个issue
        log.info("get frist issue");
        acceptIssues.add(allIssues.get(0));
        for (int i = 1; i < allIssues.size(); i++) {
            for (int j = 0; j < acceptIssues.size(); j++) {
                if (acceptIssues.get(j).equals(allIssues.get(i))) {
                    log.info("send issues to clone");
                    issueIds.addAll(testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), acceptIssues.stream().toArray(Long[]::new)));
                    acceptIssues.clear();
                    log.info("issueId:"+allIssues.get(i)+" waiting to be sent to clone");
                    acceptIssues.add(allIssues.get(i));
                    break;
                } else if (j == acceptIssues.size() - 1) {
                    log.info("issueId:"+allIssues.get(i)+" waiting to be sent to clone");
                    acceptIssues.add(allIssues.get(i));
                    break;
                }
            }
        }
        log.info("send issues to clone");
        issueIds.addAll(testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), acceptIssues.stream().toArray(Long[]::new)));
    }
}
