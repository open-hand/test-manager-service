package io.choerodon.test.manager.app.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import io.choerodon.agile.api.vo.ProductVersionDTO;
import io.choerodon.agile.api.vo.UserDO;
import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.base.domain.Sort;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.IssueInfosVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
import io.choerodon.test.manager.app.service.*;
import io.choerodon.test.manager.infra.dto.*;
import io.choerodon.test.manager.infra.enums.TestAttachmentCode;
import io.choerodon.test.manager.infra.enums.TestCycleCaseDefectCode;
import io.choerodon.test.manager.infra.enums.TestStatusType;
import io.choerodon.test.manager.infra.mapper.*;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import io.choerodon.test.manager.infra.util.PageUtil;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseServiceImpl implements TestCycleCaseService {

    @Autowired
    private TestCycleCaseDefectRelService testCycleCaseDefectRelService;

    @Autowired
    private TestCycleCaseAttachmentRelService attachmentRelService;

    @Autowired
    private UserService userService;

    @Autowired
    private TestStatusService testStatusService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleMapper cycleMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestStatusMapper testStatusMapper;

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private TestCaseStepMapper testCaseStepMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long cycleCaseId, Long projectId) {
        TestCycleCaseVO dto = new TestCycleCaseVO();
        dto.setExecuteId(cycleCaseId);
        delete(modelMapper.map(dto, TestCycleCaseDTO.class));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchDelete(TestCycleCaseVO testCycleCaseVO, Long projectId) {
        List<TestCycleCaseDTO> list = testCycleCaseMapper.select(modelMapper.map(testCycleCaseVO, TestCycleCaseDTO.class));
        if (!ObjectUtils.isEmpty(list)) {
            list.forEach(v -> delete(v.getExecuteId(), projectId));
        }
    }

    @Override
    public PageInfo<TestCycleCaseVO> queryByCycle(TestCycleCaseVO dto, PageRequest pageRequest, Long projectId, Long organizationId) {

        //找到所有的子阶段
        List<TestCycleDTO> testCycleES = cycleMapper.queryChildCycle(modelMapper.map(dto, TestCycleDTO.class));
        //装配值进DTO中
        List<TestCycleCaseVO> testCycleCaseVOS = new ArrayList<>();
        testCycleCaseVOS.add(dto);
        testCycleES.forEach(v -> {
                    TestCycleCaseVO tempDTO = new TestCycleCaseVO();
                    tempDTO.setAssignedTo(dto.getAssignedTo());
                    tempDTO.setLastUpdatedBy(dto.getLastUpdatedBy());
                    tempDTO.setCycleId(v.getCycleId());
                    tempDTO.setCycleName(v.getCycleName());
                    testCycleCaseVOS.add(tempDTO);
                }
        );

        PageInfo<TestCycleCaseDTO> serviceEPage = queryByFatherCycle(modelMapper.map(testCycleCaseVOS,
                new TypeToken<List<TestCycleCaseDTO>>() {
                }.getType()), pageRequest);

        PageInfo<TestCycleCaseVO> dots = PageUtil.buildPageInfoWithPageInfoList(serviceEPage, modelMapper.map(serviceEPage.getList(), new TypeToken<List<TestCycleCaseVO>>() {
        }.getType()));
        List<TestCycleCaseVO> cycleCaseDTOS = dots.getList();
        Long[] issues = cycleCaseDTOS.stream().map(TestCycleCaseVO::getIssueId).toArray(Long[]::new);

        if (!ObjectUtils.isEmpty(dto.getSearchDTO())) {
            //先去敏捷筛选issue
            Map map = new HashMap<String, Long[]>();
            map.put("issueIds", issues);
            dto.getSearchDTO().setOtherArgs(map);
            Map<Long, IssueInfosVO> filterMap = testCaseService.getIssueInfoMap(projectId, dto.getSearchDTO(), false, organizationId);
            List<TestCycleCaseVO> filterCase = new ArrayList<>();
            //根据筛选出来的issueId,进行本地筛选
            for (TestCycleCaseVO caseDTO : cycleCaseDTOS) {
                if (filterMap.keySet().contains(caseDTO.getIssueId())) {
                    filterCase.add(caseDTO);
                }
            }
            dots.setList(filterCase);
        }

        populateCycleCaseWithDefect(dots.getList(), projectId, organizationId, true);
        populateUsers(dots.getList());
        return dots;
    }

    @Override
    public PageInfo<TestCycleCaseVO> queryByCycleWithFilterArgs(Long cycleId, PageRequest pageRequest, Long projectId, TestCycleCaseVO testCycleCaseVO) {
        testCycleCaseVO = Optional.ofNullable(testCycleCaseVO).orElseGet(TestCycleCaseVO::new);
        testCycleCaseVO.setCycleId(cycleId);
        PageInfo<TestCycleCaseDTO> serviceEPage = queryWithPageRequest(modelMapper.map(testCycleCaseVO, TestCycleCaseDTO.class), pageRequest);
        PageInfo<TestCycleCaseVO> dots = modelMapper.map(serviceEPage, new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());

        populateUsers(dots.getList());
        return dots;
    }

    @Override
    public List<TestCycleCaseVO> queryByIssuse(Long issueId, Long projectId, Long organizationId) {
        List<TestCycleCaseVO> dto = modelMapper.map(testCycleCaseMapper.queryByIssue(issueId), new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());
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
    public List<TestCycleCaseVO> queryInIssues(Long[] issueIds, Long projectId, Long organizationId) {
        if (issueIds == null || issueIds.length == 0) {
            return new ArrayList<>();
        }
        Assert.notEmpty(issueIds, "erorr.query.cycle.in.issues.issueIds.not.null");
        List<TestCycleCaseVO> dto = modelMapper.map(testCycleCaseMapper.queryInIssues(issueIds), new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());
        if (dto == null || dto.isEmpty()) {
            return new ArrayList<>();
        }
        populateCycleCaseWithDefect(dto, projectId, organizationId, false);
        return dto;
    }

    @Override
    public List<TestCycleCaseVO> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds, Long projectId, Long organizationId) {
        Assert.notNull(cycleIds, "error.query.case.in.versions.project.not.be.null");

        if (!(ObjectUtils.isEmpty(cycleIds) ^ ObjectUtils.isEmpty(versionIds))) {
            Assert.notEmpty(cycleIds, "erorr.query.cycle.in.issues.issueIds.not.null");
        }

        List<TestCycleCaseVO> dto = modelMapper.map(testCycleCaseMapper.queryCaseAllInfoInCyclesOrVersions(cycleIds, versionIds), new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());
        populateCycleCaseWithDefect(dto, projectId, organizationId, false);
        populateUsers(dto);
        return dto;
    }

    /**
     * 将实例查询的Issue信息和缺陷关联的Issue信息合并到一起，为了减少一次外部调用。
     *
     * @param testCycleCaseVOS
     * @param projectId
     */
    private void populateCycleCaseWithDefect(List<TestCycleCaseVO> testCycleCaseVOS, Long projectId, Long organizationId, Boolean needDetails) {
        List<TestCycleCaseDefectRelVO> list = new ArrayList<>();
        for (TestCycleCaseVO v : testCycleCaseVOS) {
            List<TestCycleCaseDefectRelVO> defects = v.getDefects();
            Optional.ofNullable(defects).ifPresent(list::addAll);
            Optional.ofNullable(v.getSubStepDefects()).ifPresent(list::addAll);
        }

        Long[] issueLists = Stream.concat(list.stream().map(TestCycleCaseDefectRelVO::getIssueId),
                testCycleCaseVOS.stream().map(TestCycleCaseVO::getIssueId)).filter(Objects::nonNull).distinct()
                .toArray(Long[]::new);
        if (ObjectUtils.isEmpty(issueLists)) {
            return;
        }
        Map<Long, IssueInfosVO> defectMap = testCaseService.getIssueInfoMap(projectId, issueLists, needDetails, organizationId);
        list.forEach(v -> v.setIssueInfosVO(defectMap.get(v.getIssueId())));
        testCycleCaseVOS.forEach(v -> v.setIssueInfosVO(defectMap.get(v.getIssueId())));
    }


    private void populateVersionBuild(Long projectId, List<TestCycleCaseVO> dto) {
        Map<Long, ProductVersionDTO> map = testCaseService.getVersionInfo(projectId);
        if (ObjectUtils.isEmpty(map)) {
            return;
        }

        for (TestCycleCaseVO cases : dto) {
            TestCycleDTO testCycleDTO = new TestCycleDTO();
            testCycleDTO.setCycleId(cases.getCycleId());

//            List<TestCycleCaseDTO> list = queryWithAttachAndDefect(modelMapper.map(dto, TestCycleCaseDTO.class), new PageRequest(1, 1));
//            DBValidateUtil.executeAndvalidateUpdateNum(list::size, 1, "error.cycle.case.query.not.found");

//            Long versionId = modelMapper.map(list.get(0), TestCycleCaseVO.class).getVersionId();
            Long versionId = cycleMapper.selectOne(testCycleDTO).getVersionId();
            Assert.notNull(versionId, "error.version.id.not.null");
            Optional.ofNullable(map.get(versionId)).ifPresent(v -> cases.setVersionName(v.getName()));
        }

    }

    @Override
    public TestCycleCaseVO queryOne(Long cycleCaseId, Long projectId, Long cycleId, Long organizationId) {
        TestCycleCaseVO sourceTestCycleCaseVO = new TestCycleCaseVO();
        sourceTestCycleCaseVO.setCycleId(cycleId);
        //找到所有的子阶段
        List<TestCycleDTO> testCycleES = cycleMapper.queryChildCycle(modelMapper.map(sourceTestCycleCaseVO, TestCycleDTO.class));

        TestCycleCaseVO testCycleCaseVO = new TestCycleCaseVO();
        testCycleCaseVO.setExecuteId(cycleCaseId);

        List<TestCycleCaseDTO> list = queryWithAttachAndDefect(modelMapper.map(testCycleCaseVO, TestCycleCaseDTO.class), new PageRequest(1, 1));
        DBValidateUtil.executeAndvalidateUpdateNum(list::size, 1, "error.cycle.case.query.not.found");

        TestCycleCaseVO dto = modelMapper.map(list.get(0), TestCycleCaseVO.class);
        dto.setCaseAttachment(modelMapper.map(list.get(0).getCaseAttachment(), new TypeToken<List<TestCycleCaseAttachmentRelVO>>() {
        }.getType()));
        testCycleCaseDefectRelService.populateDefectAndIssue(dto, projectId, organizationId);
        userService.populateTestCycleCaseDTO(dto);

        if (!testCycleES.isEmpty()) {
            if (dto.getLastExecuteId() == null) {
                PageRequest pageRequest = new PageRequest(1, 400);
                List<Sort.Order> sort = new ArrayList<>();
                sort.add(new Sort.Order(Sort.Direction.ASC, "cycle_id"));
                sort.add(new Sort.Order(Sort.Direction.DESC, "rank"));
                pageRequest.setSort(new Sort(sort));
                updateExecuteId(testCycleES, pageRequest, projectId, dto.getCycleId(), organizationId, dto, 0L);
            }
            if (dto.getNextExecuteId() == null) {
                PageRequest pageRequest = new PageRequest(1, 400);
                List<Sort.Order> sort = new ArrayList<>();
                sort.add(new Sort.Order(Sort.Direction.ASC, "cycle_id"));
                sort.add(new Sort.Order(Sort.Direction.ASC, "rank"));
                pageRequest.setSort(new Sort(sort));
                updateExecuteId(testCycleES, pageRequest, projectId, dto.getCycleId(), organizationId, dto, 1L);
            }
        }
        return dto;
    }

    private void updateExecuteId(List<TestCycleDTO> testCycleES, PageRequest pageRequest, Long projectId, Long cycleId, Long organizationId, TestCycleCaseVO dto, Long flag) {
        Long stageId = getTestStageId(testCycleES, flag, cycleId);
        TestCycleCaseVO tmpTestCycleCaseVO = new TestCycleCaseVO();
        Long targetExecuteId;

        if (stageId != 0) {
            tmpTestCycleCaseVO.setCycleId(stageId);
            PageInfo<TestCycleCaseVO> testCycleCaseDTOPage = queryByCycle(tmpTestCycleCaseVO, pageRequest, projectId, organizationId);
            if (flag == 0) {
                targetExecuteId = testCycleCaseDTOPage.getList().get(0).getExecuteId();
                dto.setLastExecuteId(targetExecuteId);
            } else {
                targetExecuteId = testCycleCaseDTOPage.getList().get(0).getExecuteId();
                dto.setNextExecuteId(targetExecuteId);
            }
        }
    }

    private Long getTestStageId(List<TestCycleDTO> testCycleES, Long flag, Long currentCycleId) {
        int index = 0;
        Long result;
        for (TestCycleDTO testCycleE : testCycleES) {
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

    private void populateUsers(List<TestCycleCaseVO> users) {
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
    public TestCycleCaseVO create(TestCycleCaseVO testCycleCaseVO, Long projectId) {
        if (ObjectUtils.isEmpty(testCycleCaseVO.getExecutionStatus())) {
            testCycleCaseVO.setExecutionStatus(testStatusService.getDefaultStatusId(TestStatusType.STATUS_TYPE_CASE));
        }
        testCycleCaseVO.setLastRank(testCycleCaseMapper.getLastedRank(testCycleCaseVO.getCycleId()));
        return runTestCycleCase(testCycleCaseVO, projectId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCycleCaseVO> batchCreateForAutoTest(List<TestCycleCaseVO> list, Long projectId) {
        if (list == null || list.isEmpty()) {
            throw new CommonException("error.cycle.case.list.empty");
        }
        Date now = new Date();
        List<TestCycleCaseDTO> testCycleCaseDTOS = new ArrayList<>();
        for (TestCycleCaseVO testCycleCaseVO : list) {
            if (testCycleCaseVO == null || testCycleCaseVO.getExecuteId() != null) {
                throw new CommonException("error.cycle.case.insert.executeId.should.be.null");
            }
            TestCycleCaseDTO testCycleCaseDTO = modelMapper.map(testCycleCaseVO, TestCycleCaseDTO.class);
            testCycleCaseDTO.setCreationDate(now);
            testCycleCaseDTO.setLastUpdateDate(now);
            testCycleCaseDTO.setProjectId(projectId);
            testCycleCaseDTOS.add(testCycleCaseDTO);
        }
        testCycleCaseMapper.batchInsertTestCycleCases(testCycleCaseDTOS);

        return modelMapper.map(testCycleCaseDTOS, new TypeToken<List<TestCycleCaseVO>>() {
        }.getType());
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseVO changeOneCase(TestCycleCaseVO testCycleCaseVO, Long projectId) {
        testStatusService.populateStatus(testCycleCaseVO);
        TestCycleCaseVO dto = modelMapper.map(changeStep(projectId, testCycleCaseVO), TestCycleCaseVO.class);
        userService.populateTestCycleCaseDTO(dto);
        return dto;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchChangeCase(Long projectId, List<TestCycleCaseVO> cycleCaseDTOS) {
        for (TestCycleCaseVO cycleCaseDTO : cycleCaseDTOS) {
            testStatusService.populateStatus(cycleCaseDTO);
            TestCycleCaseVO dto = modelMapper.map(changeStep(projectId, cycleCaseDTO), TestCycleCaseVO.class);
            userService.populateTestCycleCaseDTO(dto);
        }
    }


    @Override
    public List<Long> getActiveCase(Long range, Long projectId, String day) {
        List<Long> caseCountList = new ArrayList<>();
        LocalDate date = LocalDate.parse(day);
        for (int i = range.intValue() - 1; i >= 0; i--) {
            caseCountList.add(new RedisAtomicLong("summary:" + projectId + ":" + date.minusDays(i)
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    , redisTemplate.getConnectionFactory()).get());
        }
        return caseCountList;
    }

    @Override
    public Long countCaseNotRun(Long projectId) {
        List<Long> cycleIds = getCycleIdByProjectId(projectId);
        return ObjectUtils.isEmpty(cycleIds) ? 0L : testCycleCaseMapper.countCaseNotRun(cycleIds.stream().toArray(Long[]::new));
    }

    @Override
    public Long countCaseNotPlain(Long projectId) {
        List<Long> cycleIds = getCycleIdByProjectId(projectId);
        return ObjectUtils.isEmpty(cycleIds) ? 0L : testCycleCaseMapper.countCaseNotPlain(cycleIds.stream().toArray(Long[]::new));
    }

    @Override
    public Long countCaseSum(Long projectId) {
        List<Long> cycleIds = getCycleIdByProjectId(projectId);
        return ObjectUtils.isEmpty(cycleIds) ? 0L : testCycleCaseMapper.countCaseSum(cycleIds.stream().toArray(Long[]::new));
    }


    public void delete(TestCycleCaseDTO testCycleCaseDTO) {
        Optional.ofNullable(testCycleCaseMapper.select(testCycleCaseDTO)).ifPresent(m ->
                m.forEach(this::deleteCaseWithSubStep));
    }

    private void deleteCaseWithSubStep(TestCycleCaseDTO testCycleCaseDTO) {
        deleteByTestCycleCase(testCycleCaseDTO);
        attachmentRelService.delete(testCycleCaseDTO.getExecuteId(), TestAttachmentCode.ATTACHMENT_CYCLE_CASE);
        deleteLinkedDefect(testCycleCaseDTO.getExecuteId());
        testCycleCaseMapper.delete(testCycleCaseDTO);
    }

    private void deleteLinkedDefect(Long executeId) {
        TestCycleCaseDefectRelDTO testCycleCaseDefectRelDTO = new TestCycleCaseDefectRelDTO();
        testCycleCaseDefectRelDTO.setDefectLinkId(executeId);
        testCycleCaseDefectRelDTO.setDefectType(TestCycleCaseDefectCode.CYCLE_CASE);
        testCycleCaseDefectRelMapper.select(testCycleCaseDefectRelDTO).forEach(v -> testCycleCaseDefectRelMapper.delete(v));
    }

    private void deleteByTestCycleCase(TestCycleCaseDTO testCycleCaseDTO) {
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setExecuteId(testCycleCaseDTO.getExecuteId());
        deleteStep(testCycleCaseStepDTO);
    }

    private void deleteStep(TestCycleCaseStepDTO testCycleCaseStepDTO) {
        Optional.ofNullable(testCycleCaseStepMapper.select(testCycleCaseStepDTO)).ifPresent(
                m -> m.forEach(v -> {
                    attachmentRelService.delete(v.getExecuteStepId(), TestAttachmentCode.ATTACHMENT_CYCLE_STEP);
                    deleteLinkedDefect(v.getExecuteStepId());
                })
        );
        testCycleCaseStepMapper.delete(testCycleCaseStepDTO);
    }

    private PageInfo<TestCycleCaseDTO> queryByFatherCycle(List<TestCycleCaseDTO> testCycleCaseES, PageRequest pageRequest) {
        List<TestCycleCaseDTO> converts = modelMapper.map(testCycleCaseES, new TypeToken<List<TestCycleCaseDTO>>() {
        }.getType());
        List<TestCycleCaseDTO> dtos = queryByFatherCycleWithDataBase(converts, pageRequest);
        Long total = 0L;
        for (TestCycleCaseDTO convert : converts) {
            total += testCycleCaseMapper.queryWithAttachAndDefect_count(convert);
        }
        if (dtos.isEmpty() && total != 0L) {
            pageRequest.setPage((total.intValue() / pageRequest.getSize()) - 1);
            dtos = queryByFatherCycleWithDataBase(converts, pageRequest);
        }
        Page page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        page.setTotal(total);
        page.addAll(dtos);
        return page.toPageInfo();
    }

    private List<TestCycleCaseDTO> queryByFatherCycleWithDataBase(List<TestCycleCaseDTO> converts, PageRequest pageRequest) {
        return testCycleCaseMapper.queryByFatherCycleWithAttachAndDefect(converts,
                (pageRequest.getPage() - 1) * pageRequest.getSize(),
                pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort()));
    }

    private PageInfo<TestCycleCaseDTO> queryWithPageRequest(TestCycleCaseDTO testCycleCaseDTO, PageRequest pageRequest) {
        List<TestCycleCaseDTO> dto = queryWithAttachAndDefect(testCycleCaseDTO, pageRequest);
        return new PageInfo<>(Optional.ofNullable(dto).orElseGet(ArrayList::new));
    }

    public List<TestCycleCaseDTO> queryWithAttachAndDefect(TestCycleCaseDTO convert, PageRequest pageRequest) {
        return testCycleCaseMapper.queryWithAttachAndDefect(convert, (pageRequest.getPage() - 1) * pageRequest.getSize(), pageRequest.getSize());
    }

    private TestCycleCaseVO runTestCycleCase(TestCycleCaseVO testCycleCaseVO, Long projectId) {
        Assert.notNull(projectId, "error.projectId.illegal");

        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
        testCycleCaseDTO.setRank(RankUtil.Operation.INSERT.getRank(testCycleCaseVO.getLastRank(), testCycleCaseVO.getNextRank()));
        testCycleCaseDTO.setProjectId(projectId);
        testCycleCaseDTO.setCycleId(testCycleCaseVO.getCycleId());
        testCycleCaseDTO.setIssueId(testCycleCaseVO.getIssueId());
        testCycleCaseDTO.setExecutionStatus(testCycleCaseVO.getExecutionStatus());
        if (testCycleCaseMapper.validateCycleCaseInCycle(testCycleCaseDTO).longValue() > 0) {
            throw new CommonException("error.cycle.case.insert.have.one.case.in.cycle");
        }
        testCycleCaseMapper.insert(testCycleCaseDTO);
        createTestCycleCaseStep(testCycleCaseDTO);
        return modelMapper.map(testCycleCaseDTO, TestCycleCaseVO.class);
    }

    public void createTestCycleCaseStep(TestCycleCaseDTO testCycleCaseDTO) {
        TestCaseStepDTO testCaseStepDTO = new TestCaseStepDTO();
        testCaseStepDTO.setIssueId(testCycleCaseDTO.getIssueId());
        List<TestCaseStepDTO> testCaseStepES = testCaseStepMapper.query(testCaseStepDTO);
        TestCycleCaseStepDTO testCycleCaseStepDTO = new TestCycleCaseStepDTO();
        testCycleCaseStepDTO.setStepStatus(testStatusMapper.getDefaultStatus(TestStatusType.STATUS_TYPE_CASE_STEP));
        testCaseStepES.forEach(v -> {
            testCycleCaseStepDTO.setStepId(v.getStepId());
            testCycleCaseStepDTO.setExecuteId(testCycleCaseDTO.getExecuteId());
            testCycleCaseStepMapper.insert(testCycleCaseStepDTO);
        });
    }

    private TestCycleCaseDTO changeStep(Long projectId, TestCycleCaseVO testCycleCaseVO) {
        TestCycleCaseDTO testCycleCaseDTO = modelMapper.map(testCycleCaseVO, TestCycleCaseDTO.class);
        testCycleCaseDTO.setProjectId(projectId);
        if (!StringUtils.isEmpty(testCycleCaseVO.getLastRank()) || !StringUtils.isEmpty(testCycleCaseVO.getNextRank())) {
            testCycleCaseDTO.setRank(RankUtil.Operation.UPDATE.getRank(testCycleCaseVO.getLastRank(), testCycleCaseVO.getNextRank()));
        }
        Assert.notNull(testCycleCaseDTO.getProjectId(), "error.projectId.illegal");
        testCycleCaseMapper.updateByPrimaryKey(testCycleCaseDTO);
        return testCycleCaseDTO;
    }

    /**
     * 获取项目下所有cycleId
     *
     * @param projectId
     * @return
     */
    private List<Long> getCycleIdByProjectId(Long projectId) {
        Long[] versionIds = testCaseService.getVersionIds(projectId);
        if (ObjectUtils.isEmpty(versionIds)) {
            return new ArrayList<>();
        }
        return selectCyclesInVersions(versionIds);
    }


    private List<Long> selectCyclesInVersions(Long[] versionIds) {
        Assert.notNull(versionIds, "error.query.cycle.In.Versions.not.null");
        versionIds = Stream.of(versionIds).filter(Objects::nonNull).toArray(Long[]::new);

        if (versionIds.length > 0) {
            return cycleMapper.selectCyclesInVersions(versionIds);
        }
        return new ArrayList<>();
    }
}