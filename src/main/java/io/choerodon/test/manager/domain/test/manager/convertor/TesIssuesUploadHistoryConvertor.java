//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.test.manager.api.vo.TestIssuesUploadHistoryVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestFileLoadHistoryE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestFileLoadHistoryEFactory;
//import io.choerodon.test.manager.infra.vo.TestFileLoadHistoryDTO;
//
//@Component
//public class TesIssuesUploadHistoryConvertor implements ConvertorI<TestFileLoadHistoryE, TestFileLoadHistoryDTO, TestIssuesUploadHistoryVO> {
//
//    @Override
//    public TestIssuesUploadHistoryVO entityToDto(TestFileLoadHistoryE entity) {
//        TestIssuesUploadHistoryVO testIssuesUploadHistoryVO = new TestIssuesUploadHistoryVO();
//        BeanUtils.copyProperties(entity, testIssuesUploadHistoryVO);
//        return testIssuesUploadHistoryVO;
//    }
//
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
