//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.test.manager.api.vo.TestCycleCaseAttachmentRelVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseAttachmentRelEFactory;
//import io.choerodon.test.manager.infra.vo.TestCycleCaseAttachmentRelDTO;
//import io.choerodon.core.convertor.ConvertorI;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
///**
// * Created by 842767365@qq.com on 6/12/18.
// */
//@Component
//public class TestCycleCaseAttachmentRelConvertor implements ConvertorI<TestCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDTO, TestCycleCaseAttachmentRelVO> {
//
//    @Override
//    public TestCycleCaseAttachmentRelE dtoToEntity(TestCycleCaseAttachmentRelVO vo) {
//        TestCycleCaseAttachmentRelE testCaseStepE = TestCycleCaseAttachmentRelEFactory.create();
//        BeanUtils.copyProperties(vo, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseAttachmentRelVO entityToDto(TestCycleCaseAttachmentRelE entity) {
//        TestCycleCaseAttachmentRelVO stepDO = new TestCycleCaseAttachmentRelVO();
//        BeanUtils.copyProperties(entity, stepDO);
//        return stepDO;
//    }
//
//    @Override
//    public TestCycleCaseAttachmentRelE doToEntity(TestCycleCaseAttachmentRelDTO dataObject) {
//        TestCycleCaseAttachmentRelE testCaseStepE = TestCycleCaseAttachmentRelEFactory.create();
//        BeanUtils.copyProperties(dataObject, testCaseStepE);
//        return testCaseStepE;
//    }
//
//    @Override
//    public TestCycleCaseAttachmentRelDTO entityToDo(TestCycleCaseAttachmentRelE entity) {
//        TestCycleCaseAttachmentRelDTO stepDO = new TestCycleCaseAttachmentRelDTO();
//        BeanUtils.copyProperties(entity, stepDO);
//        return stepDO;
//    }
//}
