package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.choerodon.test.manager.infra.dto.UserMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import com.github.pagehelper.PageInfo;

import io.choerodon.agile.api.vo.UserDO;
import io.choerodon.agile.api.vo.UserDTO;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.vo.TestAutomationHistoryVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.util.LongUtils;
import io.choerodon.test.manager.infra.feign.BaseFeignClient;

/**
 * Created by 842767365@qq.com on 7/2/18.
 */
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private BaseFeignClient baseFeignClient;

    public Map<Long, UserDO> query(Long[] ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return new HashMap<>();
        }
        return baseFeignClient.listUsersByIds(ids, false).getBody().stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
    }

    @Override
    public ResponseEntity<PageInfo<UserDTO>> list(PageRequest pageRequest, Long projectId, String param, Long userId) {
        return baseFeignClient.list(projectId, pageRequest.getPage(), pageRequest.getSize());
    }

    public void populateUsersInHistory(List<TestCycleCaseHistoryVO> dto) {
        Long[] users = dto.stream().map(TestCycleCaseHistoryVO::getLastUpdatedBy).filter(LongUtils::isUserId).distinct().toArray(Long[]::new);
        if (ObjectUtils.isEmpty(users)) {
            return;
        }
        Map<Long, UserDO> user = query(users);
        dto.forEach(v -> {
            if (LongUtils.isUserId(v.getLastUpdatedBy())) {
                v.setUser(user.get(v.getLastUpdatedBy()));
            }
        });
    }

    public void populateTestCycleCaseDTO(TestCycleCaseVO dto) {
        Long[] users = Stream.of(dto.getAssignedTo(), dto.getLastUpdatedBy()).filter(LongUtils::isUserId).distinct().toArray(Long[]::new);
        if (ObjectUtils.isEmpty(users)) {
            return;
        }
        Map<Long, UserDO> user = query(users);
        Optional.ofNullable(dto.getAssignedTo()).ifPresent(v -> dto.setAssigneeUser(user.get(v)));
        Optional.ofNullable(dto.getLastUpdatedBy()).ifPresent(v -> dto.setLastUpdateUser(user.get(v)));
    }

    public void populateTestAutomationHistory(PageInfo<TestAutomationHistoryVO> dto) {
        Long[] users = dto.getList().stream().map(TestAutomationHistoryVO::getCreatedBy).filter(LongUtils::isUserId).distinct().toArray(Long[]::new);
        if (ObjectUtils.isEmpty(users)) {
            return;
        }
        Map<Long, UserDO> user = query(users);
        dto.getList().forEach(v -> {
            if (LongUtils.isUserId(v.getCreatedBy())) {
                v.setCreateUser(user.get(v.getCreatedBy()));
            }
        });
    }

    @Override
    public Map<Long, UserMessageDTO> queryUsersMap(List<Long> assigneeIdList, Boolean withLoginName) {
        if (assigneeIdList == null) {
            return new HashMap<>();
        }
        Map<Long, UserMessageDTO> userMessageMap = new HashMap<>(assigneeIdList.size());
        if (!assigneeIdList.isEmpty()) {
            Long[] assigneeIds = new Long[assigneeIdList.size()];
            assigneeIdList.toArray(assigneeIds);
            List<UserDO> userDTOS = baseFeignClient.listUsersByIds(assigneeIds, false).getBody();
            if (withLoginName) {
                userDTOS.forEach(userDO -> {
                    String ldapName = userDO.getRealName() + "（" + userDO.getLoginName() + "）";
                    String noLdapName = userDO.getRealName() + "（" + userDO.getEmail() + "）";
                    userMessageMap.put(userDO.getId(), new UserMessageDTO(userDO.getLdap() ? ldapName : noLdapName , userDO.getLoginName(), userDO.getRealName(), userDO.getImageUrl(), userDO.getEmail(), userDO.getLdap()));
                });
            } else {
                userDTOS.forEach(userDO -> userMessageMap.put(userDO.getId(), new UserMessageDTO(userDO.getRealName(), userDO.getLoginName(), userDO.getRealName(), userDO.getImageUrl(), userDO.getEmail(), userDO.getLdap())));
            }
        }
        return userMessageMap;
    }
}
