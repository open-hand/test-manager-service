package io.choerodon.test.manager.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.devops.api.dto.ApplicationVersionRepDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.app.service.DevopsService;
import io.choerodon.test.manager.app.service.TestAutomationHistoryService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.domain.service.ITestAutomationHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TestAutomationHistoryServiceImpl implements TestAutomationHistoryService {

    @Autowired
    ITestAutomationHistoryService iTestAutomationHistoryService;

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    UserService userService;

    @Autowired
    DevopsService devopsService;

    @Override
    public Page<TestAutomationHistoryDTO> queryWithInstance(Map map, PageRequest pageRequest,Long projectId) {
        map.put("projectId",projectId);
        if(map.containsKey("filter")){
            List<Long> versionId=devopsService.getAppVersionId(map.get("filter").toString(),projectId,Long.valueOf(map.get("appId").toString()));
            if(versionId.isEmpty()) {
                return new Page<>();
            }
            map.put("appVersionId", versionId);
        }
        Page<TestAutomationHistoryDTO> list=iTestAutomationHistoryService.queryWithInstance(map,pageRequest);
        populateAPPVersion(projectId,list);
        userService.populateTestAutomationHistory(list);
        return list;
    }

    public void populateAPPVersion(Long projectId,Page<TestAutomationHistoryDTO> page){
        if(ObjectUtils.isEmpty(page))
            return;
        Map<Long, ApplicationVersionRepDTO> map=
                devopsService.getAppversion(projectId,page.stream().filter(u-> !ObjectUtils.isEmpty(u.getTestAppInstanceDTO()))
                        .map(v->v.getTestAppInstanceDTO().getAppVersionId()).distinct().collect(Collectors.toList()));

        page.stream().filter(u->!ObjectUtils.isEmpty(u.getTestAppInstanceDTO())).forEach(v->

                v.getTestAppInstanceDTO().setAppVersionName(map.get(v.getTestAppInstanceDTO().getAppVersionId()).getVersion()));
    }
}
