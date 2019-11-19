//package io.choerodon.test.manager.infra.repository.impl;
//
//import java.util.*;
//
//import com.github.pagehelper.Page;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.Assert;
//import org.springframework.util.ObjectUtils;
//import com.github.pagehelper.PageInfo;
//
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.convertor.ConvertPageHelper;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.data.domain.Pageable;
//import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
//import io.choerodon.test.manager.infra.util.DBValidateUtil;
//import io.choerodon.test.manager.infra.util.LiquibaseHelper;
//import io.choerodon.test.manager.infra.util.PageUtil;
//import io.choerodon.test.manager.infra.vo.TestCycleCaseDTO;
//import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//@Component
//public class TestCycleCaseRepositoryImpl implements TestCycleCaseRepository {
//    private static final Logger logger = LoggerFactory.getLogger(TestCycleCaseRepositoryImpl.class);
//
//    @Autowired
//    TestCycleCaseMapper testCycleCaseMapper;
//
//    @Value("${spring.datasource.url}")
//    private String dsUrl;
//
//    @Override
//    public TestCycleCaseE insert(TestCycleCaseE testCycleCaseE) {
//        TestCycleCaseDTO convert = modeMapper.map(testCycleCaseE, TestCycleCaseDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseMapper::insert, convert, 1, "error.testCycleCase.insert");
//        return modeMapper.map(convert, TestCycleCaseE.class);
//    }
//
//    @Override
//    public void delete(TestCycleCaseE testCycleCaseE) {
//        TestCycleCaseDTO convert = modeMapper.map(testCycleCaseE, TestCycleCaseDTO.class);
//        testCycleCaseMapper.delete(convert);
//    }
//
//    @Override
//    public TestCycleCaseE update(TestCycleCaseE testCycleCaseE) {
//        if (testCycleCaseE.getProjectId() == null) {
//            throw new CommonException("error.projectId.illegal");
//        }
//        TestCycleCaseDTO convert = modeMapper.map(testCycleCaseE, TestCycleCaseDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseMapper::updateByPrimaryKey, convert, 1, "error.testCycleCase.update");
//        return modeMapper.map(testCycleCaseMapper.selectByPrimaryKey(convert.getExecuteId()), TestCycleCaseE.class);
//    }
//
//    @Override
//    public PageInfo<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, Pageable pageable) {
//        TestCycleCaseDTO convert = modeMapper.map(testCycleCaseE, TestCycleCaseDTO.class);
//        List<TestCycleCaseDTO> vo = queryWithAttachAndDefect(convert, pageable);
////        Long total = 0L;
////        if (vo != null && !vo.isEmpty()) {
////            total = testCycleCaseMapper.queryWithAttachAndDefect_count(convert);
////        }
////        PageInfo info = new PageInfo(pageable.getPageNumber(), pageable.getPageSize());
//        PageInfo<TestCycleCaseDTO> page = new PageInfo<>(Optional.ofNullable(vo).orElseGet(ArrayList::new));
//        return ConvertPageHelper.convertPageInfo(page, TestCycleCaseE.class);
//    }
//
//    @Override
//    public PageInfo<TestCycleCaseE> queryByFatherCycle(List<TestCycleCaseE> testCycleCaseES, Pageable pageable) {
//        List<TestCycleCaseDTO> converts = ConvertHelper.convertList(testCycleCaseES, TestCycleCaseDTO.class);
//        List<TestCycleCaseDTO> dtos = queryByFatherCycleWithDataBase(converts, pageable);
//        Long total = 0L;
//        for (TestCycleCaseDTO convert : converts) {
//            total += testCycleCaseMapper.queryWithAttachAndDefect_count(convert);
//        }
//        if (dtos.isEmpty() && total != 0L) {
//            pageable.setPage((total.intValue() / pageable.getPageSize()) - 1);
//            dtos = queryByFatherCycleWithDataBase(converts, pageable);
//        }
////        PageInfo info = new PageInfo(pageable.getPageNumber(), pageable.getPageSize());
//        Page page = new Page<>(pageable.getPageNumber(), pageable.getPageSize());
//        page.setTotal(total);
//        page.addAll(dtos);
//        return ConvertPageHelper.convertPageInfo(page.toPageInfo(), TestCycleCaseE.class);
//    }
//
//    private List<TestCycleCaseDTO> queryByFatherCycleWithDataBase(List<TestCycleCaseDTO> converts, Pageable pageable) {
//        return testCycleCaseMapper.queryByFatherCycleWithAttachAndDefect(converts,
//                (pageable.getPageNumber()- 1) * pageable.getPageSize(),
//                pageable.getPageSize(), PageUtil.sortToSql(pageable.getSort()));
//    }
//
//    @Override
//    public List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE) {
//        TestCycleCaseDTO convert = modeMapper.map(testCycleCaseE, TestCycleCaseDTO.class);
//        return ConvertHelper.convertList(testCycleCaseMapper.select(convert), TestCycleCaseE.class);
//    }
//
//    private List<TestCycleCaseDTO> queryWithAttachAndDefect(TestCycleCaseDTO convert, Pageable pageable) {
//        return testCycleCaseMapper.queryWithAttachAndDefect(convert, (pageable.getPageNumber()- 1) * pageable.getPageSize(), pageable.getPageSize());
//    }
//
//    @Override
//    public TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE) {
//        TestCycleCaseDTO convert = modeMapper.map(testCycleCaseE, TestCycleCaseDTO.class);
//
//        List<TestCycleCaseDTO> list = queryWithAttachAndDefect(convert, PageRequest.of(1, 1));
//        DBValidateUtil.executeAndvalidateUpdateNum(list::size, 1, "error.cycle.case.query.not.found");
//        return modeMapper.map(list.get(0), TestCycleCaseE.class);
//    }
//
//    @Override
//    public List<TestCycleCaseE> filter(Map map) {
//        return ConvertHelper.convertList(testCycleCaseMapper.filter(map), TestCycleCaseE.class);
//    }
//
//    @Override
//    public List<TestCycleCaseE> queryByIssue(Long issueId) {
//
//        return ConvertHelper.convertList(testCycleCaseMapper.queryByIssue(issueId), TestCycleCaseE.class);
//
//    }
//
//    @Override
//    public List<TestCycleCaseE> queryInIssue(Long[] issueId) {
//        Assert.notEmpty(issueId, "erorr.query.cycle.in.issues.issueIds.not.null");
//        return ConvertHelper.convertList(testCycleCaseMapper.queryInIssues(issueId), TestCycleCaseE.class);
//
//    }
//
//    /**
//     * 查询versions下所有的Case
//     *
//     * @param
//     * @return
//     */
//    @Override
//    public List<TestCycleCaseE> queryCaseAllInfoInCyclesOrVersions(Long[] cycleIds, Long[] versionIds) {
//        if (!(ObjectUtils.isEmpty(cycleIds) ^ ObjectUtils.isEmpty(versionIds))) {
//            Assert.notEmpty(cycleIds, "erorr.query.cycle.in.issues.issueIds.not.null");
//        }
//        return ConvertHelper.convertList(testCycleCaseMapper.queryCaseAllInfoInCyclesOrVersions(cycleIds, versionIds), TestCycleCaseE.class);
//
//    }
//
//    @Override
//    public List<TestCycleCaseE> queryCycleCaseForReporter(Long[] issueIds) {
//        return ConvertHelper.convertList(testCycleCaseMapper.queryCycleCaseForReporter(issueIds), TestCycleCaseE.class);
//
//    }
//
//    @Override
//    public Long countCaseNotRun(Long[] cycleIds) {
//        return testCycleCaseMapper.countCaseNotRun(cycleIds);
//    }
//
//    @Override
//    public Long countCaseNotPlain(Long[] cycleIds) {
//        return testCycleCaseMapper.countCaseNotPlain(cycleIds);
//
//    }
//
//    @Override
//    public Long countCaseSum(Long[] cycleIds) {
//        return testCycleCaseMapper.countCaseSum(cycleIds);
//
//    }
//
//    @Override
//    public void validateCycleCaseInCycle(TestCycleCaseDTO testCycleCase) {
//        if (testCycleCaseMapper.validateCycleCaseInCycle(testCycleCase).longValue() > 0) {
//            throw new CommonException("error.cycle.case.insert.have.one.case.in.cycle");
//        }
//    }
//
//    @Override
//    public String getLastedRank(Long cycleId) {
//        return LiquibaseHelper.executeFunctionByMysqlOrOracle(testCycleCaseMapper::getLastedRank, testCycleCaseMapper::getLastedRank_oracle, dsUrl, cycleId);
//
//    }
//
//    @Override
//    public List<TestCycleCaseE> batchInsert(Long projectId, List<TestCycleCaseE> testCycleCases) {
//        if (testCycleCases == null || testCycleCases.isEmpty()) {
//            throw new CommonException("error.cycle.case.list.empty");
//        }
//        Date now = new Date();
//        for (TestCycleCaseE testCycleCaseE : testCycleCases) {
//            if (testCycleCaseE == null || testCycleCaseE.getExecuteId() != null) {
//                throw new CommonException("error.cycle.case.insert.executeId.should.be.null");
//            }
//            testCycleCaseE.setCreationDate(now);
//            testCycleCaseE.setLastUpdateDate(now);
//            testCycleCaseE.setProjectId(projectId);
//        }
//
//        List<TestCycleCaseDTO> testCycleCaseDTOS = ConvertHelper.convertList(testCycleCases, TestCycleCaseDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(
//                testCycleCaseMapper::batchInsertTestCycleCases, testCycleCaseDTOS, testCycleCaseDTOS.size(), "error.testCycleCase.batchInsert");
//
//        return ConvertHelper.convertList(testCycleCaseDTOS, TestCycleCaseE.class);
//    }
//
//}
