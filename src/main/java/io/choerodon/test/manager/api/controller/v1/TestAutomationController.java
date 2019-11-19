package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.JsonImportService;
import io.choerodon.test.manager.app.service.TestAppInstanceService;
import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.infra.util.FileUtil;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/v1/automation")
public class TestAutomationController {

    private static final Logger logger = LoggerFactory.getLogger(TestAutomationController.class);

    @Autowired
    private JsonImportService jsonImportService;

    @Autowired
    private TestAppInstanceService appInstanceService;

    @Permission(permissionPublic = true)
    @ApiOperation("导入自动化测试报告【mocha】")
    @PostMapping("/import/report/mocha")
    public ResponseEntity<Long> importMochaReport(@RequestParam String releaseName,
                                                  @RequestParam("file") MultipartFile file) {
        byte[] bytes;
        try {
            bytes = FileUtil.unTarGzToMemory(file.getInputStream()).get(0);
        } catch (IOException e) {
            throw new CommonException("error.decompress.tarGz", e);
        }
        String xml = new String(bytes, StandardCharsets.UTF_8);
//        logger.info("releaseName:{}", releaseName);
//        logger.info("xml:{}", xml);
        try {
            return new ResponseEntity<>(jsonImportService.importMochaReport(releaseName, xml), HttpStatus.CREATED);
        } catch (Throwable e) {
            appInstanceService.updateStatus(Long.parseLong(TestAppInstanceDTO.getInstanceIDFromReleaseName(releaseName)), 3L);
            logger.error("导入mocha测试报告失败，测试状态置为失败", e);
            throw new CommonException("error.automation.import.mocha.report");
        }
    }

    @Permission(permissionPublic = true)
    @ApiOperation("导入自动化测试报告【testng】")
    @PostMapping("/import/report/testng")
    public ResponseEntity<Long> importTestNgReport(@RequestParam String releaseName,
                                                   @RequestParam("file") MultipartFile file) {
        byte[] bytes;
        try {
            bytes = FileUtil.unTarGzToMemory(file.getInputStream()).get(0);
        } catch (IOException e) {
            throw new CommonException("error.decompress.tarGz", e);
        }
        String xml = new String(bytes, StandardCharsets.UTF_8);
//        logger.info("releaseName:{}", releaseName);
//        logger.info("xml:{}", xml);
        try {
            return new ResponseEntity<>(jsonImportService.importTestNgReport(releaseName, xml), HttpStatus.CREATED);
        } catch (Throwable e) {
            appInstanceService.updateStatus(Long.parseLong(TestAppInstanceDTO.getInstanceIDFromReleaseName(releaseName)), 3L);
            logger.error("导入testng测试报告失败，测试状态置为失败", e);
            throw new CommonException("error.automation.import.testng.report");
        }
    }
}
