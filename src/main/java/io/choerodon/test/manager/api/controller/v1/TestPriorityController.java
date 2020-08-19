package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.app.service.TestPriorityService;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import io.choerodon.test.manager.domain.entity.TestPriority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.hzero.mybatis.helper.SecurityTokenHelper;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 *  管理 API
 *
 * @author jiaxu.cui@hand-china.com 2020-08-19 17:25:29
 */
@RestController("testPriorityController.v1")
@RequestMapping("/v1/{organizationId}/test_priority")
public class TestPriorityController extends BaseController {

    @Autowired
    private TestPriorityService testPriorityService;

    @ApiOperation(value = "列表")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping
    public ResponseEntity<Page<TestPriority>> list(TestPriority testPriority, @ApiIgnore @SortDefault(value = TestPriority.FIELD_ID,
            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        Page<TestPriority> list = null;
        return Results.success(list);
    }

    @ApiOperation(value = "明细")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{id}")
    public ResponseEntity<TestPriority> detail(@PathVariable Long id) {
        TestPriority testPriority = null;
        return Results.success(testPriority);
    }

    @ApiOperation(value = "创建")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PostMapping
    public ResponseEntity<TestPriority> create(@RequestBody TestPriority testPriority) {
        validObject(testPriority);
        return Results.success(testPriority);
    }

    @ApiOperation(value = "修改")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @PutMapping
    public ResponseEntity<TestPriority> update(@RequestBody TestPriority testPriority) {
        SecurityTokenHelper.validToken(testPriority);
//        testPriorityService.updateByPrimaryKeySelective(testPriority);
        return Results.success(testPriority);
    }

    @ApiOperation(value = "删除")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestBody TestPriority testPriority) {
        SecurityTokenHelper.validToken(testPriority);
//        testPriorityService.deleteByPrimaryKey(testPriority);
        return Results.success();
    }

}
