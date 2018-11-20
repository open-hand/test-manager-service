package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.domain.service.ITestEnvCommandValueService;
import io.choerodon.test.manager.domain.test.manager.entity.TestEnvCommandValue;
import io.choerodon.test.manager.infra.mapper.TestEnvCommandValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ITestEnvCommandValueServiceImpl implements ITestEnvCommandValueService {

    @Autowired
    TestEnvCommandValueMapper mapper;
    @Override
    public List<TestEnvCommandValue> query(TestEnvCommandValue testEnvCommandValue) {
        return mapper.select(testEnvCommandValue);
    }

    @Override
    public TestEnvCommandValue update(TestEnvCommandValue testEnvCommandValue) {
        if(mapper.updateByPrimaryKey(testEnvCommandValue)==0){
            throw new CommonException("error.ITestEnvCommandValueServiceImpl.update");
        }
        return mapper.selectByPrimaryKey(testEnvCommandValue.getId());
    }

    @Override
    public void delete(TestEnvCommandValue testEnvCommandValue) {
         mapper.deleteByPrimaryKey(testEnvCommandValue.getId());
    }

    @Override
    public TestEnvCommandValue insert(TestEnvCommandValue testEnvCommandValue) {
        if(mapper.insert(testEnvCommandValue)==0){
            throw new CommonException("error.ITestEnvCommandValueServiceImpl.insert");
        }
        return mapper.selectByPrimaryKey(testEnvCommandValue.getId());
    }
}
