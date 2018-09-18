package io.choerodon.test.manager.infra.repository.impl;

import java.util.List;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.repository.TestIssueFolderRelRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderRelDO;
import io.choerodon.test.manager.infra.exception.IssueFolderException;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
@Component
public class TestIssueFolderRelRepositoryImpl implements TestIssueFolderRelRepository {

    @Autowired
    TestIssueFolderRelMapper testIssueFolderRelMapper;

    @Override
    public List<TestIssueFolderRelE> queryAllUnderProject(TestIssueFolderRelE testIssueFolderRelE) {
        TestIssueFolderRelDO testIssueFolderRelDO = ConvertHelper.convert(testIssueFolderRelE, TestIssueFolderRelDO.class);
        return ConvertHelper.convertList(testIssueFolderRelMapper.select(testIssueFolderRelDO), TestIssueFolderRelE.class);
    }

    @Override
    public TestIssueFolderRelE queryOneIssueUnderProjectVersionFolder(TestIssueFolderRelE testIssueFolderRelE) {
        TestIssueFolderRelDO testIssueFolderRelDO = ConvertHelper.convert(testIssueFolderRelE, TestIssueFolderRelDO.class);
        return ConvertHelper.convert(testIssueFolderRelMapper.selectOne(testIssueFolderRelDO), TestIssueFolderRelE.class);
    }

    @Override
    public TestIssueFolderRelE insert(TestIssueFolderRelE testIssueFolderRelE) {
        TestIssueFolderRelDO testIssueFolderRelDO = ConvertHelper.convert(testIssueFolderRelE, TestIssueFolderRelDO.class);
        testIssueFolderRelMapper.insert(testIssueFolderRelDO);
        return ConvertHelper.convert(testIssueFolderRelDO, TestIssueFolderRelE.class);
    }

    @Override
    public void delete(TestIssueFolderRelE testIssueFolderRelE) {
        TestIssueFolderRelDO testIssueFolderRelDO = ConvertHelper.convert(testIssueFolderRelE, TestIssueFolderRelDO.class);
        testIssueFolderRelMapper.delete(testIssueFolderRelDO);
    }

    @Override
    public TestIssueFolderRelE updateFolderByIssue(TestIssueFolderRelE testIssueFolderRelE) {
        TestIssueFolderRelDO testIssueFolderRelDO = ConvertHelper.convert(testIssueFolderRelE, TestIssueFolderRelDO.class);
        testIssueFolderRelMapper.updateFolderByIssue(testIssueFolderRelDO);
        return ConvertHelper.convert(testIssueFolderRelMapper.selectByPrimaryKey(testIssueFolderRelDO.getId()), TestIssueFolderRelE.class);
    }

    @Override
    public TestIssueFolderRelE updateVersionByFolderWithNoLock(TestIssueFolderRelE testIssueFolderRelE) {
        TestIssueFolderRelDO testIssueFolderRelDO = ConvertHelper.convert(testIssueFolderRelE, TestIssueFolderRelDO.class);
        testIssueFolderRelMapper.updateVersionByFolderWithNoLock(testIssueFolderRelDO);
        return ConvertHelper.convert(testIssueFolderRelMapper.selectByPrimaryKey(testIssueFolderRelDO.getId()), TestIssueFolderRelE.class);
    }
}
