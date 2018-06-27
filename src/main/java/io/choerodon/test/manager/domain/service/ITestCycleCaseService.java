package io.choerodon.test.manager.domain.service;

import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public interface ITestCycleCaseService {

    /**
     * 创建一个测试例
     *
     * @param testCycleCaseE
     * @return
     */
	TestCycleCaseE runTestCycleCase(TestCycleCaseE testCycleCaseE);

    void delete(TestCycleCaseE testCycleCaseE);


    Page<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE, PageRequest pageRequest);


    List<TestCycleCaseE> query(TestCycleCaseE testCycleCaseE);

    TestCycleCaseE queryOne(TestCycleCaseE testCycleCaseE);

	TestCycleCaseE changeStep(TestCycleCaseE currentStepE);
}
