package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import io.choerodon.asgard.api.dto.QuartzTask;
import io.choerodon.asgard.api.dto.ScheduleTaskDTO;
import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.base.domain.Sort;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.devops.api.vo.AppServiceDeployVO;
import io.choerodon.devops.api.vo.ErrorLineVO;
import io.choerodon.devops.api.vo.InstanceValueVO;
import io.choerodon.devops.infra.common.utils.TypeUtil;
import io.choerodon.test.manager.api.vo.ApplicationDeployVO;
import io.choerodon.test.manager.api.vo.TestAppInstanceVO;
import io.choerodon.test.manager.app.service.ScheduleService;
import io.choerodon.test.manager.app.service.TestAppInstanceService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestAutomationHistoryEnums;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.FileUtil;
import io.choerodon.test.manager.infra.util.GenerateUUID;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zongw.lee@gmail.com on 22/11/2018
 */
@Component
public class TestAppInstanceServiceImpl implements TestAppInstanceService {

    private static final String SCHEDULECODE = "test-deploy-instance";
    private static final String DEPLOYDTONAME = "deploy";
    private static final String FRAMEWORKERROR = "error.values.framework.can.not.be.null";
    private static Logger logger = LoggerFactory.getLogger(TestAppInstanceServiceImpl.class);

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestAppInstanceMapper testAppInstanceMapper;

    @Autowired
    private TestAppInstanceLogMapper testAppInstanceLogMapper;

    @Autowired
    private TestEnvCommandMapper envCommandMapper;

    @Autowired
    private TestEnvCommandValueMapper testEnvCommandValueMapper;

    @Autowired
    private TestAutomationHistoryMapper testAutomationHistoryMapper;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * 查询value
     *
     * @param projectId
     * @param appId        应用Id
     * @param envId        环境Id
     * @param appVersionId 应用版本Id
     * @return
     */
    @Override
    public InstanceValueVO queryValues(Long projectId, Long appId, Long envId, Long appVersionId) {
        InstanceValueVO replaceResult = new InstanceValueVO();
        //从devops取得value
        String versionValue = testCaseService.getVersionValue(projectId, appVersionId);
        try {
            FileUtil.checkYamlFormat(versionValue);
        } catch (Exception e) {
            replaceResult.setYaml(versionValue);
            replaceResult.setErrorMsg(e.getMessage());
            replaceResult.setTotalLine(FileUtil.getFileTotalLine(replaceResult.getYaml()));
            replaceResult.setErrorLines(getErrorLine(e.getMessage()));
            return replaceResult;
        }
        String deployValue = FileUtil.checkValueFormat(testAppInstanceMapper.queryValueByEnvIdAndAppId(envId, appId));
        replaceResult.setYaml(versionValue);
        if (deployValue != null) {
            InstanceValueVO sendResult = new InstanceValueVO();
            sendResult.setYaml(deployValue);
            replaceResult = testCaseService.previewValues(projectId, sendResult, appVersionId);
        }
        return replaceResult;
    }

    /**
     * 接受定时任务调用
     *
     * @param data
     */
    @JobTask(code = SCHEDULECODE,
            level = ResourceLevel.PROJECT,
            description = "自动化测试任务-定时部署",
            maxRetryCount = 3, params = {
            @JobParam(name = DEPLOYDTONAME),
            @JobParam(name = "projectId", type = Long.class),
            @JobParam(name = "userId", type = Integer.class)
    })
    @Override
    public void createBySchedule(Map<String, Object> data) {
        logger.info("定时任务执行方法开始，时间{}", new Date());
        create(JSON.parseObject((String) data.get(DEPLOYDTONAME), ApplicationDeployVO.class),
                Long.valueOf((Integer) data.get("projectId")), Long.valueOf((Integer) data.get("userId")));
        logger.info("定时任务执行方法结束，时间{}", new Date());
    }

    /**
     * 创建定时任务
     *
     * @param taskDTO
     * @param projectId
     * @return
     */
    @Override
    public QuartzTask createTimedTaskForDeploy(ScheduleTaskDTO taskDTO, Long projectId) {
        Assert.notNull(taskDTO.getParams().get(DEPLOYDTONAME), "error.deploy.param.deployDTO.not.be.null");
        String deployString = JSON.toJSONString(taskDTO.getParams().get(DEPLOYDTONAME));
        ApplicationDeployVO deploy = JSON.parseObject(deployString, ApplicationDeployVO.class);
        scheduleService.getMethodByService(projectId, "test-manager-service")
                .stream().filter(v -> v.getCode().equals(SCHEDULECODE))
                .findFirst()
                .ifPresent(v -> taskDTO.setMethodId(v.getId()));
        taskDTO.getParams().clear();
        taskDTO.getParams().put(DEPLOYDTONAME, deployString);
        taskDTO.getParams().put("projectId", projectId);
        taskDTO.getParams().put("userId", DetailsHelper.getUserDetails().getUserId());

        String appName = testCaseService.queryByAppId(projectId, deploy.getAppId()).getName();
        String appVersion = testCaseService.getAppversion(projectId, Lists.newArrayList(deploy.getAppVersionId())).get(0).getVersion();
        taskDTO.setName("test-deploy-" + appName + "-" + appVersion + "-" + GenerateUUID.generateUUID());
        taskDTO.setDescription("测试应用：" + appName + "版本：" + appVersion + "定时部署");
        return scheduleService.create(projectId, taskDTO);
    }

    /**
     * 部署应用
     *
     * @param deployDTO 部署的信息
     * @param projectId
     * @param userId    部署用户
     * @return
     */
    @Override
    public TestAppInstanceVO create(ApplicationDeployVO deployDTO, Long projectId, Long userId) {
        DetailsHelper.setCustomUserDetails(userId, "zh_CN");

        Yaml yaml = new Yaml();
        TestEnvCommandDTO envCommand;
        TestEnvCommandValueDTO commandValue;
        InstanceValueVO replaceResult = new InstanceValueVO();
        InstanceValueVO sendResult = new InstanceValueVO();

        if (ObjectUtils.isEmpty(deployDTO.getHistoryId())) {
            sendResult.setYaml(deployDTO.getValues());
            Assert.notNull(deployDTO.getAppVersionId(), "error.deployDTO.appVerisonId.can.not.be.null");
            replaceResult = testCaseService.previewValues(projectId, sendResult, deployDTO.getAppVersionId());
            //校验values
            FileUtil.checkYamlFormat(deployDTO.getValues());
            Long commandValueId = null;
            //默认值是否已经改变
            if (!ObjectUtils.isEmpty(replaceResult.getDeltaYaml())) {
                commandValue = new TestEnvCommandValueDTO();
                commandValue.setValue(replaceResult.getDeltaYaml());
                if (testEnvCommandValueMapper.insert(commandValue) == 0) {
                    throw new CommonException("error.ITestEnvCommandValueServiceImpl.insert");
                }
                commandValueId = testEnvCommandValueMapper.selectByPrimaryKey(commandValue.getId()).getId();
            }
            envCommand = new TestEnvCommandDTO(TestEnvCommandDTO.CommandType.CREATE, commandValueId);
        } else {
            //从history里面查instance，然后再去command里面找value，最后一个创建的value就是最新更改值
            TestEnvCommandDTO needEnvCommand = new TestEnvCommandDTO();
            needEnvCommand.setInstanceId(testAutomationHistoryMapper.selectByPrimaryKey(deployDTO.getHistoryId()).getInstanceId());
            List<TestEnvCommandDTO> envCommands = queryEnvCommand(needEnvCommand);
            Assert.notNull(envCommands, "error.deploy.retry.envCommands.are.empty");
            TestEnvCommandDTO retryCommand = envCommands.get(0);

            //先去APPInstance查找appversionId
            TestAppInstanceDTO needInstance = new TestAppInstanceDTO();
            needInstance.setId(retryCommand.getInstanceId());
            TestAppInstanceDTO retryInstance = testAppInstanceMapper.selectOne(needInstance);
            deployDTO.setAppVersionId(retryInstance.getAppVersionId());
            deployDTO.setAppId(retryInstance.getAppId());
            deployDTO.setEnvironmentId(retryInstance.getEnvId());
            deployDTO.setCode(retryInstance.getCode());
            deployDTO.setProjectVersionId(retryInstance.getProjectVersionId());
            //重用EnvCommandValue表中以前的value数据
            if (!ObjectUtils.isEmpty(retryCommand.getValueId())) {
                commandValue = testEnvCommandValueMapper.selectByPrimaryKey(retryCommand.getValueId());
                envCommand = new TestEnvCommandDTO(TestEnvCommandDTO.CommandType.RESTART, commandValue.getId());

                TestEnvCommandValueDTO retryChangedValue = testEnvCommandValueMapper.selectByPrimaryKey(retryCommand.getValueId());
                sendResult.setYaml(retryChangedValue.getValue());
                replaceResult = testCaseService.previewValues(projectId, sendResult, retryInstance.getAppVersionId());
            } else {
                envCommand = new TestEnvCommandDTO(TestEnvCommandDTO.CommandType.RESTART, null);
                replaceResult.setYaml(testCaseService.getVersionValue(projectId, retryInstance.getAppVersionId()));
            }
        }
        TestEnvCommandDTO resultCommand = insertOne(envCommand);
        TestAppInstanceDTO instanceE = new TestAppInstanceDTO(deployDTO, resultCommand.getId(), projectId, 0L);
        if (testAppInstanceMapper.insert(instanceE) == 0) {
            throw new CommonException("error.ITestAppInstanceServiceImpl.insert");
        }
        TestAppInstanceDTO resultInstance = testAppInstanceMapper.selectByPrimaryKey(instanceE.getId());

        //回表EncCommand更新instanceId
        resultCommand.setInstanceId(resultInstance.getId());
        envCommandMapper.updateByPrimaryKey(resultCommand);

        Map result = yaml.loadAs(replaceResult.getYaml(), Map.class);
        Assert.notNull(result, FRAMEWORKERROR);
        String frameWork = (String) result.get("framework");
        TestAutomationHistoryDTO historyE = new TestAutomationHistoryDTO();
        historyE.setFramework(frameWork);
        historyE.setInstanceId(resultInstance.getId());
        historyE.setProjectId(projectId);
        historyE.setTestStatus(TestAutomationHistoryEnums.Status.NONEXECUTION);
        if (testAutomationHistoryMapper.insert(historyE) == 0) {
            throw new CommonException("error.ITestAutomationHistoryServiceImpl.insert");
        }

        //开始部署
        AppServiceDeployVO appServiceDeployVO = new AppServiceDeployVO(deployDTO.getAppVersionId(), deployDTO.getEnvironmentId(), replaceResult.getYaml(), deployDTO.getAppId(), deployDTO.getCommandType(), resultInstance.getId());
        testCaseService.deployTestApp(projectId, appServiceDeployVO);

        return modelMapper.map(resultInstance, TestAppInstanceVO.class);
    }

    private List<ErrorLineVO> getErrorLine(String value) {
        List<ErrorLineVO> errorLines = new ArrayList<>();
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
            ErrorLineVO errorLineDTO = new ErrorLineVO();
            errorLineDTO.setLineNumber(lineNumbers.get(i));
            errorLineDTO.setErrorMsg(errorMsg[i]);
            errorLines.add(errorLineDTO);
        }
        return errorLines;
    }

    /**
     * devops更新实例信息
     *
     * @param releaseNames
     * @param podName
     * @param conName
     */
    @Override
    public void updateInstance(String releaseNames, String podName, String conName) {

        TestAppInstanceDTO testAppInstanceDTO = new TestAppInstanceDTO();
        testAppInstanceDTO.setPodName(podName);
        testAppInstanceDTO.setContainerName(conName);
        //更新实例状态
        testAppInstanceDTO.setId(Long.valueOf(TestAppInstanceDTO.getInstanceIDFromReleaseName(releaseNames)));
        testAppInstanceDTO.setPodStatus(1L);
        testAppInstanceDTO.setLastUpdateDate(new Date());
        testAppInstanceMapper.updateInstanceWithoutStatus(testAppInstanceDTO);
        testAppInstanceMapper.updateStatus(testAppInstanceDTO);
    }

    @Override
    public void updateLog(String releaseNames, String logFile) {
        TestAppInstanceDTO testAppInstanceDTO = new TestAppInstanceDTO();
        testAppInstanceDTO.setId(Long.valueOf(TestAppInstanceDTO.getInstanceIDFromReleaseName(releaseNames)));
        TestAppInstanceLogDTO logE = new TestAppInstanceLogDTO();
        logE.setLog(logFile);
        if (testAppInstanceLogMapper.insert(logE) == 0) {
            throw new CommonException("error.ITestAppInstanceLogServiceImpl.insert");
        }
        testAppInstanceDTO.setLogId(logE.getId());
        testAppInstanceDTO.setLastUpdateDate(new Date());
        testAppInstanceMapper.closeInstance(testAppInstanceDTO);
    }

    @Override
    public void updateStatus(Long instanceId, Long status) {
        TestAppInstanceDTO testAppInstanceDTO = new TestAppInstanceDTO();
        //更新实例状态
        testAppInstanceDTO.setId(instanceId);
        testAppInstanceDTO.setPodStatus(status);
        testAppInstanceDTO.setLastUpdateDate(new Date());
        testAppInstanceMapper.updateStatus(testAppInstanceDTO);
    }

    private List<TestEnvCommandDTO> queryEnvCommand(TestEnvCommandDTO envCommand) {
        PageRequest pageRequest = new PageRequest(1, 99999999, Sort.Direction.DESC, "creation_date");
        PageInfo<TestEnvCommandDTO> pageInfo = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize()).doSelectPageInfo(() -> envCommandMapper.select(envCommand));

        return pageInfo.getList();
    }

    private TestEnvCommandDTO insertOne(TestEnvCommandDTO envCommand) {
        if (envCommandMapper.insert(envCommand) == 0) {
            throw new CommonException("error.ITestEnvCommandValueServiceImpl.insert");
        }
        return envCommandMapper.selectByPrimaryKey(envCommand.getId());
    }
}
