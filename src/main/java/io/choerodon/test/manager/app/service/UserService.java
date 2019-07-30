package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import com.github.pagehelper.PageInfo;

import io.choerodon.agile.api.vo.UserDO;
import io.choerodon.agile.api.vo.UserDTO;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;

/**
 * Created by 842767365@qq.com on 7/2/18.
 */
public interface UserService {
    Map<Long, UserDO> query(Long[] ids);

    ResponseEntity<PageInfo<UserDTO>> list(PageRequest pageRequest, Long projectId, String param, Long userId);

    void populateUsersInHistory(List<TestCycleCaseHistoryVO> dto);

    void populateTestCycleCaseDTO(TestCycleCaseVO dto);

    void populateTestAutomationHistory(PageInfo<TestAutomationHistoryVO> dto);

}
