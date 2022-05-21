package io.choerodon.test.manager.api.controller.v1;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

import io.choerodon.core.iam.ResourceLevel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
                                                                         @ApiParam(value = "项目id", required = true)
                                                                         @PathVariable(name = "project_id") Long projectId,
                                                                         @ApiParam(value = "附件类型", required = true)
                                                                         @Param("attachmentType") String attachmentType,
                                                                         @ApiParam(value = "附件关联id", required = true)
                                                                         @Param("attachmentLinkId") @Encrypt Long attachmentLinkId,
                                                                         @ApiParam(value = "描述", required = true)
                                                                         @Param("comment") String comment) {
        return Optional.ofNullable(testCycleCaseAttachmentRelService.uploadMultipartFile(projectId, request, attachmentType, attachmentLinkId, comment))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.upload.file"));

    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除附件")
    @DeleteMapping("/{attachId}")
    public ResponseEntity removeAttachment(@ApiParam(value = "附件关联id", required = true)
                                           @PathVariable(name = "attachId")
                                           @Encrypt Long attachId,
                                           @ApiParam(value = "项目id", required = true)
                                           @PathVariable(name = "project_id") Long projectId) {
        testCycleCaseAttachmentRelService.deleteAttachmentRel(projectId, attachId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
