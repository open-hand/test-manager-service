//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.test.manager.api.vo.TestFileLoadHistoryVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestFileLoadHistoryEFactory;
//import io.choerodon.test.manager.infra.vo.TestFileLoadHistoryDTO;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestFileLoadHistoryConvertor  implements ConvertorI<TestFileLoadHistoryE, TestFileLoadHistoryDTO, TestFileLoadHistoryVO> {
//
//    @Override
//    public TestFileLoadHistoryE dtoToEntity(TestFileLoadHistoryVO vo) {
//        TestFileLoadHistoryE testCycleE = TestFileLoadHistoryEFactory.create();
//        BeanUtils.copyProperties(vo, testCycleE);
//        return testCycleE;
//    }
//
//    @Override
//    public TestFileLoadHistoryVO entityToDto(TestFileLoadHistoryE entity) {
//        TestFileLoadHistoryVO testCycleDTO = new TestFileLoadHistoryVO();
//        BeanUtils.copyProperties(entity, testCycleDTO);
//        return testCycleDTO;
//    }
//
//    @Override
//    public TestFileLoadHistoryE doToEntity(TestFileLoadHistoryDTO vo) {
//        TestFileLoadHistoryE testCycleE = TestFileLoadHistoryEFactory.create();
//        BeanUtils.copyProperties(vo, testCycleE);
//        return testCycleE;
//    }
//
//    @Override
//    public TestFileLoadHistoryDTO entityToDo(TestFileLoadHistoryE entity) {
//        TestFileLoadHistoryDTO stepDO = new TestFileLoadHistoryDTO();
//        BeanUtils.copyProperties(entity, stepDO);
//        return stepDO;
//    }
//
//}
