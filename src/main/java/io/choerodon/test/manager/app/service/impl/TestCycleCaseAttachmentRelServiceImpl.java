package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import io.choerodon.test.manager.api.vo.agile.ProjectDTO;
import io.choerodon.test.manager.app.service.FilePathService;
import io.choerodon.test.manager.infra.enums.FileUploadBucket;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;
import io.choerodon.test.manager.infra.feign.FileFeignClient;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelUploadService;
import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCaseDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCycleCaseAttachmentRelServiceImpl implements TestCycleCaseAttachmentRelService {

//    @Autowired
//    private FileService fileService;

    @Autowired
    private TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

//    private static final String BACKETNAME = "test";

    @Autowired
    private BaseFeignClient baseFeignClient;

    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private TestCycleCaseAttachmentRelUploadService testCycleCaseAttachmentRelUploadService;
    @Autowired
    private FilePathService filePathService;


//    @Value("${services.attachment.url}")
//    private String attachmentUrl;



    @Override
    public void deleteAttachmentRel(Long projectId,Long attachId) {
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        baseDelete(projectDTO.getOrganizationId(), filePathService.bucketName(), attachId);
    }

    @Override
    public TestCycleCaseAttachmentRelVO upload(Long organizationId,String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment) {
        return modelMapper.map(testCycleCaseAttachmentRelUploadService.baseUpload(bucketName, fileName, file, attachmentLinkId, attachmentType, comment,organizationId), TestCycleCaseAttachmentRelVO.class);
    }

    @Override
    public void delete(Long projectId,Long linkedId, String type) {
        Assert.notNull(linkedId, "error.delete.linkedId.not.null");
        Assert.notNull(type, "error.delete.type,not.null");
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setAttachmentLinkId(linkedId);
        testCycleCaseAttachmentRelDTO.setAttachmentType(type);
        ProjectDTO projectDTO= baseFeignClient.queryProject(projectId).getBody();
        Optional.ofNullable(testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO)).ifPresent(m ->
                m.forEach(v -> baseDelete(projectDTO.getOrganizationId(), filePathService.bucketName(), v.getId()))
        );
    }


    @Override
    public List<TestCycleCaseAttachmentRelVO> uploadMultipartFile(Long projectId,HttpServletRequest request,String attachmentType,Long attachmentLinkId,String comment) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (CollectionUtils.isEmpty(files)) {
            throw new CommonException("error.files.null");
        }
        ProjectDTO projectDTO = baseFeignClient.queryProject(projectId).getBody();
        for (MultipartFile multipartFile : files) {
            String fileName = multipartFile.getOriginalFilename();
            upload(projectDTO.getOrganizationId(), filePathService.bucketName(), fileName, multipartFile, attachmentLinkId, attachmentType, comment);
        }
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setAttachmentType(attachmentType);
        testCycleCaseAttachmentRelDTO.setAttachmentLinkId(attachmentLinkId);
        List<TestCycleCaseAttachmentRelDTO> testCycleCaseAttachmentRelDTOS = testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO);
        List<TestCycleCaseAttachmentRelVO> testCycleCaseAttachmentRelVOS = modelMapper.map(testCycleCaseAttachmentRelDTOS, new TypeToken<List<TestCycleCaseAttachmentRelVO>>() {
        }.getType());
        return testCycleCaseAttachmentRelVOS;
    }

    @Override
    public void batchInsert(List<TestCycleCaseDTO> testCycleCaseDTOS, Map<Long, List<TestCaseAttachmentDTO>> attachmentMap) {
        if(CollectionUtils.isEmpty(testCycleCaseDTOS)){
            return;
        }
        List<TestCycleCaseAttachmentRelDTO> attachmentRelDTOS = new ArrayList<>();
        testCycleCaseDTOS.forEach(v -> {
            List<TestCaseAttachmentDTO> attachmentDTOS = attachmentMap.get(v.getCaseId());
            if(CollectionUtils.isEmpty(attachmentDTOS)){
                return;
            }
            attachmentDTOS.forEach(attachmentDTO ->{
                TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
                testCycleCaseAttachmentRelDTO.setAttachmentName(attachmentDTO.getFileName());
                testCycleCaseAttachmentRelDTO.setAttachmentLinkId(v.getExecuteId());
                testCycleCaseAttachmentRelDTO.setAttachmentType(TestAttachmentCode.ATTACHMENT_CYCLE_CASE);
                String fullPath = filePathService.generateFullPath(attachmentDTO.getUrl());
                testCycleCaseAttachmentRelDTO.setUrl(fullPath);
                testCycleCaseAttachmentRelDTO.setCreatedBy(attachmentDTO.getCreatedBy());
                testCycleCaseAttachmentRelDTO.setLastUpdatedBy(attachmentDTO.getLastUpdatedBy());
                attachmentRelDTOS.add(testCycleCaseAttachmentRelDTO);
            });
        });
        if(!CollectionUtils.isEmpty(attachmentRelDTOS)){
            testCycleCaseAttachmentRelMapper.batchInsert(attachmentRelDTOS);
        }
    }

    @Override
    public void batchDeleteByExecutIds(List<Long> linkId,String type) {
        testCycleCaseAttachmentRelMapper.batchDeleteByLinkIdsAndType(linkId,type);
    }

    @Override
    public List<TestCycleCaseAttachmentRelVO> listByExecuteId(Long executeId) {
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setAttachmentLinkId(executeId);
        List<TestCycleCaseAttachmentRelDTO> attachmentRelDTOS = testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO);
        return modelMapper.map(attachmentRelDTOS,new TypeToken<List<TestCycleCaseAttachmentRelVO>>(){}.getType());
    }

    @Override
    public void snycByCase(TestCycleCaseDTO testCycleCaseDTO, TestCaseDTO testCaseDTO) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        testCycleCaseDTO.setLastUpdatedBy(userDetails.getUserId());
        testCycleCaseDTO.setCreatedBy(userDetails.getUserId());
        testCycleCaseAttachmentRelMapper.batchDeleteByLinkIdsAndType(Arrays.asList(testCycleCaseDTO.getExecuteId()),TestAttachmentCode.ATTACHMENT_CYCLE_CASE);
        List<TestCaseAttachmentDTO> attachmentDTOS = testAttachmentMapper.listByCaseIds(Arrays.asList(testCaseDTO.getCaseId()));
        Map<Long, List<TestCaseAttachmentDTO>> attachMap = attachmentDTOS.stream().collect(Collectors.groupingBy(TestCaseAttachmentDTO::getCaseId));
        batchInsert(Arrays.asList(testCycleCaseDTO),attachMap);
    }

    @Override
    public void cloneAttach(Map<Long, Long> caseIdMap, List<Long> olderExecuteIds,String type) {

        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        List<TestCycleCaseAttachmentRelDTO> list = testCycleCaseAttachmentRelMapper.listByExecuteIds(olderExecuteIds,type);
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(v -> {
            v.setAttachmentLinkId(caseIdMap.get(v.getAttachmentLinkId()));
            v.setCreatedBy(userDetails.getUserId());
            v.setLastUpdatedBy(userDetails.getUserId());
        });
        testCycleCaseAttachmentRelMapper.batchInsert(list);
    }

    private void baseDelete(Long organizationId,String bucketName, Long attachId) {
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = testCycleCaseAttachmentRelMapper.selectByPrimaryKey(attachId);
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        String url1 = testCycleCaseAttachmentRelDTO.getUrl();
        if (!ObjectUtils.isEmpty(url1) && url1.startsWith("http")) {
            String relativePath = filePathService.generateRelativePath(url1);
            testCaseAttachmentDTO.setUrl(relativePath);
            List<TestCaseAttachmentDTO> testCaseAttachmentDTOS = testAttachmentMapper.select(testCaseAttachmentDTO);
            if(CollectionUtils.isEmpty(testCaseAttachmentDTOS)){
                fileFeignClient.deleteFileByUrl(organizationId,bucketName,Arrays.asList(url1));
            }
        }
        testCycleCaseAttachmentRelMapper.deleteByPrimaryKey(attachId);
    }
}
