package io.choerodon.test.manager.app.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import io.choerodon.test.manager.api.dto.TestIssueFolderDTO;
import io.choerodon.test.manager.api.dto.TestIssueFolderRelDTO;

/**
 * Created by zongw.lee@gmail.com on 08/31/2018
 */
public interface TestIssueFolderRelService {
    List<TestIssueFolderRelDTO> query(TestIssueFolderRelDTO testIssueFolderRelDTO);

    TestIssueFolderRelDTO insert(TestIssueFolderRelDTO testIssueFolderRelDTO);

    void delete(TestIssueFolderRelDTO testIssueFolderRelDTO);

    TestIssueFolderRelDTO update(TestIssueFolderRelDTO testIssueFolderRelDTO);
}
