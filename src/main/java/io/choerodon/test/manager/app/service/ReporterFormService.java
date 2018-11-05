package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.DefectReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.ReporterFormE;

import java.util.List;

/**
 * Created by 842767365@qq.com on 7/13/18.
 */
public interface ReporterFormService {

	Page<ReporterFormE> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId);

	List<ReporterFormE> createFromIssueToDefect(Long projectId, Long[] issueIds,Long organizationId);

	Page<DefectReporterFormE> createFormDefectFromIssue(Long projectId, PageRequest pageRequest,Long organizationId);

	List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, Long[] issueIds,Long organizationId);


	Page createFormDefectFromIssue(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId);
}
