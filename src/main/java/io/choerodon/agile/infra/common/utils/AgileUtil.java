package io.choerodon.agile.infra.common.utils;

import io.choerodon.agile.api.dto.IssueTypeDTO;
import io.choerodon.agile.infra.common.enums.IssueTypeCode;
import io.choerodon.agile.infra.common.enums.SchemeApplyType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @since 2019/1/24
 */
public class AgileUtil {

    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<Map<String, Object>>() {
        /**
         * ThreadLocal没有被当前线程赋值时或当前线程刚调用remove方法后调用get方法，返回此方法值
         */
        @Override
        protected Map<String, Object> initialValue() {
            return new HashMap<>();
        }
    };

    public static final String ISSUE_TYPE_MAP = "issueTypeMap";
    public static final String PRIORITY_ID = "priorityId";


    public static Long queryIssueTypeId(Long projectId, Long organizationId, String issueTypeCode) {
        Long issueTypeId;
        Object issueTypeMapObj = threadLocal.get().get(ISSUE_TYPE_MAP);
        if (issueTypeMapObj == null) {
            //获取自动化测试问题类型id
            IssueFeignClient issueFeignClient = SpringUtil.getApplicationContext().getBean(IssueFeignClient.class);
            Map<String, Long> issueTypeCodeMap = issueFeignClient.queryIssueType(projectId, SchemeApplyType.TEST, organizationId).getBody()
                    .stream().collect(Collectors.toMap(IssueTypeDTO::getTypeCode, IssueTypeDTO::getId));
            issueTypeId = issueTypeCodeMap.get(IssueTypeCode.ISSUE_AUTO_TEST);
            threadLocal.get().put(ISSUE_TYPE_MAP, issueTypeCodeMap);
        } else {
            issueTypeId = ((Map<String, Long>) issueTypeMapObj).get(issueTypeCode);
        }
        if (issueTypeId == null) {
            throw new CommonException("error.issueTypeId.notFound");
        }
        return issueTypeId;
    }

    public static Long queryDefaultPriorityId(Long projectId, Long organizationId) {
        Long defaultPriorityId;
        Object priorityIdObj = threadLocal.get().get(PRIORITY_ID);
        if (priorityIdObj == null) {
            //获取自动化测试问题类型id
            IssueFeignClient issueFeignClient = SpringUtil.getApplicationContext().getBean(IssueFeignClient.class);
            //获取默认优先级
            defaultPriorityId = issueFeignClient.queryDefaultPriority(projectId, organizationId).getBody().getId();
            threadLocal.get().put(PRIORITY_ID, defaultPriorityId);
        } else {
            defaultPriorityId = (Long) priorityIdObj;
        }
        if (defaultPriorityId == null) {
            throw new CommonException("error.defaultPriorityId.notFound");
        }
        return defaultPriorityId;
    }

}
