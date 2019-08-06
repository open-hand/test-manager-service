package io.choerodon.test.manager.infra.repository.impl;

import java.util.*;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import com.github.pagehelper.PageInfo;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.infra.common.utils.DBValidateUtil;
import io.choerodon.test.manager.infra.common.utils.LiquibaseHelper;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseStepDO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseStepRepositoryImpl implements TestCycleCaseStepRepository {
    @Autowired
    TestCycleCaseStepMapper testCycleCaseStepMapper;
    @Value("${spring.datasource.url}")
    private String dsUrl;

    @Override
    public TestCycleCaseStepE insert(TestCycleCaseStepE testCycleCaseStepE) {
        Assert.notNull(testCycleCaseStepE, "error.test.cycle.step.insert.param.not.null");

        TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseStepMapper::insert, convert, 1, "error.testStepCase.insert");
        return ConvertHelper.convert(convert, TestCycleCaseStepE.class);
    }

    @Override
    public void delete(TestCycleCaseStepE testCycleCaseStepE) {
        Assert.notNull(testCycleCaseStepE, "error.test.cycle.step.delete.param.not.null");
        TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
        testCycleCaseStepMapper.delete(convert);
    }

    @Override
    public TestCycleCaseStepE update(TestCycleCaseStepE testCycleCaseStepE) {
        Assert.notNull(testCycleCaseStepE, "error.test.cycle.step.update.param.not.null");

        TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
        if (testCycleCaseStepMapper.updateByPrimaryKeySelective(convert) != 1) {
            throw new CommonException("error.testStepCase.update");
        }
        return ConvertHelper.convert(testCycleCaseStepMapper.selectByPrimaryKey(convert.getExecuteStepId()), TestCycleCaseStepE.class);
    }

    @Override
    public PageInfo<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE, PageRequest pageRequest) {
        if (!(testCycleCaseStepE != null && testCycleCaseStepE.getExecuteId() != null)) {
            throw new CommonException("error.test.cycle.case.step.caseId.not.null");
        }
        TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);

        List<TestCycleCaseStepDO> dto = LiquibaseHelper.executeBiFunctionByMysqlOrOracle(this::queryWithTestCaseStep_mysql, this::queryWithTestCaseStep_oracle, dsUrl, convert, pageRequest);
        Long total = 0L;
        if (dto != null && !dto.isEmpty()) {
            total = testCycleCaseStepMapper.queryWithTestCaseStep_count(testCycleCaseStepE.getExecuteId());
        }
//        PageInfo info = new PageInfo(pageRequest.getPage(), pageRequest.getSize());
        PageInfo<TestCycleCaseStepDO> page = new PageInfo<>(Optional.ofNullable(dto).orElseGet(ArrayList::new));

        return ConvertPageHelper.convertPageInfo(page, TestCycleCaseStepE.class);
    }

    private List<TestCycleCaseStepDO> queryWithTestCaseStep_mysql(TestCycleCaseStepDO convert, PageRequest pageRequest) {
        return testCycleCaseStepMapper.queryWithTestCaseStep(convert, pageRequest.getPage() * pageRequest.getSize(), pageRequest.getSize());
    }

    private List<TestCycleCaseStepDO> queryWithTestCaseStep_oracle(TestCycleCaseStepDO convert, PageRequest pageRequest) {
        return testCycleCaseStepMapper.queryWithTestCaseStep_oracle(convert, pageRequest.getPage() * pageRequest.getSize(), pageRequest.getSize());
    }

    @Override
    public List<TestCycleCaseStepE> query(TestCycleCaseStepE testCycleCaseStepE) {
        TestCycleCaseStepDO convert = ConvertHelper.convert(testCycleCaseStepE, TestCycleCaseStepDO.class);
        List<TestCycleCaseStepDO> dto = testCycleCaseStepMapper.select(convert);
        return ConvertHelper.convertList(dto, TestCycleCaseStepE.class);
    }

    @Override
    public List<TestCycleCaseStepE> queryCycleCaseForReporter(Long[] ids) {
        Assert.notNull(ids, "error.queryCycleCaseForReporter.ids.not.null");
        ids = Stream.of(ids).filter(Objects::nonNull).toArray(Long[]::new);
        return ConvertHelper.convertList(testCycleCaseStepMapper.queryCycleCaseForReporter(ids), TestCycleCaseStepE.class);
    }

    @Override
    public List<TestCycleCaseStepE> batchInsert(List<TestCycleCaseStepE> testCycleCaseSteps) {
        if (testCycleCaseSteps == null || testCycleCaseSteps.isEmpty()) {
            throw new CommonException("error.cycle.case.step.list.empty");
        }
        Date now = new Date();
        for (TestCycleCaseStepE testCycleCaseStep : testCycleCaseSteps) {
            if (testCycleCaseStep == null || testCycleCaseStep.getExecuteStepId() != null) {
                throw new CommonException("error.cycle.case.step.insert.executeStepId.should.be.null");
            }
            testCycleCaseStep.setCreationDate(now);
            testCycleCaseStep.setLastUpdateDate(now);
        }

        List<TestCycleCaseStepDO> testCycleCaseStepDOs = ConvertHelper.convertList(testCycleCaseSteps, TestCycleCaseStepDO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(
                testCycleCaseStepMapper::batchInsertTestCycleCaseSteps, testCycleCaseStepDOs, testCycleCaseStepDOs.size(), "error.testCycleCaseStep.batchInsert");

        return ConvertHelper.convertList(testCycleCaseStepDOs, TestCycleCaseStepE.class);
    }

}
