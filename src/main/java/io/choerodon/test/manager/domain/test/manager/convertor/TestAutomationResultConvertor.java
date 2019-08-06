//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.test.manager.api.vo.TestAutomationResultVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestAutomationResultE;
//import io.choerodon.test.manager.infra.util.SpringUtil;
//import io.choerodon.test.manager.infra.vo.TestAutomationResultDTO;
//
//@Component
//public class TestAutomationResultConvertor implements ConvertorI<TestAutomationResultE, TestAutomationResultDTO, TestAutomationResultVO> {
//
//    @Override
//    public TestAutomationResultE dtoToEntity(TestAutomationResultVO vo) {
//        TestAutomationResultE testAutomationResultE = SpringUtil.getApplicationContext().getBean(TestAutomationResultE.class);
//        BeanUtils.copyProperties(vo, testAutomationResultE);
//        return testAutomationResultE;
//    }
//
//    @Override
//    public TestAutomationResultVO entityToDto(TestAutomationResultE entity) {
//        TestAutomationResultVO testAutomationResultVO = new TestAutomationResultVO();
//        BeanUtils.copyProperties(entity, testAutomationResultVO);
//        return testAutomationResultVO;
//    }
//
//    @Override
//    public TestAutomationResultE doToEntity(TestAutomationResultDTO dataObject) {
//        TestAutomationResultE testAutomationResultE = SpringUtil.getApplicationContext().getBean(TestAutomationResultE.class);
//        BeanUtils.copyProperties(dataObject, testAutomationResultE);
//        return testAutomationResultE;
//    }
//
//    @Override
//    public TestAutomationResultDTO entityToDo(TestAutomationResultE entity) {
//        TestAutomationResultDTO testAutomationResultDTO = new TestAutomationResultDTO();
//        BeanUtils.copyProperties(entity, testAutomationResultDTO);
//        return testAutomationResultDTO;
//    }
//
//}
