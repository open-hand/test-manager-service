package io.choerodon.test.manager.app.service.impl;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import io.choerodon.core.client.MessageClientC7n;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.test.manager.api.vo.WebSocketMeaasgeVO;
import io.choerodon.test.manager.app.service.TestCycleCaseAsyncService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;

/**
 * @author chihao.ran@hand-china.com
 * 2021/05/14 17:14
 */
@Service
public class TestCycleCaseAsyncServiceImpl implements TestCycleCaseAsyncService {

    private static final String WEBSOCKET_BATCH_DELETE_CYCLE_CASE = "test-batch-delete-cycle-case";

    @Autowired
    private MessageClientC7n messageClientC7n;
    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Async
    @Override
    public void asyncBatchDelete(List<Long> cycleCaseIds, Long projectId) {
        if (CollectionUtils.isEmpty(cycleCaseIds)) {
            return;
        }
        Long userId = DetailsHelper.getUserDetails().getUserId();
        WebSocketMeaasgeVO messageVO = new WebSocketMeaasgeVO(userId, "deleting", 0.0);
        String messageCode = WEBSOCKET_BATCH_DELETE_CYCLE_CASE + "-" + projectId;
        messageClientC7n.sendByUserId(userId, messageCode, JSON.toJSONString(messageVO));
        double incremental = Math.ceil(cycleCaseIds.size() <= 10 ? 1 : (cycleCaseIds.size() * 1.0) / 10);
        try {
            for (int i = 1; i <= cycleCaseIds.size(); i++) {
                testCycleCaseService.delete(cycleCaseIds.get(i - 1), projectId);
                if (i % incremental == 0) {
                    messageVO.setRate((i * 1.0) / cycleCaseIds.size());
                    messageClientC7n.sendByUserId(userId, messageCode, JSON.toJSONString(messageVO));
                }
            }
            messageVO.setStatus("success");
            messageVO.setRate(1.0);
        } catch (Exception e) {
            messageVO.setStatus("failed");
            messageVO.setError(e.getMessage());
            throw new CommonException("batch delete cycle case failed, exception: {}", e);
        } finally {
            messageClientC7n.sendByUserId(userId, messageCode, JSON.toJSONString(messageVO));
        }
    }
}
