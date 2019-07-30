//package io.choerodon.test.manager.infra.repository.impl;
//
//import io.choerodon.core.convertor.ConvertHelper;
//import io.choerodon.test.manager.domain.repository.TestCycleCaseAttachmentRelRepository;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
//import io.choerodon.test.manager.infra.util.DBValidateUtil;
//import io.choerodon.test.manager.infra.vo.TestCycleCaseAttachmentRelDTO;
//import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * Created by 842767365@qq.com on 6/11/18.
// */
//@Component
//public class TestCycleCaseAttachmentRelRepositoryImpl implements TestCycleCaseAttachmentRelRepository {
//
//    @Autowired
//    TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;
//
//    @Override
//    public TestCycleCaseAttachmentRelE insert(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
//        TestCycleCaseAttachmentRelDTO convert = modeMapper.map(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDTO.class);
//        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseAttachmentRelMapper::insert,convert,1,"error.attachment.insert");
//        return modeMapper.map(convert, TestCycleCaseAttachmentRelE.class);
//    }
//
//    @Override
//    public void delete(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
//        TestCycleCaseAttachmentRelDTO convert = modeMapper.map(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDTO.class);
//        testCycleCaseAttachmentRelMapper.delete(convert);
//    }
//
//    @Override
//    public List<TestCycleCaseAttachmentRelE> query(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
//        TestCycleCaseAttachmentRelDTO convert = modeMapper.map(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDTO.class);
//
//        return ConvertHelper.convertList(testCycleCaseAttachmentRelMapper.select(convert), TestCycleCaseAttachmentRelE.class);
//    }
//}
