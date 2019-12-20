package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Stream;

import io.choerodon.test.manager.api.vo.*;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import com.google.common.collect.Lists;

import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;


/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCycleCaseDefectRelServiceImpl implements TestCycleCaseDefectRelService {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCaseFeignClient testCaseFeignClient;

    @Autowired
    private ModelMapper modelMapper;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public TestCycleCaseDefectRelVO insert(TestCycleCaseDefectRelVO testCycleCaseDefectRelVO, Long projectId, Long organizationId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = modelMapper
                .map(testCycleCaseDefectRelVO, TestCycleCaseDefectRelDTO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseDefectRelMapper::insert, testCycleCaseDefectRelDTO, 1, "error.defect.insert");
        return modelMapper.map(testCycleCaseDefectRelDTO, TestCycleCaseDefectRelVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(TestCycleCaseDefectRelVO testCycleCaseDefectRelVO, Long projectId, Long organizationId) {
        testCycleCaseDefectRelMapper.delete(modelMapper.map(testCycleCaseDefectRelVO, TestCycleCaseDefectRelDTO.class));
    }

    @Override
    public void populateDefectInfo(List<TestCycleCaseDefectRelVO> lists, Long projectId, Long organizationId) {
        if (ObjectUtils.isEmpty(lists)) {
            return;
        }
        Long[] issueLists = lists.stream().map(TestCycleCaseDefectRelVO::getIssueId).filter(Objects::nonNull).distinct().toArray(Long[]::new);
        Map<Long, IssueInfosVO> defectMap = testCaseService.getIssueInfoMap(projectId, issueLists, false, organizationId);
        lists.forEach(v -> v.setIssueInfosVO(defectMap.get(v.getIssueId())));
    }

    @Override
    public void populateDefectAndIssue(TestCycleCaseVO dto, Long projectId, Long organizationId) {
        Stream<Long> stream = Stream.of(dto.getIssueId());
        if (!ObjectUtils.isEmpty(dto.getDefects())) {
            stream = Stream.concat(stream, dto.getDefects().stream().map(TestCycleCaseDefectRelVO::getIssueId));
        }
        Long[] issueLists = stream.filter(Objects::nonNull).distinct().toArray(Long[]::new);
        Map<Long, IssueInfosVO> defectMap = testCaseService.getIssueInfoMap(projectId, issueLists, true, organizationId);
        dto.setIssueInfosVO(defectMap.get(dto.getIssueId()));
        Optional.ofNullable(dto.getDefects()).ifPresent(v -> v.forEach(u -> u.setIssueInfosVO(defectMap.get(u.getIssueId()))));
    }

    @Override
    public void populateCycleCaseDefectInfo(List<TestCycleCaseVO> testCycleCaseVOS, Long projectId, Long organizationId) {
        List<TestCycleCaseDefectRelVO> list = new ArrayList<>();
        for (TestCycleCaseVO v : testCycleCaseVOS) {
            List<TestCycleCaseDefectRelVO> defects = v.getDefects();
            list.addAll(defects);
        }
        populateDefectInfo(list, projectId, organizationId);
    }

    @Override
    public void populateCaseStepDefectInfo(List<TestCycleCaseStepVO> testCycleCaseDTOS, Long projectId, Long organizationId) {
        List<TestCycleCaseDefectRelVO> list = new ArrayList<>();
        for (TestCycleCaseStepVO v : testCycleCaseDTOS) {
            List<TestCycleCaseDefectRelVO> defects = v.getDefects();
            list.addAll(defects);
        }
        populateDefectInfo(list, projectId, organizationId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean updateIssuesProjectId(TestCycleCaseDefectRelVO testCycleCaseDefectRelVO, Long organizationId) {
        List<Long> issueIds = testCycleCaseDefectRelMapper.queryAllIssueIds();
        List<List<Long>> handledIssueIds = Lists.partition(issueIds, 50);
        Map<Long, IssueInfosVO> issueInfoMap;
        Boolean flag = false;
        for (List<Long> toSendIssueId : handledIssueIds) {
            Long[] tempIssueId = toSendIssueId.toArray(new Long[toSendIssueId.size()]);
            issueInfoMap = testCaseService.getIssueInfoMap(testCycleCaseDefectRelVO.getProjectId(), tempIssueId, false, organizationId);

            for (Long id : toSendIssueId) {
                IssueInfosVO issueInfosVO = issueInfoMap.get(id);
                TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
                testCycleCaseDefectRelDTO.setProjectId(issueInfosVO.getProjectId());
                testCycleCaseDefectRelDTO.setIssueId(id);
                flag = updateProjectIdByIssueId(testCycleCaseDefectRelDTO);
            }
        }
        return flag;
    }

    private Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
        int count = testCycleCaseDefectRelMapper.updateProjectIdByIssueId(testCycleCaseDefectRelDTO);
        if (log.isDebugEnabled()) {
            log.debug("fix defect data issueID {} updates num {}", testCycleCaseDefectRelDTO.getIssueId(), count);
        }
        return true;
    }

//    @Override
//    public List<TestCycleCaseVO> queryByBug(Long projectId, Long bugId) {
//        List<TestCycleCaseDTO> res = testCycleCaseDefectRelMapper.queryByBug(projectId, bugId);
//        if (res != null && !res.isEmpty()) {
////            List<Long> issueIds = res.stream().map(TestCycleCaseDTO::getCaseId).collect(Collectors.toList());
////            Map<Long, String> issueMap = testCaseFeignClient.listByIssueIds(projectId, issueIds).getBody().stream().collect(Collectors.toMap(IssueInfoDTO::getIssueId, IssueInfoDTO::getSummary));
////            List<TestCycleCaseVO> testCycleCaseVOList = new ArrayList<>();
////            res.forEach(testCycleCaseDTO -> {
////                TestCycleCaseVO testCycleCaseVO = modelMapper.map(testCycleCaseDTO, TestCycleCaseVO.class);
////                testCycleCaseVO.setSummary(issueMap.get(testCycleCaseVO.getIssueId()));
////                testCycleCaseVOList.add(testCycleCaseVO);
////            });
//            return modelMapper.map(res, new TypeToken<List<TestCycleCaseVO>>() {}.getType());
//        } else {
//            return new ArrayList<>();
//        }
//    }

    @Override
    public void deleteCaseRel(Long project,Long defectId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setIssueId(defectId);
        testCycleCaseDefectRelDTO.setProjectId(project);
        List<TestCycleCaseDefectRelDTO> testCycleCaseDefectRelDTOS = testCycleCaseDefectRelMapper.select(testCycleCaseDefectRelDTO);
        if(!CollectionUtils.isEmpty(testCycleCaseDefectRelDTOS)){
            testCycleCaseDefectRelMapper.delete(testCycleCaseDefectRelDTO);
        }
    }

    @Override
    public void cloneDefect(Map<Long, Long> caseIdMap, List<Long> olderExecuteIds,String type) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        List<TestCycleCaseDefectRelDTO> list = testCycleCaseDefectRelMapper.listByExecuteIds(olderExecuteIds,type);
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(v -> {
            v.setDefectLinkId(caseIdMap.get(v.getDefectLinkId()));
            v.setCreatedBy(userDetails.getUserId());
            v.setLastUpdatedBy(userDetails.getUserId());
        });
        testCycleCaseDefectRelMapper.batchInsert(list);
    }
}
