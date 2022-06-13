package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.vo.ListLayoutVO;
import io.choerodon.test.manager.app.service.ListLayoutService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author zhaotianxin
 * @date 2021-05-07 14:50
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/list_layout")
public class ListLayoutController {

    @Autowired
    private ListLayoutService listLayoutService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("保存列表布局配置")
    @PostMapping
    public ResponseEntity<ListLayoutVO> save(@ApiParam(value = "项目id", required = true)
                                             @PathVariable(name = "project_id") Long projectId,
                                             @ApiParam(value = "组织id", required = true)
                                             @RequestParam Long organizationId,
                                             @ApiParam(value = "布局配置", required = true)
                                             @RequestBody @Validated ListLayoutVO listLayoutVO) {
        return Optional.ofNullable(listLayoutService.save(organizationId, projectId, listLayoutVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.list.layout.save"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("根据列表编码查询展示列配置")
    @GetMapping("/{apply_type}")
    public ResponseEntity<ListLayoutVO> queryByApplyType(@ApiParam(value = "项目id", required = true)
                                                        @PathVariable(name = "project_id") Long projectId,
                                                        @ApiParam(value = "列表类型编码", required = true)
                                                        @PathVariable(name = "apply_type") String applyType,
                                                        @ApiParam(value = "组织id", required = true)
                                                        @RequestParam Long organizationId) {
        return new ResponseEntity<>(listLayoutService.queryByApplyType(organizationId, projectId, applyType), HttpStatus.OK);
    }
}
