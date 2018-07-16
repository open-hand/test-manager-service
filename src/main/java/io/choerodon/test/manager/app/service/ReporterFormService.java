package io.choerodon.test.manager.app.service;

import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.domain.test.manager.entity.DefectReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.ReporterFormE;

import java.util.List;

/**
 * Created by jialongZuo@hand-china.com on 7/13/18.
 */
public interface ReporterFormService {

	List<ReporterFormE> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, PageRequest pageRequest);

	List<ReporterFormE> createFromIssueToDefect(Long projectId, Long[] issueIds);

	List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, PageRequest pageRequest);

	List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, Long[] issueIds);

//	List<ReporterFormE> createFromIssueToDefect(Long[] issueIds);
}
