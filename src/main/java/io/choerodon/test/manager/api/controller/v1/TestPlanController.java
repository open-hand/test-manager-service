package io.choerodon.test.manager.api.controller.v1;

import io.choerodon.test.manager.api.vo.TestPlanVO;
import io.choerodon.test.manager.app.service.TestPlanServcie;
import io.choerodon.test.manager.infra.dto.TestPlanDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author zhaotianxin
 * @since 2019/11/26
 */
@RestController
@RequestMapping("/v1/project/{project_id}/plan")
public class TestPlanController {
    @Autowired
    private TestPlanServcie testPlanServcie;

    @PostMapping
    public ResponseEntity<TestPlanDTO> create(@PathVariable("project_id") Long projectId,
                                              @RequestBody TestPlanVO testPlanVO){
     return new ResponseEntity<>(testPlanServcie.create(projectId,testPlanVO), HttpStatus.OK);
    }
}
