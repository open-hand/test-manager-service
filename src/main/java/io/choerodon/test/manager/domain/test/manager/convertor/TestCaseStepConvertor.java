//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.test.manager.api.vo.TestCaseStepVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCaseStepEFactory;
//import io.choerodon.test.manager.infra.vo.TestCaseStepDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//@Component
//public class TestCaseStepConvertor implements ConvertorI<TestCaseStepE, TestCaseStepDTO, TestCaseStepVO> {
//    @Override
//    public TestCaseStepE doToEntity(TestCaseStepDTO dataObject) {
//        TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
//        BeanUtils.copyProperties(dataObject, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCaseStepDTO entityToDo(TestCaseStepE entity) {
//        TestCaseStepDTO stepDO = new TestCaseStepDTO();
//        BeanUtils.copyProperties(entity, stepDO);
//        return stepDO;
//    }
//
//    @Override
//    public TestCaseStepE dtoToEntity(TestCaseStepVO vo) {
//        TestCaseStepE testCaseStepE = TestCaseStepEFactory.create();
//        BeanUtils.copyProperties(vo, testCaseStepE);
//
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCaseStepVO entityToDto(TestCaseStepE entity) {
//        TestCaseStepVO stepDTO = new TestCaseStepVO();
//        BeanUtils.copyProperties(entity, stepDTO);
//        return stepDTO;
//    }
//}
