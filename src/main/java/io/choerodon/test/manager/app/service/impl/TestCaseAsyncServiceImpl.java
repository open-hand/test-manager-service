package io.choerodon.test.manager.app.service.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.WebSocketMeaasgeVO;
import io.choerodon.test.manager.app.service.TestCaseAsyncService;
import io.choerodon.test.manager.app.service.TestCaseService;

import org.hzero.websocket.helper.SocketSendHelper;

/**
 * @author huaxin.deng@hand-china.com 2021-08-16 13:40:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TestCaseAsyncServiceImpl implements TestCaseAsyncService {


    private static final String WEBSOCKET_BATCH_DELETE_CASE = "test-batch-delete-case";

    @Autowired
    private SocketSendHelper socketSendHelper;
    @Autowired
    private TestCaseService testCaseService;

    @Async
    @Override
    public void batchDeleteAsync(Long projectId, List<Long> caseIds) {
        if (CollectionUtils.isEmpty(caseIds)) {
            return;
        }
        Long userId = DetailsHelper.getUserDetails().getUserId();
        WebSocketMeaasgeVO messageVO = new WebSocketMeaasgeVO(userId, "deleting", 0.0);
        String messageCode = WEBSOCKET_BATCH_DELETE_CASE + "-" + projectId;
        socketSendHelper.sendByUserId(userId, messageCode, JSON.toJSONString(messageVO));
        double incremental = Math.ceil(caseIds.size() <= 10 ? 1 : (caseIds.size() * 1.0) / 10);
        try {
            for (int i = 1; i <= caseIds.size(); i++) {
                testCaseService.deleteCase(projectId, caseIds.get(i - 1));
                if (i % incremental == 0) {
                    messageVO.setRate((i * 1.0) / caseIds.size());
                    socketSendHelper.sendByUserId(userId, messageCode, JSON.toJSONString(messageVO));
                }
            }
            messageVO.setStatus("success");
            messageVO.setRate(1.0);
        } catch (Exception e) {
            messageVO.setStatus("failed");
            messageVO.setError(e.getMessage());
            throw new CommonException("batch delete case failed, exception: {}", e);
        } finally {
            socketSendHelper.sendByUserId(userId, messageCode, JSON.toJSONString(messageVO));
        }
    }
}
