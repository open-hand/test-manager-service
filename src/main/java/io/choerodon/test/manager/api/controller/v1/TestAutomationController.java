package io.choerodon.test.manager.api.controller.v1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.app.service.JsonImportService;
import io.choerodon.test.manager.infra.common.utils.FileUtil;

@RestController
@RequestMapping("/v1/automation")
public class TestAutomationController {

    @Autowired
    private JsonImportService jsonImportService;

    @Permission(permissionPublic = true)
    @ApiOperation("从导入自动化测试报告")
    @PostMapping("/import/report/mocha")
    public ResponseEntity<Long> importMochaReport(@RequestParam String releaseName,
                                                  @RequestParam("file") MultipartFile file) {
        byte[] bytes;
        try {
            bytes = FileUtil.unTarGzToMemory(file.getInputStream()).get(0);
        } catch (IOException e) {
            throw new CommonException("error.decompress.tarGz", e);
        }

        return Optional.ofNullable(jsonImportService.importMochaReport(
                releaseName, new String(bytes, StandardCharsets.UTF_8)))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.automation.import.mocha.report"));
    }
}
