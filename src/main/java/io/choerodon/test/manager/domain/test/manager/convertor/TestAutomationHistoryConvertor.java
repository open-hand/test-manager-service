//package io.choerodon.test.manager.domain.test.manager.convertor;
//
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.core.convertor.ConvertorI;
//import io.choerodon.test.manager.api.vo.TestAppInstanceVO;
//import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
//import io.choerodon.test.manager.infra.vo.TestAutomationHistoryDTO;
//
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TestAutomationHistoryConvertor  implements ConvertorI<TestAutomationHistoryDTO, TestAutomationHistoryDTO, TestAutomationHistoryVO> {
//    private static final String[] exclude=new String[]{"testAppInstanceDTO", "testAppInstanceDTO"};
//
//    @Override
//    public TestAutomationHistoryVO entityToDto(TestAutomationHistoryDTO entity) {
//        TestAutomationHistoryVO historyDTO=new TestAutomationHistoryVO();
//        BeanUtils.copyProperties(entity, historyDTO,exclude);
//        historyDTO.setTestAppInstanceVO(modeMapper.map(entity.getTestAppInstanceDTO(), TestAppInstanceVO.class));
//        return historyDTO;
//    }
//}
