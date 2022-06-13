package io.choerodon.test.manager.app.service;


import io.choerodon.test.manager.api.vo.ListLayoutVO;

/**
 * @author zhaotianxin
 * @date 2021-05-07 14:11
 */
public interface ListLayoutService {
   ListLayoutVO save(Long organizationId, Long projectId, ListLayoutVO listLayoutVO);

   ListLayoutVO queryByApplyType(Long organizationId, Long projectId, String applyType);
}
