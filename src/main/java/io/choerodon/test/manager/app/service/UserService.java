package io.choerodon.test.manager.app.service;

import java.util.List;
import java.util.Map;

import io.choerodon.test.manager.infra.dto.UserMessageDTO;
import org.springframework.http.ResponseEntity;
import com.github.pagehelper.PageInfo;

import io.choerodon.test.manager.api.vo.agile.UserDO;
import io.choerodon.test.manager.api.vo.agile.UserDTO;
import org.springframework.data.domain.Pageable;
import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;

/**
 * Created by 842767365@qq.com on 7/2/18.
 */
public interface UserService {
    Map<Long, UserDO> query(Long[] ids);

    ResponseEntity<PageInfo<UserDTO>> list(Pageable pageable, Long projectId, String param, Long userId);

    void populateUsersInHistory(List<TestCycleCaseHistoryVO> dto);

    void populateTestAutomationHistory(PageInfo<TestAutomationHistoryVO> dto);

    Map<Long, UserMessageDTO> queryUsersMap(List<Long> assigneeIdList);

}
