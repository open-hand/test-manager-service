package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.test.manager.domain.service.ITestEnvCommandService;
import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommand;
import io.choerodon.test.manager.infra.mapper.TestEnvCommandMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by zongw.lee@gmail.com on 20/11/2018
 */
@Component
public class ITestEnvCommandServiceImpl implements ITestEnvCommandService {

    @Autowired
    TestEnvCommandMapper envCommandMapper;

    @Override
    public List<TestEnvCommand> queryEnvCommand(TestEnvCommand envCommand) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(0);
        pageRequest.setSize(99999999);
        pageRequest.setSort(new Sort(Sort.Direction.DESC, "creation_date"));
        return PageHelper.doPageAndSort(pageRequest, () -> envCommandMapper.select(envCommand));
    }

    @Override
    public void updateByPrimaryKey(TestEnvCommand envCommand) {
        envCommandMapper.updateByPrimaryKey(envCommand);
    }

    @Override
    public TestEnvCommand insertOne(TestEnvCommand envCommand) {
        if (envCommandMapper.insert(envCommand) == 0) {
            throw new CommonException("error.ITestEnvCommandValueServiceImpl.insert");
        }
        return envCommandMapper.selectByPrimaryKey(envCommand.getId());
    }
}
