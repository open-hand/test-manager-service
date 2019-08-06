package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;

public interface DevopsService {

    void getTestStatus(Map<Long,List<String>> releaseName);

    List<Long>  getAppVersionId(String appName,Long projectId,Long appId);

    Map<Long,ApplicationVersionRepDTO> getAppversion(Long projectId, List<Long> appVersionIds);

    void getPodStatus();
}
