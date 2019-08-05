//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseEFactory;
//import io.choerodon.test.manager.infra.vo.TestCycleCaseDTO;
//import io.choerodon.core.convertor.ConvertorI;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/12/18.
// */
//@Component
//public class TestCycleCaseConvertor implements ConvertorI<TestCycleCaseE, TestCycleCaseDTO, TestCycleCaseVO> {
//
//    private static final String[] exclude = new String[]{"caseAttachment", "caseDefect", "subStepDefects", "cycleCaseStep"};
//
//    @Override
//    public TestCycleCaseE dtoToEntity(TestCycleCaseVO vo) {
//        TestCycleCaseE testCaseStepE = TestCycleCaseEFactory.create();
//        BeanUtils.copyProperties(vo, testCaseStepE, exclude);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseVO entityToDto(TestCycleCaseE entity) {
//        TestCycleCaseVO testCycleCaseVO = new TestCycleCaseVO();
//        BeanUtils.copyProperties(entity, testCycleCaseVO, exclude);
//        testCycleCaseVO.setCaseAttachment(entity.getCaseAttachment());
//        testCycleCaseVO.setDefects(entity.getDefects());
//        testCycleCaseVO.setSubStepDefects(entity.getSubStepDefects());
//        testCycleCaseVO.setCycleCaseStep(entity.getCycleCaseStep());
//        return testCycleCaseVO;
//    }
//
//    @Override
//    public TestCycleCaseE doToEntity(TestCycleCaseDTO dataObject) {
//        TestCycleCaseE testCaseStepE = TestCycleCaseEFactory.create();
//        BeanUtils.copyProperties(dataObject, testCaseStepE, exclude);
//        testCaseStepE.setCaseAttachment(dataObject.getCaseAttachment());
//        testCaseStepE.setDefects(dataObject.getCaseDefect());
//        testCaseStepE.setSubStepDefects(dataObject.getSubStepDefects());
//        testCaseStepE.setCycleCaseStep(dataObject.getCycleCaseStep());
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseDTO entityToDo(TestCycleCaseE entity) {
//        TestCycleCaseDTO testCycleCaseDTO = new TestCycleCaseDTO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO, exclude);
//        return testCycleCaseDTO;
//    }
//}
