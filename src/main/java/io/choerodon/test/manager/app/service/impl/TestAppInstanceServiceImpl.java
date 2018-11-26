package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.devops.api.dto.ErrorLineDTO;
import io.choerodon.devops.api.dto.ReplaceResult;
import io.choerodon.devops.infra.common.utils.TypeUtil;
import io.choerodon.test.manager.api.dto.ApplicationDeployDTO;
import io.choerodon.test.manager.app.service.ScheduleService;
import io.choerodon.test.manager.app.service.TestAppInstanceService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.domain.service.ITestAppInstanceService;
import io.choerodon.test.manager.domain.service.ITestEnvCommandService;
import io.choerodon.test.manager.domain.service.ITestEnvCommandValueService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceE;
import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommand;
import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommandValue;
import io.choerodon.test.manager.infra.common.utils.FileUtil;
import io.choerodon.test.manager.infra.common.utils.GenerateUUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
@Component
public class TestAppInstanceServiceImpl implements TestAppInstanceService {

    @Autowired
    ITestAppInstanceService instanceService;

    @Autowired
    ITestEnvCommandService commandService;

    @Autowired
    ITestEnvCommandValueService commandValueService;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    TestCaseService testCaseService;

    private static final String SCHEDULECODE = "test-deploy-instance";

    private static final String DEPLOYDTONAME = "deploy";

    @Override
    public List<TestAppInstanceDTO> query(TestAppInstanceE instanceE) {
        return ConvertHelper.convertList(instanceService.query(instanceE), TestAppInstanceDTO.class);
    }

    @Override
    public ReplaceResult queryValues(Long projectId, Long appId, Long envId, Long appVersionId) {
        ReplaceResult replaceResult = new ReplaceResult();
        //从devops取得value
        String versionValue = testCaseService.getVersionValue(projectId,appVersionId);
        try {
            FileUtil.checkYamlFormat(versionValue);
        } catch (Exception e) {
            replaceResult.setYaml(versionValue);
            replaceResult.setErrorMsg(e.getMessage());
            replaceResult.setTotalLine(FileUtil.getFileTotalLine(replaceResult.getYaml()));
            replaceResult.setErrorLines(getErrorLine(e.getMessage()));
            return replaceResult;
        }
        String deployValue = FileUtil.checkValueFormat(
                instanceService.queryValueByEnvIdAndAppId(envId, appId));
        replaceResult.setYaml(versionValue);
        if (deployValue != null) {
            replaceResult = getReplaceResult(versionValue, deployValue);
        }
        return replaceResult;
    }

    private ReplaceResult getReplaceResult(String versionValue, String deployValue) {
        if (versionValue.equals(deployValue)) {
            ReplaceResult replaceResult = new ReplaceResult();
            replaceResult.setDeltaYaml("");
            replaceResult.setHighlightMarkers(new ArrayList<>());
            replaceResult.setNewLines(new ArrayList<>());
            return replaceResult;
        }

        String fileName = GenerateUUID.generateUUID() + ".yaml";
        String path = "deployfile";
        FileUtil.saveDataToFile(path, fileName, versionValue + "\n" + "---" + "\n" + deployValue);
        ReplaceResult replaceResult;
        try {
            replaceResult = FileUtil.replaceNew(path + System.getProperty("file.separator") + fileName);
        } catch (Exception e) {
            throw new CommonException(e.getMessage(), e);
        }
        if (replaceResult.getHighlightMarkers() == null) {
            replaceResult.setHighlightMarkers(new ArrayList<>());
        }
        replaceResult.setTotalLine(FileUtil.getFileTotalLine(replaceResult.getYaml()));
        FileUtil.deleteFile(path + System.getProperty("file.separator") + fileName);
        return replaceResult;
    }

    private List<ErrorLineDTO> getErrorLine(String value) {
        List<ErrorLineDTO> errorLines = new ArrayList<>();
        List<Long> lineNumbers = new ArrayList<>();
        String[] errorMsg = value.split("\\^");
        for (int i = 0; i < value.length(); i++) {
            int j;
            for (j = i; j < value.length(); j++) {
                if (value.substring(i, j).equals("line")) {
                    lineNumbers.add(TypeUtil.objToLong(value.substring(j, value.indexOf(',', j)).trim()));
                }
            }
        }
        for (int i = 0; i < lineNumbers.size(); i++) {
            ErrorLineDTO errorLineDTO = new ErrorLineDTO();
            errorLineDTO.setLineNumber(lineNumbers.get(i));
            errorLineDTO.setErrorMsg(errorMsg[i]);
            errorLines.add(errorLineDTO);
        }
        return errorLines;
    }

    @JobTask(code = SCHEDULECODE,
            level = ResourceLevel.PROJECT,
            maxRetryCount = 5, params = {
            @JobParam(name = "deployDTO"),
            @JobParam(name = "projectId", type = Long.class)
    })
    @Override
    public Map<String, TestAppInstanceDTO> createBySchedule(Map<String, Object> data) {
        String deployDTOString = (String) data.get(DEPLOYDTONAME);
        Long projectId = (Long) data.get("projectId");
        Map returnMap = new HashMap<String, TestAppInstanceDTO>();
        returnMap.put("AppInstanceDTO", create(JSON.toJavaObject(JSON.parseObject(deployDTOString), ApplicationDeployDTO.class), projectId));
        return returnMap;
    }

    @Override
    public QuartzTask createTimedTaskForDeploy(ScheduleTaskDTO taskDTO, Long projectId) {
        Assert.notNull(taskDTO.getParams().get(DEPLOYDTONAME), "error.deploy.param.deployDTO.not.be.null");
        ApplicationDeployDTO deployDTO = JSON.parseObject(JSON.toJSONString(taskDTO.getParams().get(DEPLOYDTONAME)), ApplicationDeployDTO.class);
        scheduleService.getMethodByService(projectId, "test-manager-service")
                .stream().filter(v -> v.getCode().equals(SCHEDULECODE))
                .findFirst()
                .ifPresent(v -> taskDTO.setMethodId(v.getId()));
        taskDTO.getParams().clear();
        taskDTO.getParams().put(DEPLOYDTONAME, deployDTO);
        taskDTO.getParams().put("projectId", projectId);

        String appName = testCaseService.queryByAppId(projectId,deployDTO.getAppId()).getName();
        String appVersionName = testCaseService.queryByAppId(projectId,deployDTO.getAppVerisonId()).getName();
        taskDTO.setName("test-deploy-" + appName +
                "-" + appVersionName + "-" + GenerateUUID.generateUUID());
        taskDTO.setDescription("测试应用："+ appName + "版本：" + appVersionName +"定时部署");
        return scheduleService.create(projectId, taskDTO);
    }


    @Override
    public TestAppInstanceDTO create(ApplicationDeployDTO deployDTO, Long projectId) {
        TestEnvCommand envCommand;
        TestEnvCommandValue commandValue;
        if (ObjectUtils.isEmpty(deployDTO.getHistoryId())) {
            //校验values
            FileUtil.checkYamlFormat(deployDTO.getValues());
            commandValue = new TestEnvCommandValue(getReplaceResult(testCaseService.getVersionValue(projectId,deployDTO.getAppVerisonId()), deployDTO.getValues()).getDeltaYaml().trim());
            envCommand = new TestEnvCommand(TestEnvCommand.CommandType.CREATE, commandValueService.insert(commandValue).getId());
        } else {
            //从history里面查instance，然后再去command里面找value，最后一个创建的value就是最新更改值
            TestEnvCommand needCommand = new TestEnvCommand();
            needCommand.setInstanceId(0L);
            TestEnvCommand retryCommand = commandService.queryEnvCommand(needCommand).get(0);
            commandValue = commandValueService.query(retryCommand.getValueId());
            envCommand = new TestEnvCommand(TestEnvCommand.CommandType.RESTART, commandValue.getId());
        }

        TestEnvCommand resultCommand = commandService.insertOne(envCommand);
        TestAppInstanceE instanceE = new TestAppInstanceE(deployDTO.getCode(), deployDTO.getAppVerisonId(), deployDTO.getProjectVersionId(),
                deployDTO.getEnvironmentId(), resultCommand.getId(), projectId, 0L);
        TestAppInstanceE resultInstance = instanceService.insert(instanceE);

        resultCommand.setInstanceId(resultInstance.getId());
        commandService.updateByPrimaryKey(resultCommand);

        //插入history，framework字段从value中获取
        //commandValue.getValue();

        //把更改值与从devops查到的值进行整合变为新的 配置value 传给devops 开始部署
        //调用devops传给它  releaseName: appId-appversionId-instanceid  deployDTO.getAppId()-deployDTO.getAppVerisonId()-instanceE.getId()

        return ConvertHelper.convert(resultInstance, TestAppInstanceDTO.class);
    }

}
