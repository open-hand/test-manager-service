package io.choerodon.test.manager.app.service.impl;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.app.service.FileService;
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

    @Autowired
    private FileService fileService;

    @Autowired
    private TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    private static final String BACKETNAME = "test";

    @Autowired
    private TestCycleCaseAttachmentRelUploadService testCycleCaseAttachmentRelUploadService;


    @Value("${services.attachment.url}")
    private String attachmentUrl;



    @Override
    public void deleteAttachmentRel(Long attachId) {
        baseDelete(BACKETNAME, attachId);
    }

    @Override
    public TestCycleCaseAttachmentRelVO upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment) {
        return modelMapper.map(testCycleCaseAttachmentRelUploadService.baseUpload(bucketName, fileName, file, attachmentLinkId, attachmentType, comment), TestCycleCaseAttachmentRelVO.class);
    }

    @Override
    public void delete(Long linkedId, String type) {
        Assert.notNull(linkedId, "error.delete.linkedId.not.null");
        Assert.notNull(type, "error.delete.type,not.null");
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setAttachmentLinkId(linkedId);
        testCycleCaseAttachmentRelDTO.setAttachmentType(type);
        Optional.ofNullable(testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO)).ifPresent(m ->
                m.forEach(v -> baseDelete(TestAttachmentCode.ATTACHMENT_BUCKET, v.getId()))
        );
    }


    @Override
    public List<TestCycleCaseAttachmentRelVO> uploadMultipartFile(HttpServletRequest request,String attachmentType,Long attachmentLinkId,String comment) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (CollectionUtils.isEmpty(files)) {
            throw new CommonException("error.files.null");
        }
        for (MultipartFile multipartFile : files) {
            String fileName = multipartFile.getOriginalFilename();
            upload(BACKETNAME, fileName, multipartFile, attachmentLinkId, attachmentType, comment);
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
                testCycleCaseAttachmentRelDTO.setUrl(String.format("%s%s",attachmentUrl,attachmentDTO.getUrl()));
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

    private void baseDelete(String bucketName, Long attachId) {
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = testCycleCaseAttachmentRelMapper.selectByPrimaryKey(attachId);
        TestCaseAttachmentDTO testCaseAttachmentDTO = new TestCaseAttachmentDTO();
        String url1 = testCycleCaseAttachmentRelDTO.getUrl();
        String[] split = url1.split("/"+attachmentUrl);
        if(split.length==2){
            testCaseAttachmentDTO.setUrl(split[1]);
            List<TestCaseAttachmentDTO> testCaseAttachmentDTOS = testAttachmentMapper.select(testCaseAttachmentDTO);

            if(CollectionUtils.isEmpty(testCaseAttachmentDTOS)){
                String url;
                try {
                    url = URLDecoder.decode(testCycleCaseAttachmentRelDTO.getUrl(), "UTF-8");
                } catch (IOException i) {
                    throw new CommonException(i);
                }
                ResponseEntity<String> response = fileService.deleteFile(bucketName, url);
                if (response == null || response.getStatusCode() != HttpStatus.OK) {
                    throw new CommonException("error.attachment.upload");
                }
            }
        }
        testCycleCaseAttachmentRelMapper.deleteByPrimaryKey(attachId);
    }

}
