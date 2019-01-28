package io.choerodon.test.manager.domain.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.choerodon.agile.api.dto.IssueDTO;
import io.choerodon.test.manager.api.dto.testng.TestNgTest;
import io.choerodon.test.manager.domain.service.IResultImportService;
import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestStatusE;
import io.choerodon.test.manager.infra.common.utils.SpringUtil;
import io.choerodon.test.manager.infra.exception.IssueCreateException;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shinan.chen
 * @since 2019/1/24
 */
public class IResultImportServiceImpl implements IResultImportService {
}
