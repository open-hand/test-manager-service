package io.choerodon.test.manager.domain.service.impl;

import java.util.List;

import io.choerodon.test.manager.domain.repository.TestIssueFolderRepository;
import io.choerodon.test.manager.domain.service.ITestIssueFolderService;
import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by zongw.lee@gmail.com on 08/30/2018
 */
@Component
public class ITestIssueFolderServiceImpl implements ITestIssueFolderService {

    @Autowired
    TestIssueFolderRepository issueFolderRepository;

    @Override
    public List<TestIssueFolderE> query(TestIssueFolderE testIssueFolderE) {
        return testIssueFolderE.queryAllUnderProject();
    }

    @Override
    public TestIssueFolderE insert(TestIssueFolderE testIssueFolderE) {
        return testIssueFolderE.validateType().addSelf();
    }

    @Override
    public void delete(TestIssueFolderE testIssueFolderE) {
        testIssueFolderE.deleteSelf();
    }

    @Override
    public TestIssueFolderE update(TestIssueFolderE testIssueFolderE) {
        return testIssueFolderE.validateType().updateSelf();
    }
    @Override
    public TestIssueFolderE updateWithNoType(TestIssueFolderE testIssueFolderE) {
        return testIssueFolderE.updateSelf();
    }

    @Override
    public TestIssueFolderE queryOne(TestIssueFolderE testIssueFolderE) {
        return testIssueFolderE.queryOne(testIssueFolderE);
    }

    @Override
    public TestIssueFolderE queryByPrimaryKey(TestIssueFolderE testIssueFolderE) {
        return testIssueFolderE.queryByPrimaryKey(testIssueFolderE.getFolderId());
    }

}
