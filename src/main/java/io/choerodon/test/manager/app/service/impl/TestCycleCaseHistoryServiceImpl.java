package io.choerodon.test.manager.app.service.impl;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.github.pagehelper.PageInfo;

import io.choerodon.agile.api.vo.UserDO;
import org.springframework.data.domain.Pageable;
import io.choerodon.test.manager.api.vo.TestCycleCaseVO;
import io.choerodon.test.manager.api.vo.TestCycleCaseHistoryVO;
import io.choerodon.test.manager.app.service.TestCycleCaseHistoryService;
import io.choerodon.test.manager.app.service.UserService;
import io.choerodon.test.manager.infra.dto.TestCycleCaseHistoryDTO;
import io.choerodon.test.manager.infra.enums.TestCycleCaseHistoryType;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseHistoryMapper;
import io.choerodon.test.manager.infra.util.DBValidateUtil;
import io.choerodon.test.manager.infra.util.LongUtils;
import io.choerodon.test.manager.infra.util.PageUtil;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
public class TestCycleCaseHistoryServiceImpl implements TestCycleCaseHistoryService {

    @Autowired
    private TestCycleCaseHistoryMapper testCycleCaseHistoryMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCycleCaseHistoryVO insert(TestCycleCaseHistoryVO testCycleCaseHistoryVO) {
        TestCycleCaseHistoryDTO testCycleCaseHistoryDTO = modelMapper.map(testCycleCaseHistoryVO, TestCycleCaseHistoryDTO.class);
        DBValidateUtil.executeAndvalidateUpdateNum(testCycleCaseHistoryMapper::insert, testCycleCaseHistoryDTO, 1, "error.history.insert");
        return modelMapper.map(testCycleCaseHistoryDTO, TestCycleCaseHistoryVO.class);
    }

    @Override
    public PageInfo<TestCycleCaseHistoryVO> query(Long cycleCaseId, Pageable pageable) {
        TestCycleCaseHistoryVO historyVO = new TestCycleCaseHistoryVO();
        historyVO.setExecuteId(cycleCaseId);
        PageInfo<TestCycleCaseHistoryDTO> testCycleCaseHistoryDTOPageInfo = PageHelper.startPage(pageable.getPageNumber(),
                pageable.getPageSize(), PageUtil.sortToSql(pageable.getSort())).doSelectPageInfo(()
                -> testCycleCaseHistoryMapper.query(modelMapper.map(historyVO, TestCycleCaseHistoryDTO.class)));
        List<TestCycleCaseHistoryVO> testCycleCaseHistoryVOS = modelMapper.map(testCycleCaseHistoryDTOPageInfo.getList(),
                new TypeToken<List<TestCycleCaseHistoryVO>>() {
                }.getType());
        userService.populateUsersInHistory(testCycleCaseHistoryVOS);
        return PageUtil.buildPageInfoWithPageInfoList(testCycleCaseHistoryDTOPageInfo, testCycleCaseHistoryVOS);
    }


    @Override
    @Transactional
    public void createAssignedHistory(TestCycleCaseVO afterCycleCase, TestCycleCaseVO beforeCycleCase) {
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setExecuteId(beforeCycleCase.getExecuteId());
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_ASSIGNED);
        Long after = afterCycleCase.getAssignedTo();
        Long before = beforeCycleCase.getAssignedTo();
        Long[] para = new Long[]{before, after};
        Map<Long, UserDO> users = userService.query(para);

        if (LongUtils.isUserId(before)) {
            UserDO u = users.get(before);
            historyDTO.setOldValue(u.getLoginName() + u.getRealName());
        } else {
            historyDTO.setOldValue(TestCycleCaseHistoryType.FIELD_NULL);
        }
        if (LongUtils.isUserId(after)) {
            UserDO u = users.get(after);
            historyDTO.setNewValue(u.getLoginName() + u.getRealName());
        } else {
            historyDTO.setNewValue(TestCycleCaseHistoryType.FIELD_NULL);
        }
        insert(historyDTO);
    }

    @Override
    @Transactional
    public void createStatusHistory(TestCycleCaseVO afterCycleCase, TestCycleCaseVO beforeCycleCase) {
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setExecuteId(beforeCycleCase.getExecuteId());
        String newColor = afterCycleCase.getExecutionStatusName();
        String oldColor = beforeCycleCase.getExecutionStatusName();
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_STATUS);
        historyDTO.setNewValue(newColor);
        historyDTO.setOldValue(oldColor);
        insert(historyDTO);
    }

    @Override
    @Transactional
    public void createCommentHistory(TestCycleCaseVO afterCycleCase, TestCycleCaseVO beforeCycleCase) {
        TestCycleCaseHistoryVO historyDTO = new TestCycleCaseHistoryVO();
        historyDTO.setExecuteId(beforeCycleCase.getExecuteId());
        historyDTO.setField(TestCycleCaseHistoryType.FIELD_COMMENT);
        if (StringUtils.isEmpty(afterCycleCase.getDescription())) {
            historyDTO.setNewValue(TestCycleCaseHistoryType.FIELD_NULL);
        } else {
            historyDTO.setNewValue(afterCycleCase.getDescription());
        }
        if (StringUtils.isEmpty(beforeCycleCase.getDescription())) {
            historyDTO.setOldValue(TestCycleCaseHistoryType.FIELD_NULL);
        } else {
            historyDTO.setOldValue(beforeCycleCase.getDescription());
        }
        insert(historyDTO);
    }

}
