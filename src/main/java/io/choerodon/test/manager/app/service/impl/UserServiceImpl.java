package io.choerodon.test.manager.app.service.impl;

import io.choerodon.agile.api.dto.UserDO;
import io.choerodon.agile.api.dto.UserDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.TestAutomationHistoryDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.common.utils.LongUtils;
import io.choerodon.test.manager.infra.feign.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by 842767365@qq.com on 7/2/18.
 */
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private UserFeignClient userFeignClient;

    public Map<Long, UserDO> query(Long[] ids) {
        if (ObjectUtils.isEmpty(ids)) {
            return new HashMap<>();
        }
        return userFeignClient.listUsersByIds(ids, false).getBody().stream().collect(Collectors.toMap(UserDO::getId, Function.identity()));
    }

    @Override
    public ResponseEntity<Page<UserDTO>> list(PageRequest pageRequest, Long projectId, String param, Long userId) {
        return userFeignClient.list(projectId, userId, pageRequest.getPage(), pageRequest.getSize(), param);
    }

    public void populateUsersInHistory(List<TestCycleCaseHistoryDTO> dto) {
        Long[] users = dto.stream().map(TestCycleCaseHistoryDTO::getLastUpdatedBy).filter(LongUtils::isUserId).distinct().toArray(Long[]::new);
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

    public void populateTestCycleCaseDTO(TestCycleCaseDTO dto) {
        Long[] users = Stream.of(dto.getAssignedTo(), dto.getLastUpdatedBy()).filter(LongUtils::isUserId).distinct().toArray(Long[]::new);
        if (ObjectUtils.isEmpty(users)) {
            return;
        }
        Map<Long, UserDO> user = query(users);
        Optional.ofNullable(dto.getAssignedTo()).ifPresent(v -> dto.setAssigneeUser(user.get(v)));
        Optional.ofNullable(dto.getLastUpdatedBy()).ifPresent(v -> dto.setLastUpdateUser(user.get(v)));
    }

    public void populateTestAutomationHistory(Page<TestAutomationHistoryDTO> dto) {
        Long[] users = dto.stream().map(TestAutomationHistoryDTO::getCreatedBy).filter(LongUtils::isUserId).distinct().toArray(Long[]::new);
        if (ObjectUtils.isEmpty(users)) {
            return;
        }
        Map<Long, UserDO> user = query(users);
        dto.forEach(v -> {
            if (LongUtils.isUserId(v.getCreatedBy())) {
                v.setCreateUser(user.get(v.getCreatedBy()));
            }
        });
    }


}
