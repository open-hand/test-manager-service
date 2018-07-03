package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.agile.api.dto.ProductVersionPageDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleServiceImpl implements TestCycleService {
    @Autowired
    ITestCycleService iTestCycleService;

    @Autowired
    ProductionVersionClient productionVersionClient;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleDTO insert(TestCycleDTO testCycleDTO) {
        return ConvertHelper.convert(iTestCycleService.insert(ConvertHelper.convert(testCycleDTO, TestCycleE.class)), TestCycleDTO.class);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(TestCycleDTO testCycleDTO) {
        iTestCycleService.delete(ConvertHelper.convert(testCycleDTO, TestCycleE.class));

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCycleDTO> update(List<TestCycleDTO> testCycleDTO) {
        return ConvertHelper.convertList(iTestCycleService.update(ConvertHelper.convertList(testCycleDTO, TestCycleE.class)), TestCycleDTO.class);

    }

    @Override
    public List<TestCycleDTO> getTestCycle(Long versionId) {
        return ConvertHelper.convertList(iTestCycleService.queryCycleWithBar(versionId), TestCycleDTO.class);
    }

    @Override
    public List<TestCycleDTO> filterCycleWithBar(String filter) {
        JSONObject object = JSON.parseObject(filter);
        return ConvertHelper.convertList(iTestCycleService
                .filterCycleWithBar(object.getString("parameter"), (Long[]) object.getJSONArray("versionIds").toArray()), TestCycleDTO.class);
    }

    @Override
    public ResponseEntity<Page<ProductVersionPageDTO>> getTestCycleVersion(Long projectId, Map<String, Object> searchParamMap) {
        return productionVersionClient.listByOptions(projectId, searchParamMap);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleDTO cloneCycle(Long cycleId, String cycleName) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleId(cycleId);
        List<TestCycleE> list = iTestCycleService.querySubCycle(testCycleE);
        if (!(list.size() == 1 && list.get(0).getCycleName() != cycleName)) {
            throw new CommonException("error.test.cycle.clone.duplicate.name");
        }
        TestCycleE newTestCycleE = TestCycleEFactory.create();
        newTestCycleE.setCycleName(cycleName);
        newTestCycleE.setType(TestCycleE.CYCLE);
        return ConvertHelper.convert(iTestCycleService.cloneCycle(list.get(0), newTestCycleE), TestCycleDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleDTO cloneFolder(Long cycleId, TestCycleDTO testCycleDTO) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleId(cycleId);
        List<TestCycleE> list = iTestCycleService.querySubCycle(testCycleE);
        if (list.size() != 1) {
            throw new CommonException("error.test.cycle.clone.");
        }

        return ConvertHelper.convert(iTestCycleService.cloneFolder(list.get(0), ConvertHelper.convert(testCycleDTO, TestCycleE.class)), TestCycleDTO.class);
    }
}
