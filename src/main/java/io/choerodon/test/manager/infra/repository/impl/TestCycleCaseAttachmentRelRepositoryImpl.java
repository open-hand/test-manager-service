package io.choerodon.test.manager.infra.repository.impl;

import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.repository.TestCycleCaseAttachmentRelRepository;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseAttachmentRelE;
import io.choerodon.test.manager.infra.common.utils.DBValidateUtil;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseAttachmentRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseAttachmentRelRepositoryImpl implements TestCycleCaseAttachmentRelRepository {

    @Autowired
    TestCycleCaseAttachmentRelMapper testCycleCaseAttachmentRelMapper;

    @Override
    public TestCycleCaseAttachmentRelE insert(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
        TestCycleCaseAttachmentRelDO convert = ConvertHelper.convert(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseAttachmentRelMapper::insert,convert,1,"error.attachment.insert");
        return ConvertHelper.convert(convert, TestCycleCaseAttachmentRelE.class);
    }

    @Override
    public void delete(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
        TestCycleCaseAttachmentRelDO convert = ConvertHelper.convert(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO.class);
        testCycleCaseAttachmentRelMapper.delete(convert);
    }

    @Override
    public List<TestCycleCaseAttachmentRelE> query(TestCycleCaseAttachmentRelE testCycleCaseAttachmentRelE) {
        TestCycleCaseAttachmentRelDO convert = ConvertHelper.convert(testCycleCaseAttachmentRelE, TestCycleCaseAttachmentRelDO.class);

        return ConvertHelper.convertList(testCycleCaseAttachmentRelMapper.select(convert), TestCycleCaseAttachmentRelE.class);
    }
}
