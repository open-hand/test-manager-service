package io.choerodon.test.manager.domain.repository;

import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface TestCaseStepRepository {

    TestCaseStepE insert(TestCaseStepE testCaseStepE);

    void delete(TestCaseStepE testCaseStepE);

    TestCaseStepE update(TestCaseStepE testCaseStepE);

    List<TestCaseStepE> query(TestCaseStepE testCaseStepE);

    String getLastedRank(@Param("issueId") Long issueId);

    List<TestCaseStepE> queryByParameter(TestCaseStepE testStepCaseE);

}
