package io.choerodon.test.manager.api.controller.v1;

import com.alibaba.fastjson.JSONArray;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.TestCycleCaseStepService;
import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

/**
 * Created by 842767365@qq.com on 6/14/18.
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/cycle/case/step")
public class TestCycleCaseStepController {

	@Autowired
	TestCycleCaseStepService testCycleCaseStepService;

//	/**
//	 * 更新循环步骤
//	 *
//	 * @param testCycleCaseStepDTO
//	 * @return
//	 */
//	@Permission(level = ResourceLevel.PROJECT)
//	@ApiOperation("更新循环步骤")
//	@PutMapping
//	ResponseEntity<List<TestCycleCaseStepDTO>> update(@RequestBody List<TestCycleCaseStepDTO> testCycleCaseStepDTO) {
//		return Optional.ofNullable(testCycleCaseStepService.update(testCycleCaseStepDTO))
//				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
//				.orElseThrow(() -> new CommonException("error.testCycleCaseStep.update"));
//
//	}


	/**
	 * 查询循环测试步骤
	 *
	 * @param cycleCaseId
	 * @return
	 */
	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("查询循环步骤")
	@GetMapping("/query/{cycleCaseId}")
	ResponseEntity<Page<TestCycleCaseStepDTO>> querySubStep(@PathVariable(name = "project_id") Long projectId,
															@ApiParam(value = "cycleCaseId", required = true)
															@PathVariable(name = "cycleCaseId") Long cycleCaseId,
															@ApiIgnore
															@ApiParam(value = "分页信息", required = true)
															@SortDefault(value = "rank", direction = Sort.Direction.DESC)
																	PageRequest pageRequest) {

		return Optional.ofNullable(testCycleCaseStepService.querySubStep(cycleCaseId, pageRequest, projectId))
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElseThrow(() -> new CommonException("error.testCycleCaseStep.query"));

	}

	@Permission(level = ResourceLevel.PROJECT)
	@ApiOperation("修改一个测试循环")
	@PostMapping("/updateWithAttach")
	public ResponseEntity updateOneCase(HttpServletRequest request,
										@PathVariable(name = "project_id") Long projectId,
										@RequestParam(required = false, name = "comment") String comment,
										@RequestParam(name = "stepStatus") String stepStatus) {
		List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
		TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
		List<TestCycleCaseDefectRelDTO> defects = null;
		String def = request.getParameter("defects");
		if (!StringUtils.isEmpty(def)) {
			defects = JSONArray.parseArray(def, TestCycleCaseDefectRelDTO.class);
		}
		testCycleCaseStepDTO.setExecuteId(Long.valueOf(request.getParameter("executeId")));
		testCycleCaseStepDTO.setStepStatus(Long.valueOf(stepStatus));
		testCycleCaseStepDTO.setStepId(Long.valueOf(request.getParameter("stepId")));
		testCycleCaseStepDTO.setExecuteStepId(Long.valueOf(request.getParameter("executeStepId")));
		testCycleCaseStepDTO.setComment(comment);
		testCycleCaseStepDTO.setObjectVersionNumber(Long.valueOf(request.getParameter("objectVersionNumber")));
		return Optional.ofNullable(testCycleCaseStepService.updateOneCase(files, testCycleCaseStepDTO, defects))
				.map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
				.orElseThrow(() -> new CommonException("error.testCycleCase.query"));
	}


//    /**
//     * 启动循环测试下所有步骤
//     *
//     * @param testCycleCaseDTO
//     */
//    void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO) {
//        testCycleCaseStepService.createTestCycleCaseStep(testCycleCaseDTO);
//    }
//
//    /**
//     * 删除CycleCase下所有Step
//     *
//     * @param testCycleCaseDTO
//     */
//    void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO);

}
