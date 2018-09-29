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
        TestCycleCaseE testCycleCaseE = TestCycleCaseEFactory.create();

        //从敏捷查询所有type是issue_test的issue
        log.info("query all issue data under project...: ");
        List<IssueProjectDTO> issueProjectDTOS = testCaseService.queryIssueTestGroupByProject(projectId);
//        if(Thread.interrupted()){
//            return;
//        }
        //根据projectId查找version
        for (IssueProjectDTO issueProjectDTO : issueProjectDTOS) {
            //旧数据放到当前项目（min version）最小版本的一个叫做旧数据的文件夹下---做旧数据的归档操作
            log.info("query versions under project...");
            Long[] versionIds = testCaseService.getVersionIds(issueProjectDTO.getProjectId());
            if (ObjectUtils.isEmpty(versionIds)) {
                //无version的全删除掉
                log.info("delete issue without version under project...");
//                testCaseService.batchDeleteIssues(issueProjectDTO.getProjectId(), issueProjectDTO.getIssueIdList());
            } else {
                //创建文件夹
                log.info("insert folder to store old data under project...");
                TestIssueFolderDTO needTestIssueFolderDTO = new TestIssueFolderDTO(null, "旧数据", versionIds[0], issueProjectDTO.getProjectId(), CYCLE, null);
                TestIssueFolderDTO needFolder = testIssueFolderService.insert(needTestIssueFolderDTO);
                //有version的将他们放到目标文件夹
                List<TestIssueFolderRelDTO> testIssueFolderRelDTOS = new ArrayList<>();
                for (Long issueId : issueProjectDTO.getIssueIdList()) {
                    TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO(needFolder.getFolderId(), needFolder.getVersionId(), issueProjectDTO.getProjectId(), issueId, null);
                    testIssueFolderRelDTOS.add(testIssueFolderRelDTO);
                }
                //批量修改issue的version
                testCaseService.batchIssueToVersionTest(needFolder.getProjectId(), needFolder.getVersionId(), issueProjectDTO.getIssueIdList());
                log.info("establish relationship of issue and folder under project...");
                testIssueFolderRelService.insertBatchRelationship(issueProjectDTO.getProjectId(), testIssueFolderRelDTOS);
            }
        }

        log.info("query all cycles...");
        List<TestCycleE> testCycleES = testCycleE.queryAll();
        //用于设置IssueFolder
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();

        //用于设置每个version下的临时文件夹
        TestIssueFolderDTO tempTestIssueFolderDTO = new TestIssueFolderDTO(null, "临时", null, null, TestIssueFolderE.TYPE_TEMP, null);

        //修正所有的cycle数据
        for (TestCycleE resTestCycleE : testCycleES) {
            Long tempCycleId = resTestCycleE.getCycleId();
            log.info("cycleId" + tempCycleId);
            String tempCycleType = resTestCycleE.getType();

            log.info("query project by version...");
            Long needProjectId = null;
            try {
                needProjectId = testCaseService.queryProjectIdByVersionId(resTestCycleE.getVersionId());
            } catch (DecodeException e) {
                log.info("当前Id为" + resTestCycleE.getVersionId() + "的版本已经找不到项目！请手动删除脏数据");
                continue;
            }
            //设置修正数据
            TestCycleE needTestCycleE;
            if (resTestCycleE.getType().equals("folder")) {
                needTestCycleE = resTestCycleE;
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
                resTestCycleE.setCycleName(resTestCycleE.getCycleName() + "阶段");
                //如果是cycle或者temp类型就新增一个cycle为其子folder
                resTestCycleE.queryAll();
                log.info("cycleId:" + resTestCycleE.getCycleId());
                needTestCycleE = resTestCycleE.addSelf();
                needTestCycleE.setObjectVersionNumber(1L);
                testIssueFolderDTO.setType(CYCLE);
            }

            testIssueFolderDTO.setProjectId(needProjectId);
            testIssueFolderDTO.setVersionId(needTestCycleE.getVersionId());

            tempTestIssueFolderDTO.setProjectId(needProjectId);
            tempTestIssueFolderDTO.setVersionId(needTestCycleE.getVersionId());

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
                tempTestIssueFolderDTO.setVersionId(fatherCycleE.getVersionId());
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

//            Long[] cycleCaseIssueIds= testCycleCaseES.stream().map(TestCycleCaseE::getIssueId).toArray(Long[]::new);
//            Arrays.sort(cycleCaseIssueIds);
//
//            List<Long> noRepeatIssue = new ArrayList<>();
//            Map<Long,Integer> repeatIssue = new HashMap<>();
//
//            for(int i=1;i<cycleCaseIssueIds.length;i++){
//                if(cycleCaseIssueIds[i].equals(noRepeatIssue.get(noRepeatIssue.size()-1))){
//                    noRepeatIssue.add(cycleCaseIssueIds[i]);
//                }else{
//                    int flag = (Integer) repeatIssue.get(cycleCaseIssueIds[i]);
//                    repeatIssue.put(cycleCaseIssueIds[i],++flag);
//                }
//            }
//
//            noRepeatIssue.forEach(v->{
//
//            });
            List<Long> issueIds = new ArrayList<>();
            List<Long> acceptIssues = new ArrayList<>();
            List<Long> allIssues = testCycleCaseES.stream().map(TestCycleCaseE::getIssueId).collect(Collectors.toList());
            //取第一个issue
            acceptIssues.add(allIssues.get(0));
            for (int i = 1; i < allIssues.size(); i++) {
                for (int j = 0; j < acceptIssues.size(); j++) {
                    if (acceptIssues.get(j).equals(allIssues.get(i))) {
                        issueIds.addAll(testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), acceptIssues.stream().toArray(Long[]::new)));
                        acceptIssues.clear();
                        acceptIssues.add(allIssues.get(i));
                        break;
                    } else if (j == acceptIssues.size() - 1) {
                        acceptIssues.add(allIssues.get(i));
                    }
                }
            }
            issueIds.addAll(testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), acceptIssues.stream().toArray(Long[]::new)));
//            else {
//                log.info("issues " + allIssues.stream().toArray(Long[]::new)[0]);
//                issueIds = testCaseService.batchCloneIssue(needProjectId, needTestCycleE.getVersionId(), allIssues.stream().toArray(Long[]::new));
//            }

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
                testCaseStepDTO.setIssueId(testCaseStepE.getIssueId());
                List<TestCaseStepDTO> clonedCaseStepDTO = testCaseStepService.batchClone(testCaseStepDTO, issueIds.get(i), needProjectId);

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
                i++;
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
        //插入folder表，为每个version加一个叫做临时的文件夹
//        TestIssueFolderE testIssueFolderE = TestIssueFolderEFactory.create();
//        testIssueFolderE.setProjectId(44L);
//        List<TestIssueFolderE> t = testIssueFolderE.queryAllUnderProject();
        testIssueFolderService.insert(tempTestIssueFolderDTO);
    }

}
