package io.choerodon.test.manager.app.service.impl;

import com.google.common.collect.Lists;
import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.agile.api.dto.SearchDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.test.manager.api.dto.CustomPage;
import io.choerodon.test.manager.api.dto.IssueInfosDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseDTO;
import io.choerodon.test.manager.api.dto.TestCycleCaseStepDTO;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.domain.repository.TestCycleCaseDefectRelRepository;
import io.choerodon.test.manager.domain.repository.TestCycleCaseRepository;
import io.choerodon.test.manager.domain.repository.TestCycleCaseStepRepository;
import io.choerodon.test.manager.domain.test.manager.entity.DefectReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.ReporterFormE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseDefectRelEFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 7/13/18.
 */

@Component
public class ReporterFormServiceImpl implements ReporterFormService {


    @Autowired
    TestCaseService testCaseService;

    @Autowired
    TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCycleCaseDefectRelRepository testCycleCaseDefectRelRepository;
    @Autowired
    TestCycleCaseRepository testCycleCaseRepository;

    @Autowired
    TestCycleCaseStepRepository testCycleCaseStepRepository;

    public Page<ReporterFormE> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId) {
        Page page = new Page();
        Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMapAndPopulatePageInfo(projectId, searchDTO, pageRequest, page,organizationId);
        List<ReporterFormE> reporterFormES = doCreateFromIssueToDefect(issueResponse.values().stream().collect(Collectors.toList()), projectId,organizationId);

        page.setContent(reporterFormES);
        page.setNumberOfElements(reporterFormES.size());
        return page;
    }

    public List<ReporterFormE> createFromIssueToDefect(Long projectId, Long[] issueIds,Long organizationId) {
        Assert.notEmpty(issueIds, "error.query.form.issueId.not.empty");

        Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueIds, false,organizationId);
        return doCreateFromIssueToDefect(issueResponse.values().stream().collect(Collectors.toList()), projectId,organizationId);

    }

    private List<ReporterFormE> doCreateFromIssueToDefect(List<IssueInfosDTO> issueInfosDTO, Long projectId,Long organizationId) {
        if (ObjectUtils.isEmpty(issueInfosDTO)) {
            return new ArrayList<>();
        }
        List<Long> issues = issueInfosDTO.stream().map(IssueInfosDTO::getIssueId).collect(Collectors.toList());
        List<IssueLinkDTO> linkDTOS = testCaseService.getLinkIssueFromIssueToTest(projectId, issues);
        Long[] linkedIssues = linkDTOS.stream().map(IssueLinkDTO::getIssueId).toArray(Long[]::new);
        List<TestCycleCaseDTO> cycleCaseDTOS = testCycleCaseService.queryInIssues(linkedIssues, projectId,organizationId);

        return issueInfosDTO.stream().map(ReporterFormE::new).peek(v -> v.populateLinkedTest(linkDTOS).populateLinkedIssueCycle(cycleCaseDTOS).countDefect()).collect(Collectors.toList());

    }


    public List<DefectReporterFormE> createFormDefectFromIssue(Long projectId, Long[] issueIds,Long organizationId) {
        Assert.notEmpty(issueIds, "error.query.form.issueId.not.empty");

        Map<Long, IssueInfosDTO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueIds, false,organizationId);
        return doCreateFromDefectToIssue(issueResponse.values().stream().collect(Collectors.toList()), projectId,organizationId);
    }


    @Override
    public Page<ReporterFormE> createFormDefectFromIssue(Long projectId, SearchDTO searchDTO, PageRequest pageRequest,Long organizationId) {
        TestCycleCaseDefectRelE testCycleCaseDefectRelE = TestCycleCaseDefectRelEFactory.create();
        List<Long> issueIdsList = testCycleCaseDefectRelE.queryIssueIdAndDefectId(projectId);
        if (ObjectUtils.isEmpty(issueIdsList)) {
            return new Page();
        }
        Long[] issueIds = issueIdsList.stream().toArray(Long[]::new);
        Map args = Optional.ofNullable(searchDTO.getOtherArgs()).orElseGet(HashMap::new);

        args.put("issueIds", issueIds);
        if (searchDTO.getOtherArgs() == null) {
            searchDTO.setOtherArgs(args);
        }
        // 此处假设返回的是 long数组所有值
        Long[] allFilteredIssues = testCaseService.queryIssueIdsByOptions(searchDTO, projectId).stream().toArray(Long[]::new);
        if (ObjectUtils.isEmpty(allFilteredIssues)) {
            return new Page<>();
        }
        int pageNum = pageRequest.getPage();
        int pageSize = pageRequest.getSize();
        int highPage = (pageNum + 1) * pageSize - 1;
        int lowPage = pageNum * pageSize;
        //创建一个Long数组，将对应分页的issuesId传给它
        int size = highPage - allFilteredIssues.length > 0 ? allFilteredIssues.length - lowPage : pageSize;

        Long[] pagedIssues = new Long[size];
        System.arraycopy(allFilteredIssues, lowPage, pagedIssues, 0, size);
        // 得到包装好的报表List
        List<DefectReporterFormE> reporterFormES = createFormDefectFromIssue(projectId, pagedIssues,organizationId);

        return new CustomPage(reporterFormES, allFilteredIssues);
    }


    private List<DefectReporterFormE> doCreateFromDefectToIssue(List<IssueInfosDTO> issueInfosDTO, Long projectId,Long organizationId) {
        List<DefectReporterFormE> formES = Lists.newArrayList();
        if (ObjectUtils.isEmpty(issueInfosDTO)) {
            return formES;
        }
        List<Long> issues = new ArrayList<>();
        for (IssueInfosDTO infos : issueInfosDTO) {
            DefectReporterFormE form = new DefectReporterFormE(infos);
            formES.add(form);
            issues.add(infos.getIssueId());
        }
        Long[] issueIds = issues.toArray(new Long[issues.size()]);
        List<TestCycleCaseDefectRelE> defectLists = testCycleCaseDefectRelRepository.queryInIssues(issueIds, projectId);
        if (ObjectUtils.isEmpty(defectLists)) {
            return formES;
        }

        Map<Long, List<TestCycleCaseDefectRelE>> caseDefectLinkMap = defectLists.stream()
                .filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CYCLE_CASE)).collect(Collectors.groupingBy(TestCycleCaseDefectRelE::getDefectLinkId));
        Long[] caseIds = defectLists.stream()
                .filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CYCLE_CASE)).map(TestCycleCaseDefectRelE::getDefectLinkId).distinct().toArray(Long[]::new);

        Map<Long, List<TestCycleCaseDefectRelE>> stepDefectLinkMap = defectLists.stream()
                .filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CASE_STEP)).collect(Collectors.groupingBy(TestCycleCaseDefectRelE::getDefectLinkId));
        Long[] stepIds = defectLists.stream()
                .filter(u -> u.getDefectType().equals(TestCycleCaseDefectRelE.CASE_STEP)).map(TestCycleCaseDefectRelE::getDefectLinkId).toArray(Long[]::new);

        List<Long> issueIdLists = new ArrayList<>();
        List<TestCycleCaseDTO> cycleCases = null;
        List<TestCycleCaseStepDTO> cycleCaseSteps = null;
        if (caseIds.length > 0) {
            cycleCases = ConvertHelper.convertList(testCycleCaseRepository.queryCycleCaseForReporter(caseIds), TestCycleCaseDTO.class);
            issueIdLists.addAll(cycleCases.stream().map(TestCycleCaseDTO::getIssueId).collect(Collectors.toList()));

        }
        if (stepIds.length > 0) {
            cycleCaseSteps = ConvertHelper.convertList(testCycleCaseStepRepository.queryCycleCaseForReporter(stepIds), TestCycleCaseStepDTO.class);
            issueIdLists.addAll(cycleCaseSteps.stream().map(TestCycleCaseStepDTO::getIssueId).collect(Collectors.toList()));

        }

        List<IssueLinkDTO> linkDTOS = testCaseService.getLinkIssueFromTestToIssue(projectId, issueIdLists);

        if (cycleCases != null) {
            DefectReporterFormE.populateCaseIssueLink(linkDTOS, cycleCases);
            for (DefectReporterFormE form : formES) {
                form.populateCycleCase(cycleCases, caseDefectLinkMap);
            }
        }
        if (cycleCaseSteps != null) {
            DefectReporterFormE.populateStepIssueLink(linkDTOS, cycleCaseSteps);
            for (DefectReporterFormE form : formES) {
                form.populateCycleCaseStep(cycleCaseSteps, stepDefectLinkMap);
            }
        }

        if (!issueIdLists.isEmpty()) {
            Map<Long, IssueInfosDTO> map = testCaseService.getIssueInfoMap(projectId, issueIdLists.toArray(new Long[issueIdLists.size()]), false,organizationId);
            formES.forEach(v -> v.populateIssueInfo(map));
        }
        return formES;
    }

}
