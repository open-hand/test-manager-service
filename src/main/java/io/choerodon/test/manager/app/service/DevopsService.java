package io.choerodon.test.manager.app.service;

import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;

import java.util.List;
import java.util.Map;

public interface DevopsService {

    void getTestStatus(Map<Long,List<String>> releaseName);

    List<Long>  getAppVersionId(String appName,Long projectId,Long appId);

    Map<Long,ApplicationVersionRepDTO> getAppversion(Long projectId, List<Long> appVersionIds);
}
