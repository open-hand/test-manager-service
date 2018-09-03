package io.choerodon.test.manager.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.api.dto.TestCycleDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.app.service.TestCycleService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.domain.service.ITestIssueFolderService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@Component
public class TestIssueFolderServiceImpl implements TestIssueFolderService {

    @Autowired
    TestCycleService testCycleService;

    @Autowired
    ITestIssueFolderService iTestIssueFolderService;

    @Autowired
    ProductionVersionClient productionVersionClient;

    @Override
    public List<TestIssueFolderDTO> query(TestIssueFolderDTO testIssueFolderDTO) {
        return ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderDTO insert(TestIssueFolderDTO testIssueFolderDTO) {
        return ConvertHelper.convert(iTestIssueFolderService.insert(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(TestIssueFolderDTO testIssueFolderDTO) {
        iTestIssueFolderService.delete(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestIssueFolderDTO update(TestIssueFolderDTO testIssueFolderDTO) {
        return ConvertHelper.convert(iTestIssueFolderService.update(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
    }

    @Override
    public JSONObject getTestIssueFolder(Long projectId) {
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);
        ResponseEntity<List<ProductVersionDTO>> dto = productionVersionClient.listByProjectId(projectId);
        List<ProductVersionDTO> versions = dto.getBody();
        if (versions.isEmpty()) {
            return new JSONObject();
        }
        JSONObject root = new JSONObject();
        JSONArray versionStatus = new JSONArray();
        root.put("versions", versionStatus);

        List<TestIssueFolderDTO> testIssueFolderDTOS = ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);

        if(testIssueFolderDTOS.isEmpty()){
            return new JSONObject();
        }

        List<TestCycleDTO> cycles=testIssueFolderDTOS.stream().map(TestIssueFolderDTO::transferToCycle).collect(Collectors.toList());
        testCycleService.initVersionTree(versionStatus, versions, cycles);

        return root;
    }

    public Long getDefaultFolderId(Long projectId,Long versionId){
        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
        testIssueFolderDTO.setProjectId(projectId);
        testIssueFolderDTO.setVersionId(versionId);
        testIssueFolderDTO.setType("temp");
        testIssueFolderDTO.setName("临时");
        List<TestIssueFolderDTO> testIssueFolderDTOS = ConvertHelper.convertList(iTestIssueFolderService.query(ConvertHelper
                .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class);
        if(testIssueFolderDTOS.isEmpty()){
            return ConvertHelper.convert(iTestIssueFolderService.insert(ConvertHelper
                    .convert(testIssueFolderDTO, TestIssueFolderE.class)), TestIssueFolderDTO.class).getFolderId();
        }else {
            return testIssueFolderDTOS.get(0).getFolderId();
        }
    }

}
