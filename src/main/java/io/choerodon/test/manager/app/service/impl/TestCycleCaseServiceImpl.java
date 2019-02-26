package io.choerodon.test.manager.app.service.impl;

import io.choerodon.agile.api.dto.ProductVersionDTO;
import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.domain.service.ITestCycleCaseService;
import io.choerodon.test.manager.domain.service.ITestCycleService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderEFactory;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import io.choerodon.test.manager.infra.feign.ProductionVersionClient;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseServiceImpl implements TestCycleCaseService {
    @Autowired
    ITestCycleCaseService iTestCycleCaseService;

    @Autowired
    ITestCycleService iTestCycleService;

    @Autowired
    TestCaseFeignClient testCaseFeignClient;

    @Autowired
    TestCaseService testCaseService;

    @Autowired
    ProductionVersionClient productionVersionClient;

    @Autowired
    TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    UserService userService;

    @Autowired
    TestStatusService testStatusService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long cycleCaseId, Long projectId) {
        TestCycleCaseDTO dto = new TestCycleCaseDTO();
        dto.setExecuteId(cycleCaseId);
        iTestCycleCaseService.delete(ConvertHelper.convert(dto, TestCycleCaseE.class));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchDelete(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
        List<TestCycleCaseE> list = ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class).querySelf();
        if (!ObjectUtils.isEmpty(list)) {
            list.forEach(v -> delete(v.getExecuteId(), projectId));
        }
    }

    @Override
    public Page<TestCycleCaseDTO> queryByCycle(TestCycleCaseDTO dto, PageRequest pageRequest, Long projectId, Long organizationId) {

        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleId(dto.getCycleId());
        //找到所有的子阶段
        List<TestCycleE> testCycleES = iTestCycleService.queryChildCycle(testCycleE);
        //装配值进DTO中
        List<TestCycleCaseDTO> testCycleCaseDTOS = new ArrayList<>();
        testCycleCaseDTOS.add(dto);
        testCycleES.forEach(v -> {
                    TestCycleCaseDTO tempDTO = new TestCycleCaseDTO();
                    tempDTO.setAssignedTo(dto.getAssignedTo());
                    tempDTO.setLastUpdatedBy(dto.getLastUpdatedBy());
                    tempDTO.setCycleId(v.getCycleId());
                    tempDTO.setCycleName(v.getCycleName());
                    testCycleCaseDTOS.add(tempDTO);
                }
        );

        Page<TestCycleCaseE> serviceEPage = iTestCycleCaseService.queryByFatherCycle(ConvertHelper.convertList(testCycleCaseDTOS, TestCycleCaseE.class), pageRequest);

        Page<TestCycleCaseDTO> dots = ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseDTO.class);
        List<TestCycleCaseDTO> cycleCaseDTOS = dots.getContent();
        Long[] issues = cycleCaseDTOS.stream().map(TestCycleCaseDTO::getIssueId).toArray(Long[]::new);

        if (!ObjectUtils.isEmpty(dto.getSearchDTO())) {
            //先去敏捷筛选issue
            Map map = new HashMap<String, Long[]>();
            map.put("issueIds", issues);
            dto.getSearchDTO().setOtherArgs(map);
            Map<Long, IssueInfosDTO> filterMap = testCaseService.getIssueInfoMap(projectId, dto.getSearchDTO(), false, organizationId);
            List<TestCycleCaseDTO> filterCase = new ArrayList<>();
            //根据筛选出来的issueId,进行本地筛选
            for (TestCycleCaseDTO caseDTO : cycleCaseDTOS) {
                if (filterMap.keySet().contains(caseDTO.getIssueId())) {
                    filterCase.add(caseDTO);
                }
            }
            dots.setContent(filterCase);
        }

        populateCycleCaseWithDefect(dots, projectId, organizationId, true);
        populateUsers(dots);
        return dots;
    }

    @Override
    public Page<TestCycleCaseDTO> queryByCycleWithFilterArgs(Long cycleId, PageRequest pageRequest, Long projectId, TestCycleCaseDTO searchDTO) {
        searchDTO = Optional.ofNullable(searchDTO).orElseGet(TestCycleCaseDTO::new);
        searchDTO.setCycleId(cycleId);
        Page<TestCycleCaseE> serviceEPage = iTestCycleCaseService.query(ConvertHelper.convert(searchDTO, TestCycleCaseE.class), pageRequest);
        Page<TestCycleCaseDTO> dots = ConvertPageHelper.convertPage(serviceEPage, TestCycleCaseDTO.class);

        populateUsers(dots);
        return dots;
    }

    @Override
    public List<TestCycleCaseDTO> queryByIssuse(Long issuseId, Long projectId, Long organizationId) {
        List<TestCycleCaseDTO> dto = ConvertHelper.convertList(iTestCycleCaseService.queryByIssue(issuseId), TestCycleCaseDTO.class);
        if (ObjectUtils.isEmpty(dto)) {
            return new ArrayList<>();
        }
        populateCycleCaseWithDefect(dto, projectId, organizationId, false);
        populateUsers(dto);
        populateVersionBuild(projectId, dto);
        return dto;
    }


    /**
     * 查询issues的cycleCase 在生成报表处使用
     *
     * @param issueIds
     * @param projectId
     * @return
     */
    @Override
    public List<TestCycleCaseDTO> queryInIssues(Long[] issueIds, Long projectId, Long organizationId) {
        if (issueIds == null || issueIds.length == 0) {
            return new ArrayList<>();
        }
        List<TestCycleCaseDTO> dto = ConvertHelper.convertList(iTestCycleCaseService.queryInIssues(issueIds), TestCycleCaseDTO.class);
        if (dto == null || dto.isEmpty()) {
            return new ArrayList<>();
        }
        populateCycleCaseWithDefect(dto, projectId, organizationId, false);
        return dto;
    }

    @Override
    public List<TestCycleCaseDTO> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds, Long projectId, Long organizationId) {
        Assert.notNull(cycleIds, "error.query.case.in.versions.project.not.be.null");

        List<TestCycleCaseDTO> dto = ConvertHelper.convertList(iTestCycleCaseService.queryCaseAllInfoInCyclesOrVersions(cycleIds, versionIds), TestCycleCaseDTO.class);
        populateCycleCaseWithDefect(dto, projectId, organizationId, false);
        populateUsers(dto);
        return dto;
    }

    /**
     * 将实例查询的Issue信息和缺陷关联的Issue信息合并到一起，为了减少一次外部调用。
     *
     * @param testCycleCaseDTOS
     * @param projectId
     */
    private void populateCycleCaseWithDefect(List<TestCycleCaseDTO> testCycleCaseDTOS, Long projectId, Long organizationId, Boolean needDetails) {
        List<TestCycleCaseDefectRelDTO> list = new ArrayList<>();
        for (TestCycleCaseDTO v : testCycleCaseDTOS) {
            List<TestCycleCaseDefectRelDTO> defects = v.getDefects();
            Optional.ofNullable(defects).ifPresent(list::addAll);
            Optional.ofNullable(v.getSubStepDefects()).ifPresent(list::addAll);
        }

        Long[] issueLists = Stream.concat(list.stream().map(TestCycleCaseDefectRelDTO::getIssueId),
                testCycleCaseDTOS.stream().map(TestCycleCaseDTO::getIssueId)).filter(Objects::nonNull).distinct()
                .toArray(Long[]::new);
        if (ObjectUtils.isEmpty(issueLists)) {
            return;
        }
        Map<Long, IssueInfosDTO> defectMap = testCaseService.getIssueInfoMap(projectId, issueLists, needDetails, organizationId);
        list.forEach(v -> v.setIssueInfosDTO(defectMap.get(v.getIssueId())));
        testCycleCaseDTOS.forEach(v -> v.setIssueInfosDTO(defectMap.get(v.getIssueId())));
    }


    private void populateVersionBuild(Long projectId, List<TestCycleCaseDTO> dto) {
        Map<Long, ProductVersionDTO> map = testCaseService.getVersionInfo(projectId);
        if (ObjectUtils.isEmpty(map)) {
            return;
        }
        TestCycleE cycleE = TestCycleEFactory.create();

        for (TestCycleCaseDTO cases : dto) {
            cycleE.setCycleId(cases.getCycleId());
            Long versionId = cycleE.queryOne().getVersionId();
            Assert.notNull(versionId, "error.version.id.not.null");
            Optional.ofNullable(map.get(versionId)).ifPresent(v -> cases.setVersionName(v.getName()));
        }

    }

    @Override
    public TestCycleCaseDTO queryOne(Long cycleCaseId, Long projectId, Long cycleId, Long organizationId) {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setCycleId(cycleId);
        //找到所有的子阶段
        List<TestCycleE> testCycleES = iTestCycleService.queryChildCycle(testCycleE);

        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setExecuteId(cycleCaseId);
        TestCycleCaseDTO dto = ConvertHelper.convert(iTestCycleCaseService.queryOne(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
        testCycleCaseDefectRelService.populateDefectAndIssue(dto, projectId, organizationId);
        userService.populateTestCycleCaseDTO(dto);

        if (!testCycleES.isEmpty()) {
            if (dto.getLastExecuteId() == null) {
                PageRequest pageRequest = new PageRequest();
                pageRequest.setPage(0);
                pageRequest.setSize(400);
                pageRequest.setSort(new Sort(Sort.Direction.DESC,"rank"));
                updateExecuteId(testCycleES, pageRequest, projectId, dto.getCycleId(), organizationId, dto, 0L);
            }
            if (dto.getNextExecuteId() == null) {
                PageRequest pageRequest = new PageRequest();
                pageRequest.setPage(0);
                pageRequest.setSize(400);
                updateExecuteId(testCycleES, pageRequest, projectId, dto.getCycleId(), organizationId, dto, 1L);
            }
        }
        return dto;
    }

    private void updateExecuteId(List<TestCycleE> testCycleES, PageRequest pageRequest, Long projectId, Long cycleId, Long organizationId, TestCycleCaseDTO dto, Long flag) {
        Long stageId = getTestStageId(testCycleES, flag, cycleId);
        TestCycleCaseDTO tmpTestCycleCaseDTO = new TestCycleCaseDTO();
        Long targetExecuteId;

        if (stageId != 0) {
            tmpTestCycleCaseDTO.setCycleId(stageId);
            Page<TestCycleCaseDTO> testCycleCaseDTOPage = queryByCycle(tmpTestCycleCaseDTO, pageRequest, projectId, organizationId);
            if (flag == 0){
                targetExecuteId = testCycleCaseDTOPage.get(0).getExecuteId();
                dto.setLastExecuteId(targetExecuteId);
            }else {
                targetExecuteId = testCycleCaseDTOPage.get(0).getExecuteId();
                dto.setNextExecuteId(targetExecuteId);
            }
        }
    }

    private Long getTestStageId(List<TestCycleE> testCycleES, Long flag, Long currentCycleId) {
        int index = 0;
        Long result;
        for (TestCycleE testCycleE : testCycleES) {
            if (testCycleE.getCycleId().equals(currentCycleId)) {
                break;
            }
            index++;
        }
        if (flag == 0L) {
            if (checkoutListLength(testCycleES.size(), index - 1)) {
                result = testCycleES.get(index - 1).getCycleId();
            } else return 0L;
        } else {
            if (checkoutListLength(testCycleES.size(), index + 1)) {
                result = testCycleES.get(index + 1).getCycleId();
            } else return 0L;
        }
        return result;
    }

    private boolean checkoutListLength(int size, int index) {
        return index >= 0 && index < size;
    }

    private void populateUsers(List<TestCycleCaseDTO> users) {
        List<Long> usersId = new ArrayList<>();
        users.stream().forEach(v -> {
            Optional.ofNullable(v.getAssignedTo()).ifPresent(usersId::add);
            Optional.ofNullable(v.getLastUpdatedBy()).ifPresent(usersId::add);
        });
        List<Long> ids = usersId.stream().distinct().filter(v -> !v.equals(Long.valueOf(0))).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(ids)) {
            Map<Long, UserDO> userMaps = userService.query(ids.toArray(new Long[ids.size()]));
            users.forEach(v -> {
                Optional.ofNullable(userMaps.get(v.getAssignedTo())).ifPresent(v::setAssigneeUser);
                Optional.ofNullable(userMaps.get(v.getLastUpdatedBy())).ifPresent(v::setLastUpdateUser);

            });
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseDTO create(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
        if (ObjectUtils.isEmpty(testCycleCaseDTO.getExecutionStatus())) {
            testCycleCaseDTO.setExecutionStatus(testStatusService.getDefaultStatusId(TestStatusE.STATUS_TYPE_CASE));
        }
        testCycleCaseDTO.setLastRank(TestCycleCaseEFactory.create().getLastedRank(testCycleCaseDTO.getCycleId()));
        return ConvertHelper.convert(iTestCycleCaseService.runTestCycleCase(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class), projectId), TestCycleCaseDTO.class);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCycleCaseE> batchCreateForAutoTest(List<TestCycleCaseDTO> list, Long projectId) {
        TestCycleCaseRepository repository = SpringUtil.getApplicationContext().getBean(TestCycleCaseRepository.class);
        return repository.batchInsert(ConvertHelper.convertList(list, TestCycleCaseE.class));
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseDTO changeOneCase(TestCycleCaseDTO testCycleCaseDTO, Long projectId) {
        testStatusService.populateStatus(testCycleCaseDTO);
        TestCycleCaseDTO dto = ConvertHelper.convert(iTestCycleCaseService.changeStep(ConvertHelper.convert(testCycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
        userService.populateTestCycleCaseDTO(dto);
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchChangeCase(List<TestCycleCaseDTO> cycleCaseDTOS) {
        for (TestCycleCaseDTO cycleCaseDTO : cycleCaseDTOS) {
            testStatusService.populateStatus(cycleCaseDTO);
            TestCycleCaseDTO dto = ConvertHelper.convert(iTestCycleCaseService.changeStep(ConvertHelper.convert(cycleCaseDTO, TestCycleCaseE.class)), TestCycleCaseDTO.class);
            userService.populateTestCycleCaseDTO(dto);
        }
    }


    @Override
    public List<Long> getActiveCase(Long range, Long projectId, String day) {
        return iTestCycleCaseService.getActiveCase(range, projectId, day);
    }

    @Override
    public Long countCaseNotRun(Long projectId) {
        return iTestCycleCaseService.countCaseNotRun(projectId);
    }

    @Override
    public Long countCaseNotPlain(Long projectId) {
        return iTestCycleCaseService.countCaseNotPlain(projectId);
    }

    @Override
    public Long countCaseSum(Long projectId) {
        return iTestCycleCaseService.countCaseSum(projectId);
    }


}