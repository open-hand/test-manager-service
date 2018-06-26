package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/26/18.
 */
@RestController
@RequestMapping(value = "/test/defect")
public class TestDefectController {

    @Autowired
    TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Permission(permissionPublic = true)
    @ApiOperation("增加缺陷")
    @PostMapping
    public ResponseEntity<TestCycleCaseDefectRelDTO> insert(@RequestBody TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
        return Optional.ofNullable(testCycleCaseDefectRelService.insert(testCycleCaseDefectRelDTO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.testDefect.insert"));

    }

    @Permission(permissionPublic = true)
    @ApiOperation("删除缺陷")
    @DeleteMapping
    public void removeAttachment(Long defectId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setId(defectId);
        testCycleCaseDefectRelService.delete(testCycleCaseDefectRelDTO);
    }
}
