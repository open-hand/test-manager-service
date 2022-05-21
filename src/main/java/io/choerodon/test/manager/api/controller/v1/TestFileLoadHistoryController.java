package io.choerodon.test.manager.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.vo.TestIssuesUploadHistoryVO;
import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;
import io.choerodon.test.manager.app.service.ExcelImportService;
import io.choerodon.test.manager.app.service.TestFileLoadHistoryService;

@RestController
@RequestMapping(value = "/v1/projects/{project_id}/test/fileload/history")
public class TestFileLoadHistoryController {
    @Autowired
    TestFileLoadHistoryService testFileLoadHistoryService;

    @Autowired
    private ExcelImportService excelImportService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询用例导出历史")
    @PostMapping("/case")
    public ResponseEntity<Page<TestFileLoadHistoryVO>> queryIssues(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId,
                                                                   @ApiParam(value = "分页信息", required = true)
                                                                   PageRequest pageRequest,
                                                                   @ApiParam(value = "查询参数")
                                                                   @RequestBody(required = false) SearchDTO searchDTO) {
        return Optional.ofNullable(testFileLoadHistoryService.pageFileHistoryByoptions(projectId, searchDTO, pageRequest))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.file.history.query"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询最近一次导入记录")
    @GetMapping("/latest")
    public ResponseEntity<TestIssuesUploadHistoryVO> queryLatestLoadHistory(@ApiParam(value = "项目id", required = true)
                                                                            @PathVariable("project_id") Long projectId) {
        return Optional.ofNullable(testFileLoadHistoryService.queryLatestImportIssueHistory(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.OK));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("将指定导入记录置为取消")
    @PutMapping("/cancel")
    public ResponseEntity cancelUpLoad(@ApiParam(value = "项目id", required = true)
                                       @PathVariable("project_id") Long projectId,
                                       @ApiParam(value = "导入历史id", required = true)
                                       @Encrypt @RequestParam Long historyId) {
        excelImportService.cancelFileUpload(historyId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询cycle上传历史")
    @GetMapping("/cycle")
    public ResponseEntity<List<TestFileLoadHistoryVO>> queryCycles(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "project_id") Long projectId) {
        return Optional.ofNullable(testFileLoadHistoryService.queryCycles(projectId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.filehistory.query"));
    }
}
