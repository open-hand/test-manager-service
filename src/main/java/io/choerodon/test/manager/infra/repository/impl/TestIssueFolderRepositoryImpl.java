package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.domain.repository.TestIssueFolderRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import io.choerodon.test.manager.infra.dataobject.TestIssueFolderDO;
import io.choerodon.test.manager.infra.exception.IssueFolderException;
import io.choerodon.test.manager.infra.mapper.TestIssueFolderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@Component
public class TestIssueFolderRepositoryImpl implements TestIssueFolderRepository {

    @Autowired
    TestIssueFolderMapper testIssueFolderMapper;

    @Override
    public List<TestIssueFolderE> queryAllUnderProject(TestIssueFolderE testIssueFolderE) {
        TestIssueFolderDO testIssueFolderDO = ConvertHelper.convert(testIssueFolderE, TestIssueFolderDO.class);
        return ConvertHelper.convertList(testIssueFolderMapper.select(testIssueFolderDO), TestIssueFolderE.class);
    }

    @Override
    public TestIssueFolderE queryOne(TestIssueFolderE testIssueFolderE) {
        TestIssueFolderDO testIssueFolderDO = ConvertHelper.convert(testIssueFolderE, TestIssueFolderDO.class);
        return ConvertHelper.convert(testIssueFolderMapper.selectOne(testIssueFolderDO), TestIssueFolderE.class);
    }

    @Override
    public TestIssueFolderE queryByPrimaryKey(Long folderId) {
        return ConvertHelper.convert(testIssueFolderMapper.selectByPrimaryKey(folderId), TestIssueFolderE.class);
    }


    @Override
    public TestIssueFolderE insert(TestIssueFolderE testIssueFolderE) {
        if (testIssueFolderE == null || testIssueFolderE.getFolderId() != null) {
            throw new CommonException("error.issue.folder.insert.folderId.should.be.null");
        }
        TestIssueFolderDO testIssueFolderDO = ConvertHelper.convert(testIssueFolderE, TestIssueFolderDO.class);
        testIssueFolderMapper.insert(testIssueFolderDO);
        return ConvertHelper.convert(testIssueFolderDO, TestIssueFolderE.class);
    }

    @Override
    public void delete(TestIssueFolderE testIssueFolderE) {
        TestIssueFolderDO testIssueFolderDO = ConvertHelper.convert(testIssueFolderE, TestIssueFolderDO.class);
        testIssueFolderMapper.delete(testIssueFolderDO);
    }

    @Override
    public TestIssueFolderE update(TestIssueFolderE testIssueFolderE) {
        TestIssueFolderDO testIssueFolderDO = ConvertHelper.convert(testIssueFolderE, TestIssueFolderDO.class);
        if (testIssueFolderMapper.updateByPrimaryKeySelective(testIssueFolderDO) != 1) {
            throw new IssueFolderException(IssueFolderException.ERROR_UPDATE, testIssueFolderDO.toString());
        }
        return ConvertHelper.convert(testIssueFolderMapper.selectByPrimaryKey(testIssueFolderDO.getFolderId()), TestIssueFolderE.class);
    }
}
