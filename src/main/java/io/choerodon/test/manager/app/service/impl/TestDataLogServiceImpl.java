package io.choerodon.test.manager.app.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import io.choerodon.agile.api.vo.StatusVO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.test.manager.api.vo.DataLogVO;
import io.choerodon.test.manager.app.service.TestDataLogService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.dto.TestDataLogDTO;
import io.choerodon.test.manager.infra.dto.UserMessageDTO;
import io.choerodon.test.manager.infra.mapper.TestDataLogMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhaotianxin
 * @since 2019/11/18
 */
@Component
public class TestDataLogServiceImpl implements TestDataLogService {
    @Autowired
    private TestDataLogMapper testDataLogMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void create(TestDataLogDTO testDataLogDTO) {
        if (testDataLogMapper.insertSelective(testDataLogDTO) != 1) {
            throw new CommonException("error.insert.data.log");
        }
    }

    @Override
    public void delete(TestDataLogDTO dataLogDTO) {
        testDataLogMapper.delete(dataLogDTO);
    }

    @Override
    public List<DataLogVO> queryByCaseId(Long projectId, Long caseId) {
        TestDataLogDTO testDataLogDTO = new TestDataLogDTO();
        testDataLogDTO.setCaseId(caseId);
        testDataLogDTO.setProjectId(projectId);
        List<DataLogVO> dataLogVOS = modelMapper.map(testDataLogMapper.select(testDataLogDTO), new TypeToken<List<DataLogVO>>() {
        }.getType());
        fillUserAndStatus(projectId, dataLogVOS);
        return dataLogVOS.stream().sorted(Comparator.comparing(DataLogVO::getCreationDate).reversed()).collect(Collectors.toList());
    }

    private void fillUserAndStatus(Long projectId, List<DataLogVO> dataLogVOS) {
        List<Long> createByIds = dataLogVOS.stream().filter(dataLogDTO -> dataLogDTO.getCreatedBy() != null && !Objects.equals(dataLogDTO.getCreatedBy(), 0L)).map(DataLogVO::getCreatedBy).distinct().collect(Collectors.toList());
        Map<Long, UserMessageDTO> usersMap = userService.queryUsersMap(createByIds, true);
        for (DataLogVO dto : dataLogVOS) {
            UserMessageDTO userMessageDTO = usersMap.get(dto.getCreatedBy());
            String name = userMessageDTO != null ? userMessageDTO.getName() : null;
            String loginName = userMessageDTO != null ? userMessageDTO.getLoginName() : null;
            String realName = userMessageDTO != null ? userMessageDTO.getRealName() : null;
            String imageUrl = userMessageDTO != null ? userMessageDTO.getImageUrl() : null;
            String email = userMessageDTO != null ? userMessageDTO.getEmail() : null;
            dto.setName(name);
            dto.setLoginName(loginName);
            dto.setRealName(realName);
            dto.setImageUrl(imageUrl);
            dto.setEmail(email);
        }
    }
}
