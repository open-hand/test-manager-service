//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.test.manager.api.vo.TestStatusVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestStatusEFactory;
//import io.choerodon.test.manager.infra.vo.TestStatusDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/25/18.
// */
//@Component
//public class TestStatusEConvertor implements ConvertorI<TestStatusE, TestStatusDTO, TestStatusVO> {
//    @Override
//    public TestStatusE dtoToEntity(TestStatusVO vo) {
//        TestStatusE testCaseStepE = TestStatusEFactory.create();
//        BeanUtils.copyProperties(vo, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestStatusVO entityToDto(TestStatusE entity) {
//        TestStatusVO testCycleCaseDTO = new TestStatusVO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO);
//        return testCycleCaseDTO;
//    }
//
//    @Override
//    public TestStatusE doToEntity(TestStatusDTO dataObject) {
//        TestStatusE testCaseStepE = TestStatusEFactory.create();
//        BeanUtils.copyProperties(dataObject, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestStatusDTO entityToDo(TestStatusE entity) {
//        TestStatusDTO testCycleCaseDTO = new TestStatusDTO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO);
//        return testCycleCaseDTO;
//    }
//
//}
