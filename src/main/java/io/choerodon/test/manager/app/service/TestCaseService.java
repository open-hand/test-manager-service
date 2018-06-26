package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.*;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.ResponseEntity;

/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */
public interface TestCaseService {
    IssueDTO insert(Long projectId, IssueCreateDTO issueCreateDTO);

    void delete(Long projectId, Long issueId);

    ResponseEntity<IssueDTO> update(Long projectId, JSONObject issueUpdate);

    ResponseEntity<IssueDTO> query(Long projectId, Long issueId);

    ResponseEntity<Page<IssueCommonDTO>> listIssueWithoutSub(Long projectId, String typeCode, PageRequest pageRequest);
}
