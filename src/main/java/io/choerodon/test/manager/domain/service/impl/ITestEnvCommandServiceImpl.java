package io.choerodon.test.manager.domain.service.impl;

import java.util.List;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.base.domain.Sort;
import io.choerodon.core.exception.CommonException;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.domain.service.ITestEnvCommandService;
import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommand;
import io.choerodon.test.manager.infra.mapper.TestEnvCommandMapper;

/**
 * Created by zongw.lee@gmail.com on 20/11/2018
 */
@Component
public class ITestEnvCommandServiceImpl implements ITestEnvCommandService {

    @Autowired
    TestEnvCommandMapper envCommandMapper;

    @Override
    public List<TestEnvCommand> queryEnvCommand(TestEnvCommand envCommand) {
        PageRequest pageRequest = new PageRequest(1, 99999999, Sort.Direction.DESC, "creation_date");
        PageInfo<TestEnvCommand> pageInfo = PageHelper.startPage(pageRequest.getPage(),
                pageRequest.getSize()).doSelectPageInfo(() -> envCommandMapper.select(envCommand));

        return pageInfo.getList();
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
