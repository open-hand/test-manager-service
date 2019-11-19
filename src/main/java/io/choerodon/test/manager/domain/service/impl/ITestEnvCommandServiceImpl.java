//package io.choerodon.test.manager.domain.service.impl;
//
//import java.util.List;
//
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import org.springframework.data.domain.Sort;
//import io.choerodon.core.exception.CommonException;
//import org.springframework.data.domain.Pageable;
//import io.choerodon.test.manager.domain.service.ITestEnvCommandService;
//import io.choerodon.test.manager.infra.vo.TestEnvCommandDTO;
//import io.choerodon.test.manager.infra.mapper.TestEnvCommandMapper;
//
///**
// * Created by zongw.lee@gmail.com on 20/11/2018
// */
//@Component
//public class ITestEnvCommandServiceImpl implements ITestEnvCommandService {
//
//    @Autowired
//    TestEnvCommandMapper envCommandMapper;
//
//    @Override
//    public List<TestEnvCommandDTO> queryEnvCommand(TestEnvCommandDTO envCommand) {
//        Pageable pageable = PageRequest.of(1, 99999999, Sort.Direction.DESC, "creation_date");
//        PageInfo<TestEnvCommandDTO> pageInfo = PageHelper.startPage(pageable.getPageNumber(),
//                pageable.getPageSize()).doSelectPageInfo(() -> envCommandMapper.select(envCommand));
//
//        return pageInfo.getList();
//    }
//
//    @Override
//    public void updateByPrimaryKey(TestEnvCommandDTO envCommand) {
//        envCommandMapper.updateByPrimaryKey(envCommand);
//    }
//
//    @Override
//    public TestEnvCommandDTO insertOne(TestEnvCommandDTO envCommand) {
//        if (envCommandMapper.insert(envCommand) == 0) {
//            throw new CommonException("error.ITestEnvCommandValueServiceImpl.insert");
//        }
//        return envCommandMapper.selectByPrimaryKey(envCommand.getId());
//    }
//}
