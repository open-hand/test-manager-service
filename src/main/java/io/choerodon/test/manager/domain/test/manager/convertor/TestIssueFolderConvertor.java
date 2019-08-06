//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.test.manager.api.vo.TestIssueFolderVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderEFactory;
//import io.choerodon.test.manager.infra.vo.TestIssueFolderDTO;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by zongw.lee@gmail.com on 08/30/2018
// */
//@Component
//public class TestIssueFolderConvertor implements ConvertorI<TestIssueFolderE, TestIssueFolderDTO, TestIssueFolderVO> {
//    @Override
//    public TestIssueFolderE dtoToEntity(TestIssueFolderVO vo) {
//        TestIssueFolderE testIssueFolderE = TestIssueFolderEFactory.create();
//        BeanUtils.copyProperties(vo, testIssueFolderE);
//        return testIssueFolderE;
//    }
//
//    @Override
//    public TestIssueFolderVO entityToDto(TestIssueFolderE entity) {
//        TestIssueFolderVO testIssueFolderVO = new TestIssueFolderVO();
//        BeanUtils.copyProperties(entity, testIssueFolderVO);
//        return testIssueFolderVO;
//    }
//
//    @Override
//    public TestIssueFolderE doToEntity(TestIssueFolderDTO dataObject) {
//        TestIssueFolderE testIssueFolderE = TestIssueFolderEFactory.create();
//        BeanUtils.copyProperties(dataObject, testIssueFolderE);
//        return testIssueFolderE;
//    }
//
//    @Override
//    public TestIssueFolderDTO entityToDo(TestIssueFolderE entity) {
//        TestIssueFolderDTO testIssueFolderDTO = new TestIssueFolderDTO();
//        BeanUtils.copyProperties(entity, testIssueFolderDTO);
//        return testIssueFolderDTO;
//    }
//
//}
