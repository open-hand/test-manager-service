package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.infra.mapper.TestCycleMapper;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by jialongZuo@hand-china.com on 6/12/18.
 */
@RestController
@RequestMapping(value = "/test/cycle/{project_id}")
public class TestCycleController {
    @Autowired
    TestCycleService testCycleService;

    @Autowired
    TestCycleMapper cycleMapperl;

    @Permission(permissionPublic = true)
    @ApiOperation("增加测试循环")
    @PostMapping
    public ResponseEntity<TestCycleDTO> insert(@RequestBody TestCycleDTO testCycleDTO) {
        return Optional.ofNullable(testCycleService.insert(testCycleDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testCycle.insert"));

    }

    @Permission(permissionPublic = true)
    @ApiOperation("删除测试循环")
    @DeleteMapping
    void delete(@RequestBody TestCycleDTO testCycleDTO) {
        testCycleService.delete(testCycleDTO);
    }

    @Permission(permissionPublic = true)
    @ApiOperation("修改测试循环")
    @PutMapping
    ResponseEntity<List<TestCycleDTO>> update(@RequestBody List<TestCycleDTO> testCycleDTO) {
        return Optional.ofNullable(testCycleService.update(testCycleDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.error.testCycle.update"));
    }

//    @Permission(permissionPublic = true)
//    @ApiOperation("查询测试循环")
//    @PostMapping("/query")
//    Page<TestCycleDTO> query(@RequestBody TestCycleDTO testCycleDTO, PageRequest pageRequest) {
//        return testCycleService.query(testCycleDTO, pageRequest);
//    }

    @Permission(permissionPublic = true)
    @ApiOperation("查询测试循环")
    @GetMapping("/query/{versionId}")
    ResponseEntity getTestCycle(@PathVariable(name = "versionId") Long versionId) {
//        return Optional.ofNullable(testCycleService.getTestCycle(versionId))
//                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
//                .orElseThrow(() -> new CommonException("error.testCycle.query"));
//        cycleMapperl.query(versionId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }


    @Permission(permissionPublic = true)
    @ApiOperation("查询测试循环")
    @PostMapping("/query/case/version")
    ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(@PathVariable(name = "project_id") Long projectId, @RequestBody Map<String, Object> searchParamMap) {
        return testCycleService.getTestCycleVersion(projectId, searchParamMap);
    }

}
