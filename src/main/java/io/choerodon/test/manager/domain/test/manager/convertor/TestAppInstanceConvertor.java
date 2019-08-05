//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.test.manager.api.vo.TestAppInstanceVO;
//import io.choerodon.test.manager.infra.vo.TestAppInstanceDTO;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by zongw.lee@gmail.com on 11/28/2018
// */
//@Component
//public class TestAppInstanceConvertor implements ConvertorI<TestAppInstanceDTO, TestAppInstanceDTO, TestAppInstanceVO> {
//    @Override
//    public TestAppInstanceDTO dtoToEntity(TestAppInstanceVO vo) {
//        TestAppInstanceDTO testAppInstanceDTO = new TestAppInstanceDTO();
//        BeanUtils.copyProperties(vo, testAppInstanceDTO);
//        return testAppInstanceDTO;
//    }
//
//    @Override
//    public TestAppInstanceVO entityToDto(TestAppInstanceDTO entity) {
//        TestAppInstanceVO testIssueFolderDTO = new TestAppInstanceVO();
//        BeanUtils.copyProperties(entity, testIssueFolderDTO);
//        return testIssueFolderDTO;
//    }
//
//}
