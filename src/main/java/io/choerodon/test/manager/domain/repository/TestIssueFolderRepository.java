//package io.choerodon.test.manager.domain.repository;
//
//import java.util.List;
//
//import org.springframework.data.domain.Pageable;
//import io.choerodon.test.manager.domain.test.manager.entity.TestCycleE;
//import io.choerodon.test.manager.domain.test.manager.entity.TestIssueFolderE;
//
///**
// * Created by zongw.lee@gmail.com on 08/30/2018
// */
//public interface TestIssueFolderRepository {
//	TestIssueFolderE insert(TestIssueFolderE testIssueFolderE);
//
//    void delete(TestIssueFolderE testIssueFolderE);
//
//	TestIssueFolderE update(TestIssueFolderE testIssueFolderE);
//
//	List<TestIssueFolderE> queryAllUnderProject(TestIssueFolderE testIssueFolderE);
//
//	TestIssueFolderE queryOne(TestIssueFolderE testIssueFolderE);
//
//	TestIssueFolderE queryByPrimaryKey(Long folderId);
//}
