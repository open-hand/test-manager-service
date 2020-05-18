package io.choerodon.test.manager.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;

import io.choerodon.test.manager.api.vo.agile.SearchDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.vo.DefectReporterFormVO;
import io.choerodon.test.manager.api.vo.ReporterFormVO;

/**
 * Created by 842767365@qq.com on 7/13/18.
 */
public interface ReporterFormService {

    Page<ReporterFormVO> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    List<DefectReporterFormVO> createFormDefectFromIssue(Long projectId, Long[] issueIds, Long organizationId);

    Page createFormDefectFromIssue(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);
}
