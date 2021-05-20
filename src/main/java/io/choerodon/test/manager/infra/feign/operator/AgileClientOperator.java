package io.choerodon.test.manager.infra.feign.operator;

import com.fasterxml.jackson.core.type.TypeReference;
import io.choerodon.test.manager.api.vo.ExecutionUpdateIssueVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.ServiceUnavailableException;
import io.choerodon.core.utils.FeignClientUtils;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.vo.IssueLinkVO;
import io.choerodon.test.manager.api.vo.IssueQueryVO;
import io.choerodon.test.manager.api.vo.agile.*;
import io.choerodon.test.manager.infra.feign.IssueFeignClient;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.feign.SprintClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;

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
        return FeignClientUtils.doRequest(() -> issueFeignClient.queryIssues(projectId, issueIds), new TypeReference<List<IssueLinkVO>>() {
        });
    }

    public Page<IssueLinkVO> pagedQueryIssueByOptions(Long projectId, Integer page, Integer size, IssueQueryVO issueQueryV) {
        try {
            return FeignClientUtils.doRequest(() -> issueFeignClient.pagedQueryIssueByOptions(projectId, page, size, issueQueryV), new TypeReference<Page<IssueLinkVO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new Page<>();
        }
    }

    public ProjectInfoVO queryProjectInfoByProjectId(Long projectId) {
        try {
            return FeignClientUtils.doRequest(() -> issueFeignClient.queryProjectInfoByProjectId(projectId), ProjectInfoVO.class);
        } catch (ServiceUnavailableException e) {
            return null;
        }
    }

    public IssueDTO createIssue(Long projectId, String applyType, IssueCreateDTO issueCreateDTO) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.createIssue(projectId, applyType, issueCreateDTO), IssueDTO.class);
        } catch (ServiceUnavailableException e) {
            return null;
        }
    }

    public IssueDTO queryIssue(Long projectId, Long issueId, Long organizationId) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.queryIssue(projectId, issueId, organizationId), IssueDTO.class);
        } catch (ServiceUnavailableException e) {
            return null;
        }
    }

    public Page<IssueListTestVO> listIssueWithoutSubToTestComponent(Long projectId, SearchDTO searchDTO, Long organizationId, int page, int size, String sort) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.listIssueWithoutSubToTestComponent(projectId, searchDTO, organizationId, page, size, sort), new TypeReference<Page<IssueListTestVO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new Page<>();
        }
    }

    public List<IssueInfoDTO> listByIssueIds(Long projectId, List<Long> issueIds) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.listByIssueIds(projectId, issueIds), new TypeReference<List<IssueInfoDTO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new ArrayList<>();
        }
    }

    public List<IssueLinkDTO> listIssueLinkByBatch(Long projectId, List<Long> issueIds) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.listIssueLinkByBatch(projectId, issueIds), new TypeReference<List<IssueLinkDTO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new ArrayList<>();
        }
    }

    public Page<IssueComponentDetailVO> listIssueWithoutSubDetail(int page, int size, String orders, Long projectId, SearchDTO searchDTO, Long organizationId) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.listIssueWithoutSubDetail(page, size, orders, projectId, searchDTO, organizationId), new TypeReference<Page<IssueComponentDetailVO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new Page<>();
        }
    }

    public List<Long> queryIssueIdsByOptions(Long projectId, SearchDTO searchDTO) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.queryIssueIdsByOptions(projectId, searchDTO), new TypeReference<List<Long>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new ArrayList<>();
        }
    }

    public Page<IssueListTestWithSprintVersionDTO> listIssueWithLinkedIssues(int page, int size, String orders, Long projectId, SearchDTO searchDTO, Long organizationId) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.listIssueWithLinkedIssues(page, size, orders, projectId, searchDTO, organizationId), new TypeReference<Page<IssueListTestWithSprintVersionDTO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new Page<>();
        }
    }

    public List<IssueStatusDTO> listStatusByProjectId(Long projectId) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.listStatusByProjectId(projectId), new TypeReference<List<IssueStatusDTO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new ArrayList<>();
        }
    }

    public LookupTypeWithValuesDTO queryLookupValueByCode(String typeCode) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.queryLookupValueByCode(typeCode), LookupTypeWithValuesDTO.class);
        } catch (ServiceUnavailableException e) {
            return null;
        }
    }

    public Page<IssueLinkTypeDTO> listIssueLinkType(Long projectId, Long issueLinkTypeId, IssueLinkTypeSearchDTO issueLinkTypeSearchDTO) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.listIssueLinkType(projectId, issueLinkTypeId, issueLinkTypeSearchDTO), new TypeReference<Page<IssueLinkTypeDTO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return new Page<>();
        }
    }

    public IssueNumDTO queryIssueByIssueNum(Long projectId, String issueNum) {
        try {
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.queryIssueByIssueNum(projectId, issueNum), IssueNumDTO.class);
        } catch (ServiceUnavailableException e) {
            return null;
        }
    }

    public Page<IssueListFieldKVVO> queryListIssueWithSub(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId) {
        String orderStr = "";
        Iterator<Sort.Order> sortIterator = pageRequest.getSort().iterator();
        if (sortIterator.hasNext()) {
            Sort.Order order = sortIterator.next();
            orderStr = order.getProperty() + "," + order.getDirection();
        }
        try {
            String finalOrderStr = orderStr;
            return FeignClientUtils.doRequest(() -> testCaseFeignClient.queryListIssueWithSub(
                    projectId, searchDTO,
                    pageRequest.getPage(), pageRequest.getSize(), finalOrderStr,
                    organizationId), new TypeReference<Page<IssueListFieldKVVO>>() {
            });
        } catch (ServiceUnavailableException e) {
            return null;
        }
    }

    public Map<Long, SprintNameDTO> querySprintMapByProject(Long projectId) {
        List<SprintNameDTO> sprintList;
        try {
            sprintList = FeignClientUtils.doRequest(() ->
                            sprintClient.queryNameByOptions(projectId),
                    new TypeReference<List<SprintNameDTO>>() {
                    });
        } catch (ServiceUnavailableException e) {
            sprintList = new ArrayList<>();
        }
        return sprintList
                .stream()
                .collect(Collectors.toMap(SprintNameDTO::getSprintId, Function.identity()));
    }

    public SprintNameDTO querySprintNameById(Long projectId, Long sprintId) {
        Map<Long, SprintNameDTO> sprintMap = querySprintMapByProject(projectId);
        return sprintMap.get(sprintId);
    }

    public Map<Long, ProductVersionDTO> queryProductVersionMapByProject(Long projectId) {
        List<ProductVersionDTO> productVersionList;
        try {
            productVersionList = FeignClientUtils.doRequest(() ->
                            productionVersionClient.queryNameByOptions(projectId),
                    new TypeReference<List<ProductVersionDTO>>() {
                    });
        } catch (ServiceUnavailableException e) {
            productVersionList = new ArrayList<>();
        }
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
        try {
             FeignClientUtils.doRequest(() -> issueFeignClient.executionUpdateStatus(projectId, issueId, executionUpdateIssueVO), String.class);
        } catch (Exception e) {
            return;
        }
    }
}
