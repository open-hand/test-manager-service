//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseHistoryE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseHistoryEFactory;
//import io.choerodon.test.manager.infra.vo.TestCycleCaseHistoryDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/12/18.
// */
//@Component
//public class TestCycleCaseHistoryConvertor implements ConvertorI<TestCycleCaseHistoryE, TestCycleCaseHistoryDTO, TestCycleCaseHistoryVO> {
//
//    @Override
//    public TestCycleCaseHistoryE dtoToEntity(TestCycleCaseHistoryVO vo) {
//        TestCycleCaseHistoryE testCaseStepE = TestCycleCaseHistoryEFactory.create();
//        BeanUtils.copyProperties(vo, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseHistoryVO entityToDto(TestCycleCaseHistoryE entity) {
//        TestCycleCaseHistoryVO testCycleCaseDTO = new TestCycleCaseHistoryVO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO);
//        return testCycleCaseDTO;
//    }
//
//    @Override
//    public TestCycleCaseHistoryE doToEntity(TestCycleCaseHistoryDTO dataObject) {
//        TestCycleCaseHistoryE testCaseStepE = TestCycleCaseHistoryEFactory.create();
//        BeanUtils.copyProperties(dataObject, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseHistoryDTO entityToDo(TestCycleCaseHistoryE entity) {
//        TestCycleCaseHistoryDTO testCycleCaseDTO = new TestCycleCaseHistoryDTO();
//        BeanUtils.copyProperties(entity, testCycleCaseDTO);
//        return testCycleCaseDTO;
//    }
//}
