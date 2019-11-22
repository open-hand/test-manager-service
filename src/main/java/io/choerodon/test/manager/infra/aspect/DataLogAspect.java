package io.choerodon.test.manager.infra.aspect;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.TestCaseRepVO;
import io.choerodon.test.manager.api.vo.TestCaseVO;
import io.choerodon.test.manager.app.service.TestDataLogService;
import io.choerodon.test.manager.app.service.TestIssueFolderService;
import io.choerodon.test.manager.infra.annotation.DataLog;
import io.choerodon.test.manager.infra.constant.DataLogConstants;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.mapper.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

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
    private static final String FIELD_LABELS = "labels";
    private static final String FIELD_ATTACHMENT = "Attachment";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TestDataLogService testDataLogService;

    @Autowired
    private TestCaseMapper testCaseMapper;

    @Autowired
    private TestIssueFolderMapper testIssueFolderMapper;

    @Autowired
    private TestCaseLabelMapper testCaseLabelMapper;

    @Autowired
    private TestCaseLabelRelMapper testCaseLabelRelMapper;

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
                    case DataLogConstants.LABEL_CREATE:
                        handleLabelCreateLog(args);
                        break;
                    case DataLogConstants.LABEL_DELETE:
                        handleLabelDeleteLog(args);
                        break;
                    case DataLogConstants.CREATE_ATTACHMENT:
                        handleAttachmentCreateLog(args);
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

    private void handleAttachmentDeleteLog(Object[] args) {
        try {
            TestCaseAttachmentDTO testCaseAttachmentDTO = null;
            for (Object arg : args) {
                if (arg instanceof TestCaseAttachmentDTO) {
                    testCaseAttachmentDTO = (TestCaseAttachmentDTO) arg;
                }
            }
            if (!ObjectUtils.isEmpty(testCaseAttachmentDTO)) {
                createDataLog(testCaseAttachmentDTO.getProjectId(), testCaseAttachmentDTO.getCaseId(), FIELD_ATTACHMENT, testCaseAttachmentDTO.getFileName(), null, testCaseAttachmentDTO.getAttachmentId().toString(), null);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void handleAttachmentCreateLog(Object[] args) {
        try {
            TestCaseAttachmentDTO testCaseAttachmentDTO = null;
            for (Object arg : args) {
                if (arg instanceof TestCaseAttachmentDTO) {
                    testCaseAttachmentDTO = (TestCaseAttachmentDTO) arg;
                }
            }
            if (!ObjectUtils.isEmpty(testCaseAttachmentDTO)) {
                createDataLog(testCaseAttachmentDTO.getProjectId(), testCaseAttachmentDTO.getCaseId(), FIELD_ATTACHMENT, null, testCaseAttachmentDTO.getFileName(), null, null);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void handleLabelDeleteLog(Object[] args) {
        try {
            TestCaseLabelRelDTO testCaseLabelRelDTO = null;
            for (Object arg : args) {
                if (arg instanceof TestCaseLabelRelDTO) {
                    testCaseLabelRelDTO = (TestCaseLabelRelDTO) arg;
                }
            }
            if (!ObjectUtils.isEmpty(testCaseLabelRelDTO)) {
                TestCaseLabelDTO testCaseLabelDTO = testCaseLabelMapper.selectByPrimaryKey(testCaseLabelRelDTO.getLabelId());
                createDataLog(testCaseLabelRelDTO.getProjectId(), testCaseLabelRelDTO.getCaseId(), FIELD_LABELS, testCaseLabelDTO.getLabelName(), null, testCaseLabelRelDTO.getLabelId().toString(), null);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void handleLabelCreateLog(Object[] args) {
        try {
            TestCaseLabelRelDTO testCaseLabelRelDTO = null;
            for (Object arg : args) {
                if (arg instanceof TestCaseLabelRelDTO) {
                    testCaseLabelRelDTO = (TestCaseLabelRelDTO) arg;
                }
            }
            if (!ObjectUtils.isEmpty(testCaseLabelRelDTO)) {
                TestCaseLabelDTO testCaseLabelDTO = testCaseLabelMapper.selectByPrimaryKey(testCaseLabelRelDTO.getLabelId());
                createDataLog(testCaseLabelRelDTO.getProjectId(), testCaseLabelRelDTO.getCaseId(), FIELD_LABELS, null, testCaseLabelDTO.getLabelName(), null, testCaseLabelRelDTO.getLabelId().toString());
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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

        } catch (Throwable throwable) {
            throwable.printStackTrace();
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
            if (!ObjectUtils.isEmpty(testCaseRepVO) && !field.isEmpty()) {
                TestCaseDTO testCaseDTO = testCaseMapper.selectByPrimaryKey(testCaseRepVO.getCaseId());
                handleIssueSummary(field, testCaseDTO, testCaseRepVO);
                handleIssueDescription(field, testCaseDTO, testCaseRepVO);
                handleIssueFolder(field, testCaseDTO, testCaseRepVO);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private Object handleIssueCreateDataLog(ProceedingJoinPoint pjp, Object[] args) {
        Object result = null;
        try {
            result = pjp.proceed();
            TestCaseVO testCaseVO = null;
            Long projectId = null;
            for (Object arg : args) {
                if (arg instanceof TestCaseVO) {
                    testCaseVO = (TestCaseVO) arg;
                } else if (arg instanceof Long) {
                    projectId = (Long) arg;
                }
            }
            if (!ObjectUtils.isEmpty(testCaseVO)) {
                createDataLog(projectId, null, null, "sad", "sadad", "", "");
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return result;
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


