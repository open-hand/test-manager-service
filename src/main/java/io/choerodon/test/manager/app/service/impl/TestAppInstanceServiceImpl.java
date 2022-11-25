package io.choerodon.test.manager.app.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.vo.ApplicationDeployVO;
import io.choerodon.test.manager.api.vo.asgard.QuartzTask;
import io.choerodon.test.manager.api.vo.asgard.ScheduleTaskDTO;
import io.choerodon.test.manager.api.vo.devops.ErrorLineVO;
import io.choerodon.test.manager.api.vo.devops.InstanceValueVO;
import io.choerodon.test.manager.app.service.ScheduleService;
import io.choerodon.test.manager.app.service.TestAppInstanceService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.infra.dto.TestAppInstanceDTO;
import io.choerodon.test.manager.infra.dto.TestAppInstanceLogDTO;
import io.choerodon.test.manager.infra.dto.TestEnvCommandDTO;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.FileUtil;
import io.choerodon.test.manager.infra.util.GenerateUUID;
import io.choerodon.test.manager.infra.util.TypeUtil;

import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptionService;

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

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private ObjectMapper objectMapper;

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
     * 创建定时任务
     *
     * @param taskDTO
     * @param projectId
     * @return
     */
    @Override
    public QuartzTask createTimedTaskForDeploy(ScheduleTaskDTO taskDTO, Long projectId) {
        Assert.notNull(taskDTO.getParams().get(DEPLOYDTONAME), "error.deploy.param.deployDTO.not.be.null");
        String deployString =  decryptFields(taskDTO);
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

    private String decryptFields(ScheduleTaskDTO taskDTO){
        if (!EncryptContext.isEncrypt()){
            return JSON.toJSONString(taskDTO.getParams().get(DEPLOYDTONAME));
        }
        Map<String, Object> deployStringArray =  objectMapper.convertValue(taskDTO.getParams().get(DEPLOYDTONAME), Map.class);
        String appId = encryptionService.decrypt((String) deployStringArray.get("appId"), "");
        String appVersionId = encryptionService.decrypt((String) deployStringArray.get("appVersionId"), "");
        String environmentId = encryptionService.decrypt((String) deployStringArray.get("environmentId"), "");
        deployStringArray.put("appId", appId);
        deployStringArray.put("appVersionId", appVersionId);
        deployStringArray.put("environmentId", environmentId);
        try {
            return objectMapper.writeValueAsString(deployStringArray);
        } catch (JsonProcessingException e) {
            throw new CommonException("error.parse.object.to.string", e);
        }
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
        if (testAppInstanceLogMapper.insertTestAppInstanceLog(logE) == 0) {
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
        PageRequest pageRequest = new PageRequest(0, 99999999, Sort.Direction.DESC, "creation_date");
        Page<TestEnvCommandDTO> pageInfo = PageHelper.doPageAndSort(pageRequest,() -> envCommandMapper.select(envCommand));

        return pageInfo.getContent();
    }

    private TestEnvCommandDTO insertOne(TestEnvCommandDTO envCommand) {
        if (envCommandMapper.insert(envCommand) == 0) {
            throw new CommonException("error.ITestEnvCommandValueServiceImpl.insert");
        }
        return envCommandMapper.selectByPrimaryKey(envCommand.getId());
    }
}
