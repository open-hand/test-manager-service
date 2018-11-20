package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.domain.service.ITestAppInstanceLogService;
import io.choerodon.test.manager.domain.test.manager.entity.TestAppInstanceLogE;
import io.choerodon.test.manager.infra.mapper.TestAppInstanceLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ITestAppInstanceLogServiceImpl implements ITestAppInstanceLogService {
    
    @Autowired
    TestAppInstanceLogMapper mapper;
    
    @Override
    public List<TestAppInstanceLogE> query(TestAppInstanceLogE testAppInstanceE) {
        return mapper.select(testAppInstanceE);
    }

    @Override
    public TestAppInstanceLogE update(TestAppInstanceLogE testAppInstanceE) {
        if(mapper.updateByPrimaryKey(testAppInstanceE)==0){
            throw new CommonException("error.ITestAppInstanceLogServiceImpl.update");
        }
        return mapper.selectByPrimaryKey(testAppInstanceE.getId());
    }

    @Override
    public void delete(TestAppInstanceLogE testAppInstanceE) {
        mapper.delete(testAppInstanceE);
    }

    @Override
    public TestAppInstanceLogE insert(TestAppInstanceLogE testAppInstanceE) {
        if(mapper.insert(testAppInstanceE)==0){
            throw new CommonException("error.ITestAppInstanceLogServiceImpl.insert");
        }
        return mapper.selectByPrimaryKey(testAppInstanceE.getId());
    }
}
