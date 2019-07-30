package io.choerodon.test.manager.app.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

import io.choerodon.agile.api.vo.SearchDTO;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.test.manager.api.vo.DefectReporterFormVO;
import io.choerodon.test.manager.api.vo.ReporterFormVO;

/**
 * Created by 842767365@qq.com on 7/13/18.
 */
public interface ReporterFormService {

    PageInfo<ReporterFormVO> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);

    List<ReporterFormVO> createFromIssueToDefect(Long projectId, Long[] issueIds, Long organizationId);

    List<DefectReporterFormVO> createFormDefectFromIssue(Long projectId, Long[] issueIds, Long organizationId);

    PageInfo createFormDefectFromIssue(Long projectId, SearchDTO searchDTO, PageRequest pageRequest, Long organizationId);
}
