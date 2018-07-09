package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseDefectRelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/25/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/defect")
public class TestCycleCaseDefectRelController {

	@Autowired
	TestCycleCaseDefectRelService testCycleCaseDefectRelService;

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("增加缺陷")
	@PostMapping
	public ResponseEntity<TestCycleCaseDefectRelDTO> insert(@RequestBody TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO) {
		return Optional.ofNullable(testCycleCaseDefectRelService.insert(testCycleCaseDefectRelDTO))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testDefect.insert"));

	}

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("删除缺陷")
	@DeleteMapping
	public ResponseEntity removeAttachment(Long defectId) {
		TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
		testCycleCaseDefectRelDTO.setId(defectId);
		testCycleCaseDefectRelService.delete(testCycleCaseDefectRelDTO);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
}
