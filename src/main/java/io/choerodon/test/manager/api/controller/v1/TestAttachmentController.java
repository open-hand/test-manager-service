package io.choerodon.test.manager.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.infra.constant.EncryptKeyConstants;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.app.service.TestCycleCaseAttachmentRelService;
import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;

/**
 * Created by 842767365@qq.com on 6/21/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/test/case/attachment")
public class TestAttachmentController {

    @Autowired
    TestCycleCaseAttachmentRelService testCycleCaseAttachmentRelService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("增加附件")
    @PostMapping
    public ResponseEntity<List<TestCycleCaseAttachmentRelVO>> uploadFile(HttpServletRequest request,
                                                                         @PathVariable(name = "project_id") Long projectId,
                                                                         @Param("attachmentType") String attachmentType,
                                                                         @Param("attachmentLinkId")Long attachmentLinkId,
                                                                         @Param("comment") String comment) {
        return Optional.ofNullable(testCycleCaseAttachmentRelService.uploadMultipartFile(projectId,request,attachmentType,attachmentLinkId,comment))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.upload.file"));

    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除附件")
    @DeleteMapping("/{attachId}")
    public ResponseEntity removeAttachment(@PathVariable(name = "attachId") @Encrypt(EncryptKeyConstants.TEST_CYCLE_CASE_ATTACH_REL) Long attachId,
                                           @PathVariable(name = "project_id") Long projectId) {
        testCycleCaseAttachmentRelService.deleteAttachmentRel(projectId,attachId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}
