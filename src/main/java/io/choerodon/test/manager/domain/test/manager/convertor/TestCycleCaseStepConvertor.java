//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.test.manager.api.vo.TestCycleCaseStepVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
//import io.choerodon.test.manager.infra.vo.TestCycleCaseStepDTO;
//import io.choerodon.core.convertor.ConvertorI;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/12/18.
// */
//@Component
//public class TestCycleCaseStepConvertor implements ConvertorI<TestCycleCaseStepE, TestCycleCaseStepDTO, TestCycleCaseStepVO> {
//    static final String[] excludeParam = new String[]{"stepAttachment", "defects"};
//
//    @Override
//    public TestCycleCaseStepE dtoToEntity(TestCycleCaseStepVO vo) {
//        TestCycleCaseStepE testCaseStepE = TestCycleCaseStepEFactory.create();
//        BeanUtils.copyProperties(vo, testCaseStepE, excludeParam);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseStepVO entityToDto(TestCycleCaseStepE entity) {
//        TestCycleCaseStepVO testCycleCaseDTO = new TestCycleCaseStepVO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO, excludeParam);
//        testCycleCaseDTO.setStepAttachment(entity.getStepAttachment());
//        testCycleCaseDTO.setDefects(entity.getDefects());
//        return testCycleCaseDTO;
//    }
//
//    @Override
//    public TestCycleCaseStepE doToEntity(TestCycleCaseStepDTO dataObject) {
//        TestCycleCaseStepE testCaseStepE = TestCycleCaseStepEFactory.create();
//        BeanUtils.copyProperties(dataObject, testCaseStepE, excludeParam);
//        testCaseStepE.setStepAttachment(dataObject.getStepAttachment());
//        testCaseStepE.setDefects(dataObject.getDefects());
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseStepDTO entityToDo(TestCycleCaseStepE entity) {
//        TestCycleCaseStepDTO testCycleCaseDTO = new TestCycleCaseStepDTO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO, excludeParam);
//        return testCycleCaseDTO;
//    }
//}
