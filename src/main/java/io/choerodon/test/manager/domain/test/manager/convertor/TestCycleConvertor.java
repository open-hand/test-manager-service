//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//import io.choerodon.test.manager.api.vo.TestCycleVO;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
//import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;
//import io.choerodon.test.manager.infra.vo.TestCycleDTO;
//import io.choerodon.core.convertor.ConvertorI;
//
///**
// * Created by 842767365@qq.com on 6/12/18.
// */
//@Component
//public class TestCycleConvertor implements ConvertorI<TestCycleE, TestCycleDTO, TestCycleVO> {
//
//    @Override
//    public TestCycleE dtoToEntity(TestCycleVO vo) {
//        TestCycleE testCycleE = TestCycleEFactory.create();
//        BeanUtils.copyProperties(vo, testCycleE);
//        return testCycleE;
//    }
//
//    @Override
//    public TestCycleVO entityToDto(TestCycleE entity) {
//        TestCycleVO testCycleVO = new TestCycleVO();
//        BeanUtils.copyProperties(entity, testCycleVO, "cycleCaseList");
//        List<Object> list = new ArrayList<>();
//        if (entity.getCycleCaseList() != null) {
//            entity.getCycleCaseList().forEach((k, v) -> list.add(v));
//            testCycleVO.setCycleCaseWithBarList(list);
//        }
//        return testCycleVO;
//    }
//
//    @Override
//    public TestCycleE doToEntity(TestCycleDTO vo) {
//        TestCycleE testCycleE = TestCycleEFactory.create();
//        BeanUtils.copyProperties(vo, testCycleE, "cycleCaseList");
//        Optional.ofNullable(vo.getCycleCaseList()).ifPresent(testCycleE::setCycleCaseList);
//
//        return testCycleE;
//    }
//
//    @Override
//    public TestCycleDTO entityToDo(TestCycleE entity) {
//        TestCycleDTO stepDO = new TestCycleDTO();
//        BeanUtils.copyProperties(entity, stepDO);
//        return stepDO;
//    }
//}
