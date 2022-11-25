package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.test.manager.api.vo.devops.AppServiceVersionRespVO;

public interface DevopsService {

    List<Long>  getAppVersionId(String appName,Long projectId,Long appId);

    Map<Long, AppServiceVersionRespVO> getAppversion(Long projectId, List<Long> appVersionIds);

}
