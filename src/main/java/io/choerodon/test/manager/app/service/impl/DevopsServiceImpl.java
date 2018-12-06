package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.infra.feign.DevopsClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DevopsServiceImpl implements DevopsService {

    @Autowired
    DevopsClient devopsClient;
    @Override
    public void getTestStatus(Map<Long,List<String>> releaseName) {
        devopsClient.getTestStatus(releaseName);
    }

    @Override
    public Long  getAppVersionId(String appName,Long projectId){

        ResponseEntity<Page<ApplicationVersionRepDTO>> list=devopsClient.pageByOptions(projectId,0,1,"id",null,appName);
        return list.getBody().get(0).getAppId();
    }

    @Override
    public Map<Long,ApplicationVersionRepDTO> getAppversion(Long projectId, List<Long> appVersionIds){
       return devopsClient.getAppversion(projectId,appVersionIds).getBody().stream().collect(Collectors.toMap(ApplicationVersionRepDTO::getId, Function.identity()));
    }
}
