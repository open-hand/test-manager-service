package io.choerodon.test.manager.app.service.impl;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.choerodon.test.manager.infra.dto.TestCaseAttachmentDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.app.service.FileService;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.test.manager.infra.dto.TestCycleCaseAttachmentRelDTO;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Service
public class TestCycleCaseAttachmentRelServiceImpl implements TestCycleCaseAttachmentRelService {

    @Autowired
    private TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(String bucketName, Long attachId) {
        baseDelete(bucketName, attachId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseAttachmentRelVO upload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment) {
        return modelMapper.map(baseUpload(bucketName, fileName, file, attachmentLinkId, attachmentType, comment), TestCycleCaseAttachmentRelVO.class);
    }

    @Transactional(rollbackFor = Exception.class)
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCycleCaseAttachmentRelVO> uploadMultipartFile(HttpServletRequest request, String bucketName, Long attachmentLinkId, String attachmentType) {
        List<TestCycleCaseAttachmentRelVO> testCycleCaseAttachmentRelVOS = new ArrayList<>();
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        files.forEach(v -> testCycleCaseAttachmentRelVOS.add(testCycleCaseAttachmentRelService.upload(bucketName, v.getOriginalFilename(), v, attachmentLinkId, attachmentType, null)));
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

    private void baseDelete(String bucketName, Long attachId) {
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setId(attachId);

        String url;
        try {
            url = URLDecoder.decode(testCycleCaseAttachmentRelMapper.select(testCycleCaseAttachmentRelDTO).get(0).getUrl(), "UTF-8");
        } catch (IOException i) {
            throw new CommonException(i);
        }

        ResponseEntity<String> response = fileService.deleteFile(bucketName, url);
        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new CommonException("error.attachment.upload");
        }
        testCycleCaseAttachmentRelMapper.delete(testCycleCaseAttachmentRelDTO);
    }

    private TestCycleCaseAttachmentRelDTO baseUpload(String bucketName, String fileName, MultipartFile file, Long attachmentLinkId, String attachmentType, String comment) {
        TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO = new TestCycleCaseAttachmentRelDTO();
        testCycleCaseAttachmentRelDTO.setAttachmentLinkId(attachmentLinkId);
        testCycleCaseAttachmentRelDTO.setAttachmentName(fileName);
        testCycleCaseAttachmentRelDTO.setComment(comment);

        ResponseEntity<String> response = fileService.uploadFile(bucketName, fileName, file);
        if (response == null || response.getStatusCode() != HttpStatus.OK) {
            throw new CommonException("error.attachment.upload");
        }

        testCycleCaseAttachmentRelDTO.setUrl(response.getBody());
        testCycleCaseAttachmentRelDTO.setAttachmentType(attachmentType);
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseAttachmentRelMapper::insert, testCycleCaseAttachmentRelDTO, 1, "error.attachment.insert");

        return testCycleCaseAttachmentRelDTO;
    }

    private void baseInsert(TestCycleCaseAttachmentRelDTO testCycleCaseAttachmentRelDTO){
       if(ObjectUtils.isEmpty(testCycleCaseAttachmentRelDTO)){
           throw  new CommonException("error.cycle.attachment.rel.is.null");
       }
       DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseAttachmentRelMapper::insertSelective,testCycleCaseAttachmentRelDTO,1,"error.insert.cycle.attachment.rel");
    }
}
