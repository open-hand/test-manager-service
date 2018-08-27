package io.choerodon.test.manager.domain.service.impl;

import com.google.common.collect.Maps;
import io.choerodon.agile.api.dto.IssueCommonDTO;
import io.choerodon.agile.api.dto.IssueInfoDTO;
import io.choerodon.agile.api.dto.IssueListDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.service.ITestCycleCaseDefectRelService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.infra.feign.TestCaseFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class ITestCycleCaseDefectRelServiceImpl implements ITestCycleCaseDefectRelService {

    @Autowired
    TestCaseFeignClient testCaseFeignClient;


    @Override
    public TestCycleCaseDefectRelE insert(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        return testCycleCaseDefectRelE.addSelf();
    }

    @Override
    public void delete(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        testCycleCaseDefectRelE.deleteSelf();
    }

    @Override
    public Boolean updateProjectIdByIssueId(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        Boolean flag = testCycleCaseDefectRelE.updateProjectIdByIssueId();
        return flag;
    }


    @Override
    public List<TestCycleCaseDefectRelE> query(TestCycleCaseDefectRelE testCycleCaseDefectRelE) {
        return testCycleCaseDefectRelE.querySelf();
    }
}
