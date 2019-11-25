package io.choerodon.test.manager.app.assembler;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import io.choerodon.mybatis.entity.BaseDTO;
import io.choerodon.test.manager.api.vo.TestCaseInfoVO;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.mapper.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
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
    private static final String BACKETNAME = "agile-service";

    @Autowired
    private TestProjectInfoMapper testProjectInfoMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestCaseLinkMapper testCaseLinkMapper;

    @Autowired
    private TestDataLogMapper testDataLogMapper;

    @Autowired
    private TestCaseLinkService testCaseLinkService;

    @Autowired
    private TestCaseLabelRelService testCaseLabelRelService;

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    public TestCaseRepVO dtoToRepVo(TestCaseDTO testCaseDTO) {
        Map<Long, UserMessageDTO> map = getUserMap(testCaseDTO, null);
        TestCaseRepVO testCaseRepVO = new TestCaseRepVO();
        modelMapper.map(testCaseDTO, testCaseRepVO);
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(testCaseDTO.getProjectId());
        testCaseRepVO.setCaseNum(getIssueNum(testCaseDTO.getProjectId(),testCaseDTO.getCaseNum()));
        testCaseRepVO.setCreateUser(map.get(testCaseDTO.getCreatedBy()));
        testCaseRepVO.setLastUpdateUser(map.get(testCaseDTO.getLastUpdatedBy()));
        return testCaseRepVO;
    }

    public List<TestCaseRepVO> listDtoToRepVo(List<TestCaseDTO> list){
        Map<Long, UserMessageDTO> userMap = getUserMap(null, modelMapper.map(list, new TypeToken<List<BaseDTO>>() {
        }.getType()));
        List<TestCaseRepVO> collect = list.stream()
                .map(v -> dtoToRepVo(v)).collect(Collectors.toList());
        return collect;
    }

    public TestCaseInfoVO dtoToInfoVO(TestCaseDTO testCaseDTO){
        TestCaseInfoVO testCaseInfoVO = modelMapper.map(testCaseDTO, TestCaseInfoVO.class);
        // 获取用户信息
        Map<Long, UserMessageDTO> UserMessageDTOMap = getUserMap(testCaseDTO,null);
        if (!ObjectUtils.isEmpty(UserMessageDTOMap.get(testCaseDTO.getCreatedBy()))) {
            testCaseInfoVO.setCreateUser(UserMessageDTOMap.get(testCaseDTO.getCreatedBy()));
        }
        if (!ObjectUtils.isEmpty(UserMessageDTOMap.get(testCaseDTO.getCreatedBy()))) {
            testCaseInfoVO.setLastUpdateUser(UserMessageDTOMap.get(testCaseDTO.getLastUpdatedBy()));
        }
        //  获取用例的标签
        List<TestCaseLabelRelDTO> testCaseLabelRelDTOS = testCaseLabelRelService.listLabelByCaseId(testCaseDTO.getCaseId());
        if (!CollectionUtils.isEmpty(testCaseLabelRelDTOS)) {
            List<Long> labelIds = testCaseLabelRelDTOS.stream().map(TestCaseLabelRelDTO::getLabelId).collect(Collectors.toList());
            testCaseInfoVO.setLableIds(labelIds);
        }
        // 用例的问题链接
        testCaseInfoVO.setIssuesInfos(testCaseLinkService.listIssueInfo(testCaseDTO.getProjectId(), testCaseDTO.getCaseId()));
        // 查询附件信息
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        testCaseAttachmentDTO.setCaseId(testCaseDTO.getCaseId());
        List<TestCaseAttachmentDTO> attachment = testAttachmentMapper.select(testCaseAttachmentDTO);
        if (!CollectionUtils.isEmpty(attachment)) {
            attachment.forEach(v -> {
                v.setUrl(attachmentUrl + "/" + BACKETNAME + "/" + v.getUrl());
            });
            testCaseInfoVO.setAttachment(attachment);
        }
        // 查询测试用例所属的文件夹
        TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(testCaseDTO.getFolderId());
        if (!ObjectUtils.isEmpty(testIssueFolderDTO)) {
            testCaseInfoVO.setFolder(testIssueFolderDTO.getName());
        }
        testCaseInfoVO.setCaseNum(getIssueNum(testCaseDTO.getProjectId(),testCaseDTO.getCaseNum()));
        return testCaseInfoVO;
    }

    private String getIssueNum(Long projectId,String caseNum){
        TestProjectInfoDTO testProjectInfoDTO = new TestProjectInfoDTO();
        testProjectInfoDTO.setProjectId(projectId);
        TestProjectInfoDTO testProjectInfo = testProjectInfoMapper.selectOne(testProjectInfoDTO);
        String issue = String.format("%s-%s", testProjectInfo.getProjectCode(), caseNum);
        return issue;
    }

    public Map<Long, UserMessageDTO> getUserMap(BaseDTO baseDTO,List<BaseDTO> list){
        List<Long> userIds = new ArrayList<>();
        if (!ObjectUtils.isEmpty(baseDTO)){
            userIds.add(baseDTO.getCreatedBy());
            userIds.add(baseDTO.getLastUpdatedBy());
        }
        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(v -> {
                userIds.add(v.getCreatedBy());
                userIds.add(v.getLastUpdatedBy());
            });
        }
        Map<Long, UserMessageDTO> userMessageDTOMap = userService.queryUsersMap(userIds);
        return  userMessageDTOMap;
    }
}
