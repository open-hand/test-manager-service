package io.choerodon.test.manager.infra.feign.operator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.vo.ExecutionUpdateIssueVO;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.api.vo.IssueQueryVO;
import io.choerodon.test.manager.api.vo.agile.*;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.feign.SprintClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author zhaotianxin
 * @date 2021-01-08 18:06
 */
@Component
public class AgileClientOperator {
    @Autowired
    private IssueFeignClient issueFeignClient;
    @Autowired
    private TestCaseFeignClient testCaseFeignClient;
    @Autowired
    private SprintClient sprintClient;
    @Autowired
    private ProductionVersionClient productionVersionClient;

    public List<IssueLinkVO> queryIssues(Long projectId, List<Long> issueIds) {
        return ResponseUtils.getResponse(issueFeignClient.queryIssues(projectId, issueIds), new TypeReference<List<IssueLinkVO>>() {
        });
    }

    public Page<IssueLinkVO> pagedQueryIssueByOptions(Long projectId, Integer page, Integer size, IssueQueryVO issueQueryV) {
        return ResponseUtils.getResponse(issueFeignClient.pagedQueryIssueByOptions(projectId, page, size, issueQueryV), new TypeReference<Page<IssueLinkVO>>() {
        });
    }

    public ProjectInfoVO queryProjectInfoByProjectId(Long projectId) {
        return ResponseUtils.getResponse(issueFeignClient.queryProjectInfoByProjectId(projectId), ProjectInfoVO.class);

    }

    public IssueDTO createIssue(Long projectId, String applyType, IssueCreateDTO issueCreateDTO) {
        return ResponseUtils.getResponse(testCaseFeignClient.createIssue(projectId, applyType, issueCreateDTO), IssueDTO.class);

    }

    public IssueDTO queryIssue(Long projectId, Long issueId, Long organizationId) {
        return ResponseUtils.getResponse(testCaseFeignClient.queryIssue(projectId, issueId, organizationId), IssueDTO.class);

    }

    public Page<IssueListTestVO> listIssueWithoutSubToTestComponent(Long projectId, SearchDTO searchDTO, Long organizationId, int page, int size, String sort) {
        return ResponseUtils.getResponse(testCaseFeignClient.listIssueWithoutSubToTestComponent(projectId, searchDTO, organizationId, page, size, sort), new TypeReference<Page<IssueListTestVO>>() {
        });
    }

    public List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds) {
        return ResponseUtils.getResponse(testCaseFeignClient.listByIssueIds(projectId, issueIds), new TypeReference<List<IssueInfoDTO>>() {
        });
    }

    public List<IssueLinkDTO> listIssueLinkByBatch(Long projectId, List<Long> issueIds) {
        return ResponseUtils.getResponse(testCaseFeignClient.listIssueLinkByBatch(projectId, issueIds), new TypeReference<List<IssueLinkDTO>>() {
        });
    }

    public Page<IssueComponentDetailVO> listIssueWithoutSubDetail(int page, int size, String orders, Long projectId, SearchDTO searchDTO, Long organizationId) {
        return ResponseUtils.getResponse(testCaseFeignClient.listIssueWithoutSubDetail(page, size, orders, projectId, searchDTO, organizationId), new TypeReference<Page<IssueComponentDetailVO>>() {
        });
    }

    public List<Long> queryIssueIdsByOptions(Long projectId, SearchDTO searchDTO) {
        return ResponseUtils.getResponse(testCaseFeignClient.queryIssueIdsByOptions(projectId, searchDTO), new TypeReference<List<Long>>() {
        });
    }

    public Page<IssueListTestWithSprintVersionDTO> listIssueWithLinkedIssues(int page, int size, String orders, Long projectId, SearchDTO searchDTO, Long organizationId) {
        return ResponseUtils.getResponse(testCaseFeignClient.listIssueWithLinkedIssues(page, size, orders, projectId, searchDTO, organizationId), new TypeReference<Page<IssueListTestWithSprintVersionDTO>>() {
        });
    }

    public List<IssueStatusDTO> listStatusByProjectId(Long projectId) {
        return ResponseUtils.getResponse(testCaseFeignClient.listStatusByProjectId(projectId), new TypeReference<List<IssueStatusDTO>>() {
        });
    }

    public LookupTypeWithValuesDTO queryLookupValueByCode(String typeCode) {
        return ResponseUtils.getResponse(testCaseFeignClient.queryLookupValueByCode(typeCode), LookupTypeWithValuesDTO.class);

    }

    public Page<IssueLinkTypeDTO> listIssueLinkType(Long projectId, Long issueLinkTypeId, IssueLinkTypeSearchDTO issueLinkTypeSearchDTO) {
        return ResponseUtils.getResponse(testCaseFeignClient.listIssueLinkType(projectId, issueLinkTypeId, issueLinkTypeSearchDTO), new TypeReference<Page<IssueLinkTypeDTO>>() {
        });
    }

    public IssueNumDTO queryIssueByIssueNum(Long projectId, String issueNum) {
        return ResponseUtils.getResponse(testCaseFeignClient.queryIssueByIssueNum(projectId, issueNum), IssueNumDTO.class);

    }

    public Page<IssueListFieldKVVO> queryListIssueWithSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        String orderStr = "";
        Iterator<Sort.Order> sortIterator = pageRequest.getSort().iterator();
        if (sortIterator.hasNext()) {
            Sort.Order order = sortIterator.next();
            orderStr = order.getProperty() + "," + order.getDirection();
        }
        String finalOrderStr = orderStr;
        return ResponseUtils.getResponse(testCaseFeignClient.queryListIssueWithSub(
                projectId, searchDTO,
                pageRequest.getPage(), pageRequest.getSize(), finalOrderStr,
                organizationId), new TypeReference<Page<IssueListFieldKVVO>>() {
        });

    }

    public Map<Long, SprintNameDTO> querySprintMapByProject(Long projectId) {
        List<SprintNameDTO> sprintList = ResponseUtils.getResponse(
                sprintClient.queryNameByOptions(projectId),
                new TypeReference<List<SprintNameDTO>>() {
                });
        return Optional.ofNullable(sprintList).orElse(Lists.newArrayList())
                .stream()
                .collect(Collectors.toMap(SprintNameDTO::getSprintId, Function.identity()));
    }

    public SprintNameDTO querySprintNameById(Long projectId, Long sprintId) {
        Map<Long, SprintNameDTO> sprintMap = querySprintMapByProject(projectId);
        return sprintMap.get(sprintId);
    }

    public Map<Long, ProductVersionDTO> queryProductVersionMapByProject(Long projectId) {
        List<ProductVersionDTO> productVersionList = ResponseUtils.getResponse(
                productionVersionClient.queryNameByOptions(projectId),
                new TypeReference<List<ProductVersionDTO>>() {
                });

        if (CollectionUtils.isEmpty(productVersionList)) {
            return new HashMap<>(0);
        }
        return productVersionList
                .stream()
                .collect(Collectors.toMap(ProductVersionDTO::getVersionId, Function.identity()));

    }

    public ProductVersionDTO queryProductVersionById(Long projectId, Long productVersionId) {
        Map<Long, ProductVersionDTO> productVersionMap = queryProductVersionMapByProject(projectId);
        return productVersionMap.get(productVersionId);
    }

    public void executionUpdateStatus(Long projectId, Long issueId, ExecutionUpdateIssueVO executionUpdateIssueVO) {
        ResponseUtils.getResponse(issueFeignClient.executionUpdateStatus(projectId, issueId, executionUpdateIssueVO), String.class);
    }
}
