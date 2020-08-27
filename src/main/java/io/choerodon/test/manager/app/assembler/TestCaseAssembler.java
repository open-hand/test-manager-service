package io.choerodon.test.manager.app.assembler;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.TestCaseLinkService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.mapper.*;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author zhaotianxin
 * @since 2019/11/22
 */
@Component
public class TestCaseAssembler {


    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TestCaseLinkService testCaseLinkService;

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Autowired
    private TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestPriorityMapper testPriorityMapper;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    private static final String TYPE = "CYCLE_CASE";
    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public TestCaseRepVO dtoToRepVo(TestCaseDTO testCaseDTO, Map<Long, TestIssueFolderDTO> folderMap) {
        Map<Long, UserMessageDTO> map = getUserMap(testCaseDTO, null);
        TestCaseRepVO testCaseRepVO = new TestCaseRepVO();
        modelMapper.map(testCaseDTO, testCaseRepVO);
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(testCaseDTO.getProjectId());
        testCaseRepVO.setCaseNum(getIssueNum(testCaseDTO.getProjectId(), testCaseDTO.getCaseNum()));
        testCaseRepVO.setCreateUser(map.get(testCaseDTO.getCreatedBy()));
        testCaseRepVO.setLastUpdateUser(map.get(testCaseDTO.getLastUpdatedBy()));
        testCaseRepVO.setFolderName(folderMap.get(testCaseDTO.getFolderId()).getName());
        return testCaseRepVO;
    }

    public TestFolderCycleCaseVO setAssianUser(TestCycleCaseDTO testCycleCaseDTO,Map<Long, UserMessageDTO> userMap) {
        TestFolderCycleCaseVO testFolderCycleCaseVO = modelMapper.map(testCycleCaseDTO, TestFolderCycleCaseVO.class);
        Long assignedTo = testCycleCaseDTO.getAssignedTo();
        if (assignedTo != null && !Objects.equals(assignedTo, 0L)) {
            UserMessageDTO userMessage = userMap.get(assignedTo);
            if(ObjectUtils.isEmpty(userMessage)){
                TestCycleCaseDTO cycleCaseDTO = new TestCycleCaseDTO();
                cycleCaseDTO.setCreatedBy(testCycleCaseDTO.getAssignedTo());
                Map<Long, UserMessageDTO> userMap1 = getUserMap(cycleCaseDTO, null);
                testFolderCycleCaseVO.setAssignedUser(userMap1.get(assignedTo));
                userMap.putAll(userMap1);
            }
            else {
                testFolderCycleCaseVO.setAssignedUser(userMessage);
            }
        }
        UserMessageDTO updateUser = userMap.get(testCycleCaseDTO.getLastUpdatedBy());
        if(!ObjectUtils.isEmpty(updateUser)){
            testFolderCycleCaseVO.setLastUpdateUser(updateUser);
        }
        return testFolderCycleCaseVO;
    }

    public List<TestCaseRepVO> listDtoToRepVo(Long projectId, List<TestCaseDTO> list, Long planId) {
        List<TestIssueFolderDTO> testIssueFolderDTOS = testIssueFolderMapper.selectListByProjectId(projectId);
        Map<Long, TestIssueFolderDTO> folderMap = testIssueFolderDTOS.stream().collect(Collectors.toMap(TestIssueFolderDTO::getFolderId, Function.identity()));
        List<Long> caseIds = null;
        if (!ObjectUtils.isEmpty(planId)) {
            caseIds = testCycleCaseMapper.listByPlanId(planId);
        }
        List<Long> finalCaseIds = caseIds;
        List<TestCaseRepVO> collect = list.stream()
                .map(v -> {
                    TestCaseRepVO testCaseRepVO = dtoToRepVo(v, folderMap);
                    if (!CollectionUtils.isEmpty(finalCaseIds)) {
                        if (finalCaseIds.contains(v.getCaseId())) {
                            testCaseRepVO.setHasDisable(true);
                        } else {
                            testCaseRepVO.setHasDisable(false);
                        }
                    }
                    return testCaseRepVO;
                }).collect(Collectors.toList());
        return collect;
    }


    public TestCaseInfoVO dtoToInfoVO(TestCaseDTO testCaseDTO) {
        TestCaseInfoVO testCaseInfoVO = modelMapper.map(testCaseDTO, TestCaseInfoVO.class);
        // 获取用户信息
        Map<Long, UserMessageDTO> userMessageDTOMap = getUserMap(testCaseDTO, null);
        if (!ObjectUtils.isEmpty(userMessageDTOMap.get(testCaseDTO.getCreatedBy()))) {
            testCaseInfoVO.setCreateUser(userMessageDTOMap.get(testCaseDTO.getCreatedBy()));
        }
        if (!ObjectUtils.isEmpty(userMessageDTOMap.get(testCaseDTO.getCreatedBy()))) {
            testCaseInfoVO.setLastUpdateUser(userMessageDTOMap.get(testCaseDTO.getLastUpdatedBy()));
        }
        // 用例的问题链接
        testCaseInfoVO.setIssuesInfos(testCaseLinkService.listIssueInfo(testCaseDTO.getProjectId(), testCaseDTO.getCaseId()));
        // 查询附件信息
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        testCaseAttachmentDTO.setCaseId(testCaseDTO.getCaseId());
        List<TestCaseAttachmentDTO> attachment = testAttachmentMapper.select(testCaseAttachmentDTO);
        if (!CollectionUtils.isEmpty(attachment)) {
            attachment.forEach(v -> v.setUrl(attachmentUrl + v.getUrl()));
            testCaseInfoVO.setAttachment(attachment);
        }
        // 查询测试用例所属的文件夹
        TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(testCaseDTO.getFolderId());
        if (!ObjectUtils.isEmpty(testIssueFolderDTO)) {
            testCaseInfoVO.setFolder(testIssueFolderDTO.getName());
        }
        testCaseInfoVO.setCaseNum(getIssueNum(testCaseDTO.getProjectId(), testCaseDTO.getCaseNum()));
        // 查询用例的优先级信息
        TestPriorityDTO priorityDTO = testPriorityMapper.selectByPrimaryKey(testCaseInfoVO.getPriorityId());
        PriorityVO priorityVO = new PriorityVO();
        BeanUtils.copyProperties(priorityDTO, priorityVO);
        testCaseInfoVO.setPriorityVO(priorityVO);
        return testCaseInfoVO;
    }

    private String getIssueNum(Long projectId, String caseNum) {
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(projectId);
        TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
        if (StringUtils.startsWith(caseNum,testProjectInfo.getProjectCode() + BaseConstants.Symbol.MIDDLE_LINE)){
            return caseNum;
        }
        return String.format("%s-%s", testProjectInfo.getProjectCode(), caseNum);
    }

    public <T> Map<Long, UserMessageDTO> getUserMap(T t, List<T> list) {
        List<Long> userIds = new ArrayList<>();
        if (!ObjectUtils.isEmpty(t)) {
            AuditDomain auditDomain = (AuditDomain) t;
            if (!ObjectUtils.isEmpty(auditDomain.getCreatedBy())) {
                userIds.add(auditDomain.getCreatedBy());
            }
            if (!ObjectUtils.isEmpty(auditDomain.getLastUpdatedBy())) {
                userIds.add(auditDomain.getLastUpdatedBy());
            }
        }
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(v -> {
                AuditDomain auditDomain = (AuditDomain) v;
                if (!ObjectUtils.isEmpty(auditDomain.getCreatedBy())) {
                    userIds.add(auditDomain.getCreatedBy());
                }
                if (!ObjectUtils.isEmpty(auditDomain.getLastUpdatedBy())) {
                    userIds.add(auditDomain.getLastUpdatedBy());
                }
            });
        }
        Map<Long, UserMessageDTO> userMessageDTOMap = userService.queryUsersMap(userIds);
        return userMessageDTOMap;
    }

    public TestCycleCaseInfoVO cycleCaseExtraInfo(TestCycleCaseInfoVO testCycleCaseInfoVO) {
        if (testCycleCaseInfoVO.getAssignedTo() != null && !Objects.equals(testCycleCaseInfoVO.getAssignedTo(), 0L)) {
            TestCycleDTO testCycleDTO = new TestCycleDTO();
            testCycleDTO.setCreatedBy(testCycleCaseInfoVO.getAssignedTo());
            Map<Long, UserMessageDTO> userMessageDTOMap = getUserMap(testCycleDTO, null);
            if (!ObjectUtils.isEmpty(userMessageDTOMap.get(testCycleCaseInfoVO.getAssignedTo()))) {
                testCycleCaseInfoVO.setExecutor(userMessageDTOMap.get(testCycleCaseInfoVO.getAssignedTo()));
            }
        }
        // 查询附件信息
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setAttachmentLinkId(testCycleCaseInfoVO.getExecuteId());
        testCycleCaseAttachmentRelDTO.setAttachmentType(TYPE);
        List<TestCycleCaseAttachmentRelDTO> testCycleCaseAttachmentRelDTOS = testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO);
        testCycleCaseInfoVO.setAttachment(modelMapper.map(testCycleCaseAttachmentRelDTOS, new TypeToken<List<TestCycleCaseAttachmentRelVO>>() {
        }.getType()));
        // 查询用例信息
        if(!ObjectUtils.isEmpty(testCycleCaseInfoVO.getCaseId())){
            Boolean hasExist = true;
            TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(testCycleCaseInfoVO.getCaseId());
            if(ObjectUtils.isEmpty(testCaseDTO)){
                hasExist = false;
            }else {
                testCycleCaseInfoVO.setCaseNum(getIssueNum(testCaseDTO.getProjectId(), testCaseDTO.getCaseNum()));
                testCycleCaseInfoVO.setCaseFolderId(testCaseDTO.getFolderId());
            }
            testCycleCaseInfoVO.setCaseHasExist(hasExist);
        }
        // 查询用例的优先级信息
        TestPriorityDTO priorityDTO = testPriorityMapper.selectByPrimaryKey(testCycleCaseInfoVO.getPriorityId());
        PriorityVO priorityVO = new PriorityVO();
        BeanUtils.copyProperties(priorityDTO, priorityVO);
        testCycleCaseInfoVO.setPriorityVO(priorityVO);
        return testCycleCaseInfoVO;
    }

    public TestCycleCaseUpdateVO dtoToUpdateVO(TestCycleCaseDTO testCycleCaseDTO) {
        if (ObjectUtils.isEmpty(testCycleCaseDTO)) {
            throw new CommonException("error.cycle.case.null");
        }
        TestCycleCaseUpdateVO testCycleCaseUpdateVO = modelMapper.map(testCycleCaseDTO, TestCycleCaseUpdateVO.class);
        List<TestCycleCaseStepDTO> cycleCaseStep = testCycleCaseDTO.getCycleCaseStep();
        List<TestCycleCaseStepUpdateVO> stepUpdateVOS = modelMapper.map(cycleCaseStep, new TypeToken<List<TestCycleCaseStepUpdateVO>>() {
        }.getType());
        testCycleCaseUpdateVO.setTestCycleCaseStepUpdateVOS(stepUpdateVOS);
        // 查询附件信息
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setAttachmentLinkId(testCycleCaseDTO.getExecuteId());
        testCycleCaseAttachmentRelDTO.setAttachmentType(TYPE);
        List<TestCycleCaseAttachmentRelDTO> testCycleCaseAttachmentRelDTOS = testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO);
        testCycleCaseUpdateVO.setCycleCaseAttachmentRelVOList(modelMapper.map(testCycleCaseAttachmentRelDTOS, new TypeToken<List<TestCycleCaseAttachmentRelVO>>() {
        }.getType()));
        Boolean hasExist = true;
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(testCycleCaseDTO.getCaseId());
        if (ObjectUtils.isEmpty(testCycleCaseDTO.getCaseId()) || ObjectUtils.isEmpty(testCaseDTO)) {
            hasExist = false;
        }
        testCycleCaseUpdateVO.setCaseHasExist(hasExist);
        return testCycleCaseUpdateVO;
    }

    public TestCaseStepDTO cycleStepToCaseStep(TestCycleCaseStepDTO testCycleCaseStepDTO, TestCaseDTO testCaseDTO, CustomUserDetails userDetails) {
        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
        testCaseStepDTO.setTestStep(testCycleCaseStepDTO.getTestStep());
        testCaseStepDTO.setTestData(testCycleCaseStepDTO.getTestData());
        testCaseStepDTO.setExpectedResult(testCycleCaseStepDTO.getExpectedResult());
        testCaseStepDTO.setIssueId(testCaseDTO.getCaseId());
        testCaseStepDTO.setLastUpdatedBy(userDetails.getUserId());
        testCaseStepDTO.setCreatedBy(userDetails.getUserId());
        testCaseStepDTO.setRank(testCycleCaseStepDTO.getRank());
        testCaseStepDTO.setCycleCaseStepId(testCycleCaseStepDTO.getExecuteStepId());
        return testCaseStepDTO;
    }

    // 执行同步
    public void autoAsyncCase(List<TestCycleCaseDTO> testCycleCaseDTOS, Boolean changeCase, Boolean changeStep, Boolean changeAttach) {
        testCycleCaseDTOS.forEach(v -> {
            CaseCompareRepVO caseCompareVO = new CaseCompareRepVO();
            caseCompareVO.setCaseId(v.getCaseId());
            caseCompareVO.setExecuteId(v.getExecuteId());
            caseCompareVO.setSyncToCase(false);
            caseCompareVO.setChangeStep(changeStep);
            caseCompareVO.setChangeCase(changeCase);
            caseCompareVO.setChangeAttach(changeAttach);
            testCycleCaseService.updateCompare(v.getProjectId(), caseCompareVO);
        });

    }
}
