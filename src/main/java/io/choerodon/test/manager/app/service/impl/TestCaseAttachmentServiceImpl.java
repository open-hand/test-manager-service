package io.choerodon.test.manager.app.service.impl;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.IIssueAttachmentService;
import io.choerodon.test.manager.app.service.TestCaseAttachmentService;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.feign.FileFeignClient;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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

    private static final String BACKETNAME = "agile-service";


    private final FileFeignClient fileFeignClient;

    @Autowired
    public TestCaseAttachmentServiceImpl(FileFeignClient fileFeignClient) {
        this.fileFeignClient = fileFeignClient;
    }

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @Autowired
    private IIssueAttachmentService iIssueAttachmentService;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Override
    public void dealIssue(Long projectId, Long issueId, String fileName, String url) {
        TestCaseAttachmentDTO issueAttachmentDTO = new TestCaseAttachmentDTO();
        issueAttachmentDTO.setProjectId(projectId);
        issueAttachmentDTO.setCaseId(issueId);
        issueAttachmentDTO.setFileName(fileName);
        issueAttachmentDTO.setUrl(url);
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
            issueAttachmentDTOList.forEach(attachment -> {
                attachment.setUrl(attachmentUrl + "/" + BACKETNAME + "/" + attachment.getUrl());
            });
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
        String url = null;
        try {
            url = URLDecoder.decode(issueAttachmentDTO.getUrl(), "UTF-8");
            fileFeignClient.deleteFile(BACKETNAME, attachmentUrl + "/" + BACKETNAME + "/" + url);
        } catch (Exception e) {
            LOGGER.error("error.attachment.delete", e);
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
    public int deleteByIssueId(Long issueId) {
        TestCaseAttachmentDTO issueAttachmentDTO = new TestCaseAttachmentDTO();
        issueAttachmentDTO.setCaseId(issueId);
        return testAttachmentMapper.delete(issueAttachmentDTO);
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

    private void baseInsert(TestCaseAttachmentDTO v) {
        if(testAttachmentMapper.insertSelective(v) != 1){
            throw new CommonException("error.insert.attachment");
        }
    }

}
