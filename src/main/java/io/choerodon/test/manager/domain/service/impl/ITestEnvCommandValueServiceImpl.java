//package io.choerodon.test.manager.domain.service.impl;
//
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.test.manager.domain.service.ITestEnvCommandValueService;
//import io.choerodon.test.manager.infra.vo.TestEnvCommandValueDTO;
//import io.choerodon.test.manager.infra.mapper.TestEnvCommandValueMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class ITestEnvCommandValueServiceImpl implements ITestEnvCommandValueService {
//
//    @Autowired
//    TestEnvCommandValueMapper mapper;
//    @Override
//    public TestEnvCommandValueDTO query(Long id) {
//        return mapper.selectByPrimaryKey(id);
//    }
//
//    @Override
//    public TestEnvCommandValueDTO update(TestEnvCommandValueDTO testEnvCommandValueDTO) {
//        if(mapper.updateByPrimaryKey(testEnvCommandValueDTO)==0){
//            throw new CommonException("error.ITestEnvCommandValueServiceImpl.update");
//        }
//        return mapper.selectByPrimaryKey(testEnvCommandValueDTO.getId());
//    }
//
//    @Override
//    public void delete(TestEnvCommandValueDTO testEnvCommandValueDTO) {
//         mapper.deleteByPrimaryKey(testEnvCommandValueDTO.getId());
//    }
//
//    @Override
//    public TestEnvCommandValueDTO insert(TestEnvCommandValueDTO testEnvCommandValueDTO) {
//        if(mapper.insert(testEnvCommandValueDTO)==0){
//            throw new CommonException("error.ITestEnvCommandValueServiceImpl.insert");
//        }
//        return mapper.selectByPrimaryKey(testEnvCommandValueDTO.getId());
//    }
//}
