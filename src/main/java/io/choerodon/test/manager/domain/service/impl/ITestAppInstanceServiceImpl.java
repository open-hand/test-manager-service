//package io.choerodon.test.manager.domain.service.impl;
//
//import io.choerodon.core.exception.CommonException;
//import io.choerodon.test.manager.domain.service.ITestAppInstanceService;
//import io.choerodon.test.manager.infra.vo.TestAppInstanceDTO;
//import io.choerodon.test.manager.infra.mapper.TestAppInstanceMapper;
//import org.apache.commons.lang.time.DateUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.List;
//
//@Component
//public class ITestAppInstanceServiceImpl implements ITestAppInstanceService {
//
//    @Autowired
//    TestAppInstanceMapper mapper;
//
//    @Override
//    public List<TestAppInstanceDTO> queryDelayInstance(int delayTime) {
//        Date delayTiming=DateUtils.addMinutes(new Date(),-delayTime);
//        return mapper.queryDelayInstance(delayTiming);
//    }
//
//    @Override
//    public String queryValueByEnvIdAndAppId(Long envId, Long appId) {
//        return mapper.queryValueByEnvIdAndAppId(envId,appId);
//    }
//
//    @Override
//    public TestAppInstanceDTO update(TestAppInstanceDTO testAppInstanceDTO) {
//        if(mapper.updateByPrimaryKeySelective(testAppInstanceDTO)==0){
//            throw new CommonException("error.ItestAppInstanceEServiceImpl.update");
//        }
//        return mapper.selectByPrimaryKey(testAppInstanceDTO.getId());
//    }
//
//    @Override
//    public void delete(TestAppInstanceDTO testAppInstanceDTO) {
//        mapper.delete(testAppInstanceDTO);
//    }
//
//    @Override
//    public TestAppInstanceDTO insert(TestAppInstanceDTO testAppInstanceDTO) {
//        if(mapper.insert(testAppInstanceDTO)==0){
//            throw new CommonException("error.ITestAppInstanceServiceImpl.insert");
//        }
//        return mapper.selectByPrimaryKey(testAppInstanceDTO.getId());
//    }
//
//    @Override
//    public TestAppInstanceDTO queryOne(TestAppInstanceDTO id){
//       return mapper.selectOne(id);
//    }
//
//    @Override
//    public void updateInstanceWithoutStatus(TestAppInstanceDTO testAppInstanceDTO){
//        mapper.updateInstanceWithoutStatus(testAppInstanceDTO);
//    }
//
//    @Override
//    public void updateStatus(TestAppInstanceDTO testAppInstanceDTO){
//        mapper.updateStatus(testAppInstanceDTO);
//    }
//
//    @Override
//    public void closeInstance(TestAppInstanceDTO testAppInstanceDTO){
//        mapper.closeInstance(testAppInstanceDTO);
//    }
//
//}
