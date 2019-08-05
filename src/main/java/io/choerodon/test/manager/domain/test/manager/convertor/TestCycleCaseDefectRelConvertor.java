//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.test.manager.api.vo.TestCycleCaseDefectRelVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
//import io.choerodon.test.manager.infra.vo.TestCycleCaseDefectRelDTO;
//import io.choerodon.core.convertor.ConvertorI;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/12/18.
// */
//@Component
//public class TestCycleCaseDefectRelConvertor implements ConvertorI<TestCycleCaseDefectRelE, TestCycleCaseDefectRelDTO, TestCycleCaseDefectRelVO> {
//    @Override
//    public TestCycleCaseDefectRelE dtoToEntity(TestCycleCaseDefectRelVO vo) {
//        TestCycleCaseDefectRelE testCaseStepE = TestCycleCaseDefectRelEFactory.create();
//        BeanUtils.copyProperties(vo, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseDefectRelVO entityToDto(TestCycleCaseDefectRelE entity) {
//        TestCycleCaseDefectRelVO testCycleCaseDTO = new TestCycleCaseDefectRelVO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO);
//        return testCycleCaseDTO;
//    }
//
//    @Override
//    public TestCycleCaseDefectRelE doToEntity(TestCycleCaseDefectRelDTO dataObject) {
//        TestCycleCaseDefectRelE testCaseStepE = TestCycleCaseDefectRelEFactory.create();
//        BeanUtils.copyProperties(dataObject, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseDefectRelDTO entityToDo(TestCycleCaseDefectRelE entity) {
//        TestCycleCaseDefectRelDTO testCycleCaseDTO = new TestCycleCaseDefectRelDTO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO);
//        return testCycleCaseDTO;
//    }
//}
