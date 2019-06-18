package io.choerodon.test.manager.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.infra.feign.DevopsClient;

@Component
public class DevopsServiceImpl implements DevopsService {

    @Autowired
    DevopsClient devopsClient;

    @Override
    public void getTestStatus(Map<Long, List<String>> releaseName) {
        devopsClient.getTestStatus(releaseName);
    }

    @Override
    public List<Long> getAppVersionId(String appName, Long projectId, Long appId) {

        ResponseEntity<PageInfo<ApplicationVersionRepDTO>> list = devopsClient.pageByOptions(projectId, 0, 9999999, "id", appId, appName);
        return list.getBody().getList().stream().map(ApplicationVersionRepDTO::getId).collect(Collectors.toList());
    }

    @Override
    public Map<Long, ApplicationVersionRepDTO> getAppversion(Long projectId, List<Long> appVersionIds) {
        return devopsClient.getAppversion(projectId, appVersionIds).getBody().stream().collect(Collectors.toMap(ApplicationVersionRepDTO::getId, Function.identity()));
    }
}
