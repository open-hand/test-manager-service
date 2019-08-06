package io.choerodon.test.manager.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import com.google.common.primitives.Longs;
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
        return devopsClient.getAppversion(projectId, toArray(appVersionIds)).getBody().stream().collect(Collectors.toMap(ApplicationVersionRepDTO::getId, Function.identity()));
    }

    private Long[] toArray(List<Long> values) {
        Long[] result = new Long[values.size()];
        int i = 0;
        for (Long l : values)
            result[i++] = l;
        return result;
    }
}
