package io.choerodon.test.manager.infra.repository.impl;

import java.util.List;
import java.util.Map;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.convertor.ConvertPageHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.repository.TestCycleCaseDefectRelRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseDefectRelDO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseDefectRelRepositoryImpl implements TestCycleCaseDefectRelRepository {
    @Autowired
    TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;


    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);
        if (testCycleCaseDefectRelMapper.insert(convert) != 1) {
            throw new CommonException("error.testStepCase.insert");
        }
        return ConvertHelper.convert(convert, TestCycleCaseDefectRelE.class);
    }

    @Override
    public void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);
        testCycleCaseDefectRelMapper.delete(convert);
    }

    @Override
    public TestCycleCaseDefectRelE update(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);
        if (testCycleCaseDefectRelMapper.updateByPrimaryKeySelective(convert) != 1) {
            throw new CommonException("error.testStepCase.update");
        }
        return ConvertHelper.convert(testCycleCaseDefectRelMapper.selectByPrimaryKey(convert.getId()), TestCycleCaseDefectRelE.class);
    }

    @Override
    public Page<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE, PageRequest pageRequest) {
        TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);

        Page<TestCycleCaseAttachmentRelDO> serviceDOPage = PageHelper.doPageAndSort(pageRequest,
                () -> testCycleCaseDefectRelMapper.select(convert));

        return ConvertPageHelper.convertPage(serviceDOPage, TestCycleCaseDefectRelE.class);
    }

    @Override
    public List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);

        return ConvertHelper.convertList(testCycleCaseDefectRelMapper.select(convert), TestCycleCaseDefectRelE.class);
    }

    @Override
    public List<TestCycleCaseDefectRelE> queryInIssues(Long[] issues) {
        Assert.notEmpty(issues, "error.query.issues.not.empty");
        return ConvertHelper.convertList(testCycleCaseDefectRelMapper.queryInIssues(issues), TestCycleCaseDefectRelE.class);
    }

    @Override
    public Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        TestCycleCaseDefectRelDO convert = ConvertHelper.convert(testCycleCaseDefectRelE, TestCycleCaseDefectRelDO.class);
        int count = testCycleCaseDefectRelMapper.updateProjectIdByIssueId(convert);
        if (log.isDebugEnabled()) {
            log.debug("fix defect data issueID {} updates num {}", convert.getIssueId(), count);
        }
        return true;
    }

    @Override
    public Map<Long, List<Long>> queryIssueIdAndDefectId(Long projectId) {
        return testCycleCaseDefectRelMapper.queryIssueIdAndDefectId(projectId);
    }

    @Override
    public List<Long> queryAllIssueIds() {
        return testCycleCaseDefectRelMapper.queryAllIssueIds();
    }
}
