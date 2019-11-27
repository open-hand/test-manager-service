package io.choerodon.test.manager.app.service.impl;

import java.util.List;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.app.service.TestLabelRelService;
import io.choerodon.test.manager.infra.dto.TestCaseLabelRelDTO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseLabelRelDTO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseLabelRelMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author zhaotianxin
 * @since 2019/11/27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestLabelRelServiceImpl implements TestLabelRelService {
    @Autowired
    private TestCycleCaseLabelRelMapper testCycleCaseLabelRelMapper;

    @Override
    public void batchInsert(Long executeId, List<TestCaseLabelRelDTO> testCaseLabelRelDTOS) {
        if(CollectionUtils.isEmpty(testCaseLabelRelDTOS)){
          return;
        }
        testCaseLabelRelDTOS.forEach(v -> {
           TestCycleCaseLabelRelDTO testCycleCaseLabelRelDTO = new TestCycleCaseLabelRelDTO();
            testCycleCaseLabelRelDTO.setCaseId(v.getCaseId());
            testCycleCaseLabelRelDTO.setExecuteId(executeId);
            testCycleCaseLabelRelDTO.setProjectId(v.getProjectId());
            testCycleCaseLabelRelDTO.setLabelId(v.getLabelId());
            baseInsert(testCycleCaseLabelRelDTO);
        });
    }

    private void baseInsert(TestCycleCaseLabelRelDTO testCycleCaseLabelRelDTO){
         if(ObjectUtils.isEmpty(testCycleCaseLabelRelDTO)){
             throw new CommonException("error.cycle.label.rel.is.null");
         }
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseLabelRelMapper::insertSelective,testCycleCaseLabelRelDTO,1,"error.insert.cycle.label.rel");
    }
}
