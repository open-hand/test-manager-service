//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.test.manager.api.vo.TestIssueFolderRelVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderRelE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestIssueFolderRelEFactory;
//import io.choerodon.test.manager.infra.vo.TestIssueFolderRelDTO;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by zongw.lee@gmail.com on 08/31/2018
// */
//@Component
//public class TestIssueFolderRelConvertor implements ConvertorI<TestIssueFolderRelE, TestIssueFolderRelDTO, TestIssueFolderRelVO> {
//    @Override
//    public TestIssueFolderRelE dtoToEntity(TestIssueFolderRelVO vo) {
//        TestIssueFolderRelE testIssueFolderRelE = TestIssueFolderRelEFactory.create();
//        BeanUtils.copyProperties(vo, testIssueFolderRelE);
//        return testIssueFolderRelE;
//    }
//
//    @Override
//    public TestIssueFolderRelVO entityToDto(TestIssueFolderRelE entity) {
//        TestIssueFolderRelVO testIssueFolderRelVO = new TestIssueFolderRelVO();
//        BeanUtils.copyProperties(entity, testIssueFolderRelVO);
//        return testIssueFolderRelVO;
//    }
//
//    @Override
//    public TestIssueFolderRelE doToEntity(TestIssueFolderRelDTO dataObject) {
//        TestIssueFolderRelE testIssueFolderRelE = TestIssueFolderRelEFactory.create();
//        BeanUtils.copyProperties(dataObject, testIssueFolderRelE);
//        return testIssueFolderRelE;
//    }
//
//    @Override
//    public TestIssueFolderRelDTO entityToDo(TestIssueFolderRelE entity) {
//        TestIssueFolderRelDTO testIssueFolderRelDTO = new TestIssueFolderRelDTO();
//        BeanUtils.copyProperties(entity, testIssueFolderRelDTO);
//        return testIssueFolderRelDTO;
//    }
//
//}
