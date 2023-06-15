package io.choerodon.test.manager.app.service.impl;

import io.choerodon.test.manager.app.service.FilePathService;
import io.choerodon.test.manager.infra.enums.FileUploadBucket;
import org.hzero.boot.file.FileClient;
import org.hzero.core.util.ResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.TestCaseAttachmentCombineVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.app.service.IIssueAttachmentService;
import io.choerodon.test.manager.app.service.TestCaseAttachmentService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.feign.FileFeignClient;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper;

/**
 * @author zhaotianxin
 * @since 2019/11/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseAttachmentServiceImpl implements TestCaseAttachmentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseAttachmentServiceImpl.class);

//    private static final String BACKETNAME = "test";

    @Autowired
    private  FileFeignClient fileFeignClient;
    @Autowired
    private BaseFeignClient baseFeignClient;

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Autowired
    private IIssueAttachmentService iIssueAttachmentService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

//    @Value("${services.attachment.url}")
//    private String attachmentUrl;
    @Autowired
    private FilePathService filePathService;
    @Autowired
    private FileClient fileClient;


    @Override
    public TestCaseAttachmentDTO dealIssue(Long projectId, Long issueId, String fileName, String url) {
        TestCaseAttachmentDTO issueAttachmentDTO = new TestCaseAttachmentDTO();
        issueAttachmentDTO.setProjectId(projectId);
        issueAttachmentDTO.setCaseId(issueId);
        issueAttachmentDTO.setFileName(fileName);
        issueAttachmentDTO.setUrl(url);
        return iIssueAttachmentService.createBase(issueAttachmentDTO);
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

//    private String dealUrl(String url) {
//        String dealUrl = null;
//        try {
//            URL netUrl = new URL(url);
//            dealUrl = netUrl.getFile();
//        } catch (MalformedURLException e) {
//            throw new CommonException(e.getMessage());
//        }
//        return dealUrl;
//    }

    @Override
    public List<TestCaseAttachmentDTO> create(Long projectId, Long issueId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                String fileName = multipartFile.getOriginalFilename();
                String path = fileClient.uploadFile(projectDTO.getOrganizationId(), filePathService.bucketName(),filePathService.dirName(), fileName, multipartFile);
                String relativePath = filePathService.generateRelativePath(path);
                dealIssue(projectId, issueId, fileName, relativePath);
            }
        }
        TestCaseAttachmentDTO issueAttachmentDTO = new TestCaseAttachmentDTO();
        issueAttachmentDTO.setCaseId(issueId);
        List<TestCaseAttachmentDTO> issueAttachmentDTOList = testAttachmentMapper.select(issueAttachmentDTO);
        if (issueAttachmentDTOList != null && !issueAttachmentDTOList.isEmpty()) {
            issueAttachmentDTOList.forEach(attachment -> attachment.setUrl(
                    filePathService.generateFullPath(attachment.getUrl())));
        }
        testCaseService.updateVersionNum(issueId);
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(projectId,issueId);
        if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
            testCaseService.autoAsyncCase(testCycleCaseDTOS,false,false,true);
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
        testCycleCaseAttachmentRelDTO.setUrl(
                filePathService.generateFullPath(issueAttachmentDTO.getUrl()));
        List<TestCycleCaseAttachmentRelDTO> testCycleCaseAttachmentRelDTOS = testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO);

        if(CollectionUtils.isEmpty(testCycleCaseAttachmentRelDTOS)){
            String url = null;
            try {
                ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
                url = URLDecoder.decode(issueAttachmentDTO.getUrl(), "UTF-8");
                String fullPath = filePathService.generateFullPath(url);
                fileFeignClient.deleteFileByUrl(projectDTO.getOrganizationId(), filePathService.bucketName(), Arrays.asList(fullPath));
            } catch (Exception e) {
                LOGGER.error("error.attachment.delete", e);
            }
        }
        testCaseService.updateVersionNum(issueAttachmentDTO.getCaseId());
        List<TestCycleCaseDTO> testCycleCaseDTOS = testCycleCaseMapper.listAsyncCycleCase(projectId,issueAttachmentDTO.getCaseId());
        if(!CollectionUtils.isEmpty(testCycleCaseDTOS)){
            testCaseService.autoAsyncCase(testCycleCaseDTOS,false,false,true);
        }
        return result;
    }

    @Override
    public List<String> uploadForAddress(Long projectId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (!(files != null && !files.isEmpty())) {
            throw new CommonException("error.attachment.exits");
        }
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        List<String> result = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String fileName = multipartFile.getOriginalFilename();
            String path = fileClient.uploadFile(projectDTO.getOrganizationId(), filePathService.bucketName(), filePathService.dirName(),fileName, multipartFile);
            result.add(path);
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
            testCaseService.autoAsyncCase(list,false,false,true);
        }
    }

    @Override
    public TestCaseAttachmentDTO attachmentCombineUpload(Long projectId, TestCaseAttachmentCombineVO testCaseAttachmentCombineVO) {
        Long caseId = testCaseAttachmentCombineVO.getCaseId();
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        if (ObjectUtils.isEmpty(projectDTO)) {
            throw new CommonException("error.attachmentRule.project");
        }
        String fileName = testCaseAttachmentCombineVO.getFileName();

        Map<String, String> args = new HashMap<>(1);
        args.put("bucketName", filePathService.bucketName());
        args.put("directory", filePathService.dirName());
        String path = ResponseUtils.getResponse(fileFeignClient.fragmentCombineBlock(
                projectDTO.getOrganizationId(),
                testCaseAttachmentCombineVO.getGuid(),
                testCaseAttachmentCombineVO.getFileName(),
                args),
                String.class,
                (httpStatus, response) -> {
                }, exceptionResponse -> {
                    LOGGER.error("combine fragment failed: {}", exceptionResponse.getMessage());
                    throw new CommonException(exceptionResponse.getMessage());
                });
        if (path == null) {
            throw new CommonException("error.attachment.combine.failed");
        }
        String relativePath = filePathService.generateRelativePath(path);
        TestCaseAttachmentDTO attachment = dealIssue(projectId, caseId, fileName, relativePath);
        String fullPath = filePathService.generateFullPath(attachment.getUrl());
        attachment.setUrl(fullPath);
        testCaseService.updateVersionNumNotObjectVersion(caseId, DetailsHelper.getUserDetails().getUserId());

        List<TestCycleCaseDTO> testCycleCases = testCycleCaseMapper.listAsyncCycleCase(projectId, caseId);
        if (!CollectionUtils.isEmpty(testCycleCases)) {
            testCaseService.autoAsyncCase(testCycleCases, false, false, true);
        }
        return attachment;
    }

    @Override
    public void validCombineUpload(TestCaseAttachmentCombineVO testCaseAttachmentCombineVO) {
        if (testCaseAttachmentCombineVO.getCaseId() == null) {
            throw new CommonException("error.attachmentRule.caseId");
        }
        if (testCaseAttachmentCombineVO.getFileName() == null) {
            throw new CommonException("error.attachmentRule.fileName");
        }
        if (testCaseAttachmentCombineVO.getGuid() == null) {
            throw new CommonException("error.attachmentRule.guId");
        }
        TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(testCaseAttachmentCombineVO.getCaseId());
        if (ObjectUtils.isEmpty(testCaseDTO)) {
            throw new CommonException("error.attachmentRule.testCase");
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
