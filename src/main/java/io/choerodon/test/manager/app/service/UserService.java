package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.agile.api.dto.UserDTO;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;

import com.github.pagehelper.PageInfo;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by 842767365@qq.com on 7/2/18.
 */
public interface UserService {
    Map<Long, UserDO> query(Long[] ids);

    ResponseEntity<PageInfo<UserDTO>> list(PageRequest pageRequest, Long projectId, String param, Long userId);

    void populateUsersInHistory(List<TestCycleCaseHistoryDTO> dto);

    void populateTestCycleCaseDTO(TestCycleCaseDTO dto);

    void populateTestAutomationHistory(PageInfo<TestAutomationHistoryDTO> dto);

}
