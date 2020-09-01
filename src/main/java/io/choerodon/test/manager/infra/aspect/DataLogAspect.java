package io.choerodon.test.manager.infra.aspect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.app.service.TestDataLogService;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.mapper.TestAttachmentMapper;
import io.choerodon.test.manager.infra.mapper.TestCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class DataLogAspect {
    private static final String SUMMARY_FIELD = "summary";
    private static final String DESCRIPTION = "description";
    private static final String FIELD_DESCRIPTION_NULL = "[{\"insert\":\"\n\"}]";
    private static final String FIELD_FOLDER = "Folder Link";
    private static final String FIELD_ATTACHMENT = "Attachment";

    public static final Logger LOGGER = LoggerFactory.getLogger(DataLogAspect.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestDataLogService testDataLogService;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;


    @Autowired
    private TestAttachmentMapper testAttachmentMapper;

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    /**
     * 定义拦截规则：拦截Spring管理的后缀为ServiceImpl的bean中带有@DataLog注解的方法。
     */
    @Pointcut(value = "bean(*ServiceImpl) && @annotation(io.choerodon.test.manager.infra.annotation.DataLog)")
    public void updateMethodPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("updateMethodPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        Object result = null;
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        //获取被拦截的方法
        Method method = signature.getMethod();
        DataLog dataLog = method.getAnnotation(DataLog.class);
        //获取被拦截的方法名
        Object[] args = pjp.getArgs();
        if (dataLog != null && args != null) {
            if (dataLog.single()) {
                switch (dataLog.type()) {
                    case DataLogConstants.CASE_UPDATE:
                        handleCaseDataLog(args);
                        break;
                    case DataLogConstants.CREATE_ATTACHMENT:
                        result = handleAttachmentCreateLog(pjp,args);
                        break;
                    case DataLogConstants.DELETE_ATTACHMENT:
                        handleAttachmentDeleteLog(args);
                        break;
                    default:
                        break;
                }
            } else {
                switch (dataLog.type()) {
                    case DataLogConstants.BATCH_MOVE:
                        handleCaseMoveFolder(args);
                        break;
                    case DataLogConstants.BATCH_DELETE_ATTACH:
                        handleCaseBatchDeleteAttach(args);
                        break;
                    case DataLogConstants.BATCH_INSERT_ATTACH:
                        handleCaseBatchInsertAttach(args);
                        break;
                    default:
                        break;
                }
            }
        }
        try {
            // 一切正常的情况下，继续执行被拦截的方法
            if (result == null) {
                result = pjp.proceed();
            }
        } catch (Throwable e) {
            throw new CommonException("error.dataLogEpic.methodExecute", e);
        }
        return result;
    }

    private void handleCaseBatchInsertAttach(Object[] args) {
        try {
            List<String> attachNames = new ArrayList<>();
            List<TestCaseAttachmentDTO> testCaseAttachmentDTOS = new ArrayList<>();
            for (Object arg : args) {
                if (arg instanceof List) {
                    testCaseAttachmentDTOS.addAll((List<TestCaseAttachmentDTO>) arg);
                }
                if (arg instanceof List) {
                    attachNames.addAll((List<String>) arg);
                }
            }

            if(!CollectionUtils.isEmpty(testCaseAttachmentDTOS)){
                testCaseAttachmentDTOS.stream().filter(v -> !attachNames.contains(v.getFileName())).forEach( v -> createDataLog(v.getProjectId(), v.getCaseId(), FIELD_ATTACHMENT, null, v.getUrl(), null, null));
             }
        } catch (Exception throwable) {
            LOGGER.error("handleCaseBatchInsertAttach e message:[{}], trace: [{}]", throwable.getMessage(), throwable.getStackTrace());
        }
    }

    private void handleCaseBatchDeleteAttach(Object[] args) {
        try {
            Long caseId = null;
            List<String> attachNames = new ArrayList<>();
            for (Object arg : args) {
                if (arg instanceof Long) {
                    caseId = (Long) arg;
                }
                if (arg instanceof List) {
                    attachNames.addAll((List<String>) arg);
                }
            }
            if (!ObjectUtils.isEmpty(caseId)) {
                List<TestCaseAttachmentDTO> attachmentDTOS = testAttachmentMapper.listByCaseIds(Arrays.asList(caseId));
                attachmentDTOS.stream().filter(v -> !attachNames.contains(v.getFileName())).forEach( v -> createDataLog(v.getProjectId(), v.getCaseId(), FIELD_ATTACHMENT, v.getUrl(), null, v.getAttachmentId().toString(), null));
            }
        } catch (Exception throwable) {
            LOGGER.error("handleCaseBatchDeleteAttach exception message:[{}], trace: [{}]", throwable.getMessage(), throwable.getStackTrace());
        }
    }

    private void handleAttachmentDeleteLog(Object[] args) {
        try {
            Long attachmentId = null;
            for (Object arg : args) {
                if (arg instanceof Long) {
                    attachmentId = (Long) arg;
                }
            }
            if (!ObjectUtils.isEmpty(attachmentId)) {
                TestCaseAttachmentDTO testCaseAttachmentDTO = testAttachmentMapper.selectByPrimaryKey(attachmentId);
                createDataLog(testCaseAttachmentDTO.getProjectId(), testCaseAttachmentDTO.getCaseId(), FIELD_ATTACHMENT, testCaseAttachmentDTO.getUrl(), null, testCaseAttachmentDTO.getAttachmentId().toString(), null);
            }
        } catch (Exception throwable) {
            LOGGER.error("handleAttachmentDeleteLog exception message:[{}], trace: [{}]", throwable.getMessage(), throwable.getStackTrace());
        }
    }

    private Object handleAttachmentCreateLog(ProceedingJoinPoint pjp,Object[] args) {
        Object result = null;
        try {
            TestCaseAttachmentDTO testCaseAttachmentDTO = null;
            for (Object arg : args) {
                if (arg instanceof TestCaseAttachmentDTO) {
                    testCaseAttachmentDTO = (TestCaseAttachmentDTO) arg;
                }
            }
            if (!ObjectUtils.isEmpty(testCaseAttachmentDTO)) {
                result = pjp.proceed();
                testCaseAttachmentDTO = (TestCaseAttachmentDTO) result;
                createDataLog(testCaseAttachmentDTO.getProjectId(), testCaseAttachmentDTO.getCaseId(), FIELD_ATTACHMENT, null, testCaseAttachmentDTO.getUrl(), null, testCaseAttachmentDTO.getAttachmentId().toString());
            }
        } catch (Throwable throwable) {
            LOGGER.error("handleAttachmentCreateLog exception message:[{}], trace: [{}]", throwable.getMessage(), throwable.getStackTrace());
        }

        return  result;
    }

    @SuppressWarnings("checkstyle:LineLength")
    private void handleCaseMoveFolder(Object[] args) {
        try {
            Long projectId = null;
            Long folderId = null;
            List<TestCaseRepVO> testCaseRepVOS = null;
            int i = 0;
            for (Object arg : args) {
                if (arg instanceof Long) {
                    if (i == 0) {
                        projectId = (Long) arg;
                    } else {
                        folderId = (Long) arg;
                    }
                    i++;
                } else if (arg instanceof List) {
                    testCaseRepVOS = (List<TestCaseRepVO>) arg;
                }
            }
            if (!CollectionUtils.isEmpty(testCaseRepVOS) && !ObjectUtils.isEmpty(projectId) && !ObjectUtils.isEmpty(folderId)) {
                TestIssueFolderDTO testIssueFolderDTO = testIssueFolderMapper.selectByPrimaryKey(folderId);
                Long finalProjectId = projectId;
                Long finalFolderId = folderId;
                testCaseRepVOS.forEach(v -> {
                    TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(v.getCaseId());
                    Long oldFolderId = testCaseDTO.getFolderId();
                    TestIssueFolderDTO olderFolder = testIssueFolderMapper.selectByPrimaryKey(oldFolderId);
                    createDataLog(finalProjectId, v.getCaseId(), FIELD_FOLDER, olderFolder.getName(), testIssueFolderDTO.getName(), String.valueOf(oldFolderId), String.valueOf(finalFolderId));
                });
            }

        } catch (Exception throwable) {
            LOGGER.error("handleCaseMoveFolder exception message:[{}], trace: [{}]", throwable.getMessage(), throwable.getStackTrace());
        }
    }

    private void handleCaseDataLog(Object[] args) {
        try {
            TestCaseRepVO testCaseRepVO = null;
            List<String> field = null;
            for (Object arg : args) {
                if (arg instanceof TestCaseRepVO) {
                    testCaseRepVO = (TestCaseRepVO) arg;
                } else if (arg instanceof String[]) {
                    field = Arrays.asList((String[]) arg);
                }
            }
            if (!ObjectUtils.isEmpty(testCaseRepVO) && !CollectionUtils.isEmpty(field)) {
                TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(testCaseRepVO.getCaseId());
                handleIssueSummary(field, testCaseDTO, testCaseRepVO);
                handleIssueDescription(field, testCaseDTO, testCaseRepVO);
                handleIssueFolder(field, testCaseDTO, testCaseRepVO);
            }
        } catch (Exception throwable) {
            LOGGER.error("handleCaseDataLog exception message:[{}], trace: [{}]", throwable.getMessage(), throwable.getStackTrace());
        }
    }

    private void createDataLog(Long projectId, Long issueId, String field, String oldString,
                               String newString, String oldValue, String newValue) {
        TestDataLogDTO dataLogDTO = new TestDataLogDTO();
        dataLogDTO.setProjectId(projectId);
        dataLogDTO.setCaseId(issueId);
        dataLogDTO.setField(field);
        dataLogDTO.setOldString(oldString);
        dataLogDTO.setNewString(newString);
        dataLogDTO.setOldValue(oldValue);
        dataLogDTO.setNewValue(newValue);
        testDataLogService.create(dataLogDTO);
    }

    private void handleIssueSummary(List<String> field, TestCaseDTO testCaseDTO, TestCaseRepVO testCaseRepVO) {
        if (field.contains(SUMMARY_FIELD) && !Objects.equals(testCaseDTO.getSummary(), testCaseRepVO.getSummary())) {
            createDataLog(testCaseDTO.getProjectId(), testCaseDTO.getCaseId(),
                    SUMMARY_FIELD, testCaseDTO.getSummary(), testCaseRepVO.getSummary(), null, null);
        }
    }

    private void handleIssueDescription(List<String> field, TestCaseDTO testCaseDTO, TestCaseRepVO testCaseRepVO) {
        if (field.contains(DESCRIPTION) && !Objects.equals(testCaseDTO.getDescription(), testCaseRepVO.getDescription())) {
            if (!FIELD_DESCRIPTION_NULL.equals(testCaseRepVO.getDescription())) {
                createDataLog(testCaseDTO.getProjectId(), testCaseDTO.getCaseId(),
                        DESCRIPTION, testCaseDTO.getDescription(), testCaseRepVO.getDescription(), null, null);
            } else {
                createDataLog(testCaseDTO.getProjectId(), testCaseDTO.getCaseId(),
                        DESCRIPTION, testCaseDTO.getDescription(), null, null, null);
            }
        }
    }

    private void handleIssueFolder(List<String> field, TestCaseDTO testCaseDTO, TestCaseRepVO testCaseRepVO) {
        if (field.contains(FIELD_FOLDER) && !Objects.equals(testCaseDTO.getFolderId(), testCaseRepVO.getFolderId())) {
            TestIssueFolderDTO olderFolder = testIssueFolderMapper.selectByPrimaryKey(testCaseDTO.getFolderId());
            TestIssueFolderDTO newFolder = testIssueFolderMapper.selectByPrimaryKey(testCaseRepVO.getFolderId());
            createDataLog(testCaseDTO.getProjectId(), testCaseDTO.getCaseId(),
                    "folder", olderFolder.getName(), newFolder.getName(), testCaseDTO.getFolderId().toString(), testCaseRepVO.getFolderId().toString());
        }
    }
}


