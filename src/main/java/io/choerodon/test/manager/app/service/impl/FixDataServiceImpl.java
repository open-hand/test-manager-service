package io.choerodon.test.manager.app.service.impl;

import feign.FeignException;
import feign.codec.DecodeException;

import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.*;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.test.manager.domain.test.manager.entity.*;
import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.exception.FeignReceiveException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zongw.lee@gmail.com
 */
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

    @Autowired
    TestCycleRepository testCycleRepository;

    private static final String CYCLE = "cycle";

    Log log = LogFactory.getLog(this.getClass());


    @Override
    public void fixCycleData(Long projectId) {
        log.info("start fix data...now thread :" + Thread.currentThread().getName());
        TestCycleE testCycleE = TestCycleEFactory.create();

        //从敏捷查询所有type是issue_test的issue
        log.info("query all issue data under project...: ");
        List<IssueProjectDTO> issueProjectDTOS;
        try {
            issueProjectDTOS = testCaseService.queryIssueTestGroupByProject(projectId);
        } catch (FeignException e) {
            throw new FeignReceiveException("query all issues group by project error", e);
        }
        step1(issueProjectDTOS);

        log.info("query all cycles...");
        List<TestCycleE> testCycleES = testCycleE.queryAll();

        log.info("start fix all cycles data...");
        //修正所有的cycle数据
        for (TestCycleE resTestCycleE : testCycleES) {
            step2(resTestCycleE, testCycleE);
        }
        log.info("fix data successful");
    }


    //整理旧数据
    private void step1(List<IssueProjectDTO> issueProjectDTOS) {
        //旧数据放到当前项目（min version）最小版本的一个叫做旧数据的文件夹下---做旧数据的归档操作
        log.info("do archiving opration of old issue data...");
        for (IssueProjectDTO issueProjectDTO : issueProjectDTOS) {
            //根据projectId查找version
            log.info("query versions under project... projectId:" + issueProjectDTO.getProjectId());
            Long[] versionIds;
            try {
                versionIds = testCaseService.getVersionIds(issueProjectDTO.getProjectId());
            } catch (FeignException e) {
                throw new FeignReceiveException("query versions under project error, projectId:{}", issueProjectDTO.getProjectId(), e);
            }
            if (ObjectUtils.isEmpty(versionIds)) {
                //无version的全删除掉
                log.info("delete issues without version...");
                try {
                    testCaseService.batchDeleteIssues(issueProjectDTO.getProjectId(), issueProjectDTO.getIssueIdList());
                } catch (FeignException e) {
                    throw new FeignReceiveException("delete issues under project error, projectId:{}", issueProjectDTO.getProjectId(), e);
                }
            } else {
                storeOldData(versionIds,issueProjectDTO);
            }
        }
    }

    private void storeOldData(Long[] versionIds,IssueProjectDTO issueProjectDTO){
        for (Long versionId : versionIds) {
            TestIssueFolderDTO tempTestIssueFolderDTO = new TestIssueFolderDTO(null, "临时", versionId, issueProjectDTO.getProjectId(), TestIssueFolderE.TYPE_TEMP, null);
            TestIssueFolderDTO resFolderDTO = testIssueFolderService.insert(tempTestIssueFolderDTO);
            log.info("create folder named \"临时\" for version... versionId:" + versionId + " folderId:" + resFolderDTO.getFolderId());
        }
        //创建文件夹
        TestIssueFolderDTO needTestIssueFolderDTO = new TestIssueFolderDTO(null, "旧数据", versionIds[0], issueProjectDTO.getProjectId(), CYCLE, null);
        TestIssueFolderDTO needFolder = testIssueFolderService.insert(needTestIssueFolderDTO);
        log.info("create folder named \"旧数据\" to store old data... folderId:" + needFolder.getFolderId());
        //有version的将他们放到目标文件夹
        List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();
        for (Long issueId : issueProjectDTO.getIssueIdList()) {
            TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(needFolder.getFolderId(), needFolder.getVersionId(), issueProjectDTO.getProjectId(), issueId, null);
            testIssueFolderRelDTOS.add(testIssueFolderRelDTO);
        }
        //批量修改issue的version
        log.info("change old data into this folder...");
        try {
            testCaseService.batchIssueToVersionTest(needFolder.getProjectId(), needFolder.getVersionId(), issueProjectDTO.getIssueIdList());
        } catch (FeignException e) {
            throw new FeignReceiveException("change issues under project's version error, projectId:{},version:{}", needFolder.getProjectId(), needFolder.getVersionId(), e);
        }
        log.info("put old issue data into this folder...");
        log.info("establish relationship of old issue data and this folder... folderId:" + needFolder.getFolderId());
        testIssueFolderRelService.insertBatchRelationship(issueProjectDTO.getProjectId(), testIssueFolderRelDTOS);
    }

    private void step2(TestCycleE resTestCycleE, TestCycleE testCycleE) {
        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();
        Long tempCycleId = resTestCycleE.getCycleId();
        log.info("start fix cycle,cycleId:" + tempCycleId);

        log.info("query project by version... versionId:" + resTestCycleE.getVersionId());
        Long needProjectId = null;
        try {
            needProjectId = testCaseService.queryProjectIdByVersionId(resTestCycleE.getVersionId());
        } catch (DecodeException e) {
            throw new FeignReceiveException("versionId：{}，this version can not find project! please delete dirty data manually", resTestCycleE.getVersionId(), e);
        }

        TestCycleE needTestCycleE = step3(resTestCycleE, testCycleE, needProjectId);

        //查询原来的cycle在cycleCase表中的数据
        log.info("query all old cycleCase of cycle... cyclyId:" + tempCycleId);
        testCycleCaseE.setCycleId(tempCycleId);
        List<TestCycleCaseE> testCycleCaseES = testCycleCaseE.querySelf();

        //用于设置folderRel数据
        List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();

        //没有case存在的话就不进行cyclecase，各个step和folderRel表的操作
        if (ObjectUtils.isEmpty(testCycleCaseES)) {
            log.info("no cycleCase exist in cycle... cycleId:" + tempCycleId);
            return;
        }

        List<Long> issueIds = new ArrayList<>();
        List<Long> acceptIssues = new ArrayList<>();
        List<Long> allIssues = testCycleCaseES.stream().map(TestCycleCaseE::getIssueId).collect(Collectors.toList());

        log.info("clone issues associated with cycleCases of cycle... cycleId:" + tempCycleId);
        cloneIssue(issueIds, acceptIssues, allIssues, needProjectId, needTestCycleE);

        //将原来的case关联的issue改成新克隆出来的issue，并修改各个step关系
        log.info("fix cycleCase - cycleCaseStep - caseStep data...");
        int i = 0;
        for (TestCycleCaseE cycleCaseE : testCycleCaseES) {
            //根据以前的issueId找到case_step
            log.info("query caseStep by issue... issueId:" + cycleCaseE.getIssueId());
            TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
            testCaseStepE.setIssueId(cycleCaseE.getIssueId());
            List<TestCaseStepE> oldCaseSteps = testCaseStepE.queryByParameter();
            cycleCaseE.setCycleId(needTestCycleE.getCycleId());
            cycleCaseE.setIssueId(issueIds.get(i));
            log.info("fix cycleCase data... executeId:" + cycleCaseE.getExecuteId());
            cycleCaseE.updateSelf();

            //根据issueId克隆caseStep
            log.info("clone caseStep by issue... issueId:" + testCaseStepE.getIssueId());
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
                log.info("query cycleCaseStep by old caseStep... caseStepId:" + v.getStepId());
                testCycleCaseStepE.setStepId(v.getStepId());
                List<TestCycleCaseStepE> testCycleCaseStepES = testCycleCaseStepE.querySelf();
                //将CycleCaseStep对应的stepId修改为新克隆出来的stepId
                for (TestCycleCaseStepE cs : testCycleCaseStepES) {
                    if (!ObjectUtils.isEmpty(clonedCaseStepDTO)) {
                        cs.setStepId(clonedCaseStepDTO.get(j).getStepId());
                        cycleCaseStepES.add(cs);
                        log.info("executeStepId: " + cs.getExecuteStepId() + "waiting to be added");
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
            log.info("issueId: " + v + "waiting to be added");
        }
        log.info("add batch Relationship between issueFolder and issue...");
        testIssueFolderRelService.insertBatchRelationship(needProjectId, testIssueFolderRelDTOS);
    }


    //插入folder表，更新cycle表
    public TestCycleE step3(TestCycleE resTestCycleE, TestCycleE testCycleE, Long needProjectId) {
        //用于设置IssueFolder
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();

        //设置修正数据
        log.info("start to correct cycle and folder data...");
        TestCycleE needTestCycleE;
        if (resTestCycleE.getType().equals("folder")) {
            //TestCycleE的type为folder的情况
            needTestCycleE = resTestCycleE;
            testIssueFolderDTO.setType(CYCLE);
        } else {
            if (resTestCycleE.getType().equals("temp")) {
                resTestCycleE.setType(CYCLE);
                log.info("change cycle's type to cycle... cycleId:" + resTestCycleE.getCycleId());
                resTestCycleE.updateSelf();
            }
            resTestCycleE.setType("folder");
            resTestCycleE.setParentCycleId(resTestCycleE.getCycleId());
            resTestCycleE.setCycleId(null);
            resTestCycleE.setCycleName(resTestCycleE.getCycleName() + "阶段");
            //如果是cycle或者temp类型就新增一个cycle为其子folder
            Long parentCycleId = resTestCycleE.getCycleId();
            resTestCycleE.checkRank();
            testCycleE.setRank(RankUtil.Operation.INSERT.getRank(testCycleRepository.getLastedRank(testCycleE), null));
            needTestCycleE = resTestCycleE.addSelf();
            log.info("add a child cycle for cycle... parentCycleId:" + parentCycleId + " , childCycleId:" + needTestCycleE.getCycleId());
            needTestCycleE.setObjectVersionNumber(1L);
            testIssueFolderDTO.setType(CYCLE);
        }

        testIssueFolderDTO.setProjectId(needProjectId);
        testIssueFolderDTO.setVersionId(needTestCycleE.getVersionId());

        //如果有父节点的话，将folder的名字设置为如：父名称_子名称
        if (needTestCycleE.getParentCycleId() != null) {
            log.info("change cycle's name which's type is folder... cycleId:" + needTestCycleE.getCycleId());
            testCycleE.setCycleId(needTestCycleE.getParentCycleId());
            log.info("query father cycle...");
            TestCycleE fatherCycleE = testCycleE.queryOne();
            //如果父名字和子名字是相同的就说明这个名字是唯一的只需要给folder设置此名即可，中间不需要加 _
            log.info("father name:" + fatherCycleE.getCycleName() + "father Id" + fatherCycleE.getCycleId());
            log.info("son name:" + needTestCycleE.getCycleName() + "son Id" + needTestCycleE.getCycleId());
            if (fatherCycleE.getCycleName().equals(needTestCycleE.getCycleName())) {
                testIssueFolderDTO.setName(needTestCycleE.getCycleName() + "阶段");
            } else {
                testIssueFolderDTO.setName(fatherCycleE.getCycleName() + "_" + needTestCycleE.getCycleName());
            }
            needTestCycleE.setVersionId(fatherCycleE.getVersionId());
            testIssueFolderDTO.setVersionId(fatherCycleE.getVersionId());
        }

        //插入folder表，更新cycle表
        Long folderId = testIssueFolderService.insert(testIssueFolderDTO).getFolderId();
        log.info("insert issueFolder which is associated cycle... cycleId:" + needTestCycleE.getCycleId() + " , folderId:" + folderId);
        needTestCycleE.setFolderId(folderId);
        log.info("update relationship of cycle and issueFolder... cycleId:" + needTestCycleE.getCycleId());
        return needTestCycleE.updateSelf();
    }


    public void cloneIssue(List<Long> issueIds, List<Long> acceptIssues, List<Long> allIssues, Long needProjectId, TestCycleE needTestCycleE) {
        //取第一个issue
        log.info("get frist issue... issueId:" + allIssues.get(0));
        acceptIssues.add(allIssues.get(0));
        for (int i = 1; i < allIssues.size(); i++) {
            for (int j = 0; j < acceptIssues.size(); j++) {
                if (acceptIssues.get(j).equals(allIssues.get(i))) {
                    log.info("send issues to clone");
                    try {
                        issueIds.addAll(testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), acceptIssues.stream().toArray(Long[]::new)));
                    } catch (FeignException e) {
                        throw new FeignReceiveException("clone issues under project's version error, projectId:{},versionId:{}", needProjectId, needTestCycleE.getVersionId(), e);
                    }
                    acceptIssues.clear();
                    log.info("issueId:" + allIssues.get(i) + " waiting to be sent to clone");
                    acceptIssues.add(allIssues.get(i));
                    break;
                } else if (j == acceptIssues.size() - 1) {
                    log.info("issueId:" + allIssues.get(i) + " waiting to be sent to clone");
                    acceptIssues.add(allIssues.get(i));
                    break;
                }
            }
        }
        log.info("send issues to clone");
        try {
            issueIds.addAll(testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), acceptIssues.stream().toArray(Long[]::new)));
        } catch (FeignException e) {
            throw new FeignReceiveException("clone issues under project's version error, projectId:{},versionId:{}", needProjectId, needTestCycleE.getVersionId(), e);
        }
    }
}
