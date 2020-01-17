package io.choerodon.test.manager.app.service.impl;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.app.assembler.TestCaseAssembler;
import io.choerodon.test.manager.app.service.IIssueAttachmentService;
import io.choerodon.test.manager.app.service.TestCaseAttachmentService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.feign.FileFeignClient;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author zhaotianxin
 * @since 2019/11/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseAttachmentServiceImpl implements TestCaseAttachmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseAttachmentServiceImpl.class);

    private static final String BACKETNAME = "test";


    private final FileFeignClient fileFeignClient;

    @Autowired
    public TestCaseAttachmentServiceImpl(FileFeignClient fileFeignClient) {
        this.fileFeignClient = fileFeignClient;
    }

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Autowired
    private IIssueAttachmentService iIssueAttachmentService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCaseAssembler testCaseAssembler;
    @Autowired
    private TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Override
    public void dealIssue(Long projectId, Long issueId, String fileName, String url) {
        TestCaseAttachmentDTO issueAttachmentDTO = new TestCaseAttachmentDTO();
        issueAttachmentDTO.setProjectId(projectId);
        issueAttachmentDTO.setCaseId(issueId);
        issueAttachmentDTO.setFileName(fileName);
        issueAttachmentDTO.setUrl(String.format("/%s/%s",BACKETNAME,url));
        iIssueAttachmentService.createBase(issueAttachmentDTO);
    }

//    @DataLog(type = "createAttachment")
//    public IssueAttachmentDTO insertIssueAttachment(IssueAttachmentDTO issueAttachmentDTO) {
//        if (issueAttachmentMapper.insert(issueAttachmentDTO) != 1) {
//            throw new CommonException(INSERT_ERROR);
//        }
//        return issueAttachmentMapper.selectByPrimaryKey(issueAttachmentDTO.getAttachmentId());
//    }

//    @DataLog(type = "deleteAttachment")
//    public Boolean deleteById(Long attachmentId) {
//        IssueAttachmentDTO issueAttachmentDTO = issueAttachmentMapper.selectByPrimaryKey(attachmentId);
//        if (issueAttachmentDTO == null) {
//            throw new CommonException("error.attachment.get");
//        }
//        if (issueAttachmentMapper.delete(issueAttachmentDTO) != 1) {
//            throw new CommonException("error.attachment.delete");
//        }
//        return true;
//    }

    private String dealUrl(String url) {
        String dealUrl = null;
        try {
            URL netUrl = new URL(url);
            dealUrl = netUrl.getFile().substring(BACKETNAME.length() + 2);
        } catch (MalformedURLException e) {
            throw new CommonException(e.getMessage());
        }
        return dealUrl;
    }

    @Override
    public List<TestCaseAttachmentDTO> create(Long projectId, Long issueId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                String fileName = multipartFile.getOriginalFilename();
                ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, fileName, multipartFile);
                if (response == null || response.getStatusCode() != HttpStatus.OK) {
                    throw new CommonException("error.attachment.upload");
                }
                dealIssue(projectId, issueId, fileName, dealUrl(response.getBody()));
            }
        }
        TestCaseAttachmentDTO issueAttachmentDTO = new TestCaseAttachmentDTO();
        issueAttachmentDTO.setCaseId(issueId);
        List<TestCaseAttachmentDTO> issueAttachmentDTOList = testAttachmentMapper.select(issueAttachmentDTO);
        if (issueAttachmentDTOList != null && !issueAttachmentDTOList.isEmpty()) {
            issueAttachmentDTOList.forEach(attachment -> attachment.setUrl(attachmentUrl + attachment.getUrl()));
        }
        testCaseService.updateVersionNum(issueId);
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(projectId,issueId);
        if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
            testCaseAssembler.autoAsyncCase(testCycleCaseDTOS,false,false,true);
        }
        return issueAttachmentDTOList;
    }

    @Override
    public Boolean delete(Long projectId, Long issueAttachmentId) {
        TestCaseAttachmentDTO issueAttachmentDTO = testAttachmentMapper.selectByPrimaryKey(issueAttachmentId);
        if (issueAttachmentDTO == null) {
            throw new CommonException("error.attachment.get");
        }
        Boolean result = iIssueAttachmentService.deleteBase(issueAttachmentDTO.getAttachmentId());

        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setUrl(attachmentUrl+issueAttachmentDTO.getUrl());
        List<TestCycleCaseAttachmentRelDTO> testCycleCaseAttachmentRelDTOS = testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO);

        if(CollectionUtils.isEmpty(testCycleCaseAttachmentRelDTOS)){
            String url = null;
            try {
                url = URLDecoder.decode(issueAttachmentDTO.getUrl(), "UTF-8");
                fileFeignClient.deleteFile(BACKETNAME, attachmentUrl + url);
            } catch (Exception e) {
                LOGGER.error("error.attachment.delete", e);
            }
        }
        testCaseService.updateVersionNum(issueAttachmentDTO.getCaseId());
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(projectId,issueAttachmentDTO.getCaseId());
        if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
            testCaseAssembler.autoAsyncCase(testCycleCaseDTOS,false,false,true);
        }
        return result;
    }

    @Override
    public List<String> uploadForAddress(Long projectId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (!(files != null && !files.isEmpty())) {
            throw new CommonException("error.attachment.exits");
        }
        List<String> result = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String fileName = multipartFile.getOriginalFilename();
            ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, fileName, multipartFile);
            if (response == null || response.getStatusCode() != HttpStatus.OK) {
                throw new CommonException("error.attachment.upload");
            }
            result.add(attachmentUrl + "/" + BACKETNAME + "/" + dealUrl(response.getBody()));
        }
        return result;
    }

    @Override
    public void cloneAttachmentByCaseId(Long projectId, Long caseId, Long oldCaseId) {
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        testCaseAttachmentDTO.setCaseId(oldCaseId);
        testCaseAttachmentDTO.setProjectId(projectId);
        List<TestCaseAttachmentDTO> oldAttachment = testAttachmentMapper.select(testCaseAttachmentDTO);
        if (!CollectionUtils.isEmpty(oldAttachment)) {
            oldAttachment.forEach(v -> {
                v.setCaseId(caseId);
                v.setAttachmentId(null);
                v.setObjectVersionNumber(null);
                baseInsert(v);
            });
        }
    }

    @Override
    @DataLog(single = false,type = DataLogConstants.BATCH_INSERT_ATTACH)
    public void batchInsert(List<TestCaseAttachmentDTO> caseAttachDTOS,List<String> fileNames) {
        if(CollectionUtils.isEmpty(caseAttachDTOS)){
            return;
        }
        testAttachmentMapper.batchInsert(caseAttachDTOS);
    }

    @Override
    public void asynAttachToCase(List<TestCycleCaseAttachmentRelVO> testCycleCaseAttachmentRelVOS, TestCaseDTO testCaseDTO, Long executeId) {
        testAttachmentMapper.deleteByCaseId(testCaseDTO.getCaseId());
        if(CollectionUtils.isEmpty(testCycleCaseAttachmentRelVOS)){
          return;
        }
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        List<TestCaseAttachmentDTO> caseAttachDTOS = testCycleCaseAttachmentRelVOS.stream().map(v -> cycleAttachVoToDTO(testCaseDTO, v, userDetails)).collect(Collectors.toList());
        testAttachmentMapper.batchInsert(caseAttachDTOS);

        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(testCaseDTO.getProjectId(), testCaseDTO.getCaseId());
        if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
            List<TestCycleCaseDTO> list = testCycleCaseDTOS.stream().filter(v -> !executeId.equals(v.getExecuteId())).collect(Collectors.toList());
            testCaseAssembler.autoAsyncCase(list,false,false,true);
        }
    }

    private TestCaseAttachmentDTO cycleAttachVoToDTO(TestCaseDTO testCaseDTO, TestCycleCaseAttachmentRelVO testCycleCaseAttachmentRelVO, CustomUserDetails userDetails) {
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        testCaseAttachmentDTO.setCaseId(testCaseDTO.getCaseId());
        testCaseAttachmentDTO.setLastUpdatedBy(userDetails.getUserId());
        testCaseAttachmentDTO.setCreatedBy(userDetails.getUserId());
        testCaseAttachmentDTO.setFileName(testCycleCaseAttachmentRelVO.getAttachmentName());
        String url = testCycleCaseAttachmentRelVO.getUrl();
        url = url.replace("http://", "").replace("https://", "");
        int index = url.indexOf('/');
        String newUrl = url.substring(index);
        testCaseAttachmentDTO.setUrl(newUrl);
        testCaseAttachmentDTO.setProjectId(testCaseDTO.getProjectId());
        return testCaseAttachmentDTO;
    }

    private void baseInsert(TestCaseAttachmentDTO v) {
        if(testAttachmentMapper.insertSelective(v) != 1){
            throw new CommonException("error.insert.attachment");
        }
    }

}
