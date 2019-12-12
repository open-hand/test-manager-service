package io.choerodon.test.manager.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import com.google.common.collect.Lists;

import io.choerodon.agile.api.vo.IssueLinkDTO;
import io.choerodon.agile.api.vo.SearchDTO;
import org.springframework.data.domain.Pageable;
import io.choerodon.test.manager.api.vo.*;
import io.choerodon.test.manager.app.service.ReporterFormService;
import io.choerodon.test.manager.app.service.TestCaseService;
import io.choerodon.test.manager.app.service.TestCycleCaseService;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;
import io.choerodon.test.manager.infra.enums.TestCycleCaseDefectCode;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseDefectRelMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseMapper;
import io.choerodon.test.manager.infra.mapper.TestCycleCaseStepMapper;

/**
 * Created by 842767365@qq.com on 7/13/18.
 */

@Service
public class ReporterFormServiceImpl implements ReporterFormService {

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestCycleCaseService testCycleCaseService;

    @Autowired
    private TestCycleCaseDefectRelMapper testCycleCaseDefectRelMapper;

    @Autowired
    private TestCycleCaseMapper testCycleCaseMapper;

    @Autowired
    private TestCycleCaseStepMapper testCycleCaseStepMapper;

    @Autowired
    private ModelMapper modelMapper;

    public PageInfo<ReporterFormVO> createFromIssueToDefect(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId) {
        Page page = new Page();
        Map<Long, IssueInfosVO> issueResponse = testCaseService.getIssueInfoMapAndPopulatePageInfo(projectId, searchDTO, pageable, page, organizationId);
        List<ReporterFormVO> reporterFormES = doCreateFromIssueToDefect(issueResponse.values().stream().collect(Collectors.toList()), projectId, organizationId);
        page.addAll(reporterFormES);
        return page.toPageInfo();
    }

    public List<ReporterFormVO> createFromIssueToDefect(Long projectId, Long[] issueIds, Long organizationId) {
        Assert.notEmpty(issueIds, "error.query.form.issueId.not.empty");

        Map<Long, IssueInfosVO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueIds, false, organizationId);
        return doCreateFromIssueToDefect(issueResponse.values().stream().collect(Collectors.toList()), projectId, organizationId);

    }

    private List<ReporterFormVO> doCreateFromIssueToDefect(List<IssueInfosVO> issueInfosVO, Long projectId, Long organizationId) {
        if (ObjectUtils.isEmpty(issueInfosVO)) {
            return new ArrayList<>();
        }
        List<Long> issues = issueInfosVO.stream().map(IssueInfosVO::getIssueId).collect(Collectors.toList());
        List<IssueLinkDTO> linkDTOS = testCaseService.getLinkIssueFromIssueToTest(projectId, issues);
        Long[] linkedIssues = linkDTOS.stream().map(IssueLinkDTO::getIssueId).toArray(Long[]::new);
        List<TestCycleCaseVO> cycleCaseDTOS = testCycleCaseService.queryInIssues(linkedIssues, projectId, organizationId);

        return issueInfosVO.stream().sorted(Comparator.comparing(IssueInfosVO::getIssueId).reversed())
                .map(ReporterFormVO::new).peek(v -> v.populateLinkedTest(linkDTOS)
                        .populateLinkedIssueCycle(cycleCaseDTOS).countDefect())
                .collect(Collectors.toList());

    }


    public List<DefectReporterFormVO> createFormDefectFromIssue(Long projectId, Long[] issueIds, Long organizationId) {
        Assert.notEmpty(issueIds, "error.query.form.issueId.not.empty");

        Map<Long, IssueInfosVO> issueResponse = testCaseService.getIssueInfoMap(projectId, issueIds, false, organizationId);
        return doCreateFromDefectToIssue(issueResponse.values().stream().collect(Collectors.toList()), projectId, organizationId);
    }


    @Override
    public PageInfo<ReporterFormVO> createFormDefectFromIssue(Long projectId, SearchDTO searchDTO, Pageable pageable, Long organizationId) {
        List<Long> issueIdsList = testCycleCaseDefectRelMapper.queryIssueIdAndDefectId(projectId);
        if (ObjectUtils.isEmpty(issueIdsList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        Long[] issueIds = issueIdsList.stream().toArray(Long[]::new);
        Map args = Optional.ofNullable(searchDTO.getOtherArgs()).orElseGet(HashMap::new);

        args.put("issueIds", issueIds);
        if (searchDTO.getOtherArgs() == null) {
            searchDTO.setOtherArgs(args);
        }
        // 此处假设返回的是 long数组所有值
        Long[] allFilteredIssues = testCaseService.queryIssueIdsByOptions(searchDTO, projectId).stream().sorted(Comparator.reverseOrder()).toArray(Long[]::new);
        if (ObjectUtils.isEmpty(allFilteredIssues)) {
            return new PageInfo<>(new ArrayList<>());
        }
        int pageNum = pageable.getPageNumber()- 1;
        int pageSize = pageable.getPageSize();
        int highPage = (pageNum + 1) * pageSize - 1;
        int lowPage = pageNum * pageSize;
        //创建一个Long数组，将对应分页的issuesId传给它
        int size = highPage - allFilteredIssues.length >= 0 ? allFilteredIssues.length - lowPage : pageSize;

        Long[] pagedIssues = new Long[size];
        System.arraycopy(allFilteredIssues, lowPage, pagedIssues, 0, size);
        // 得到包装好的报表List
        List<DefectReporterFormVO> reporterFormES = createFormDefectFromIssue(projectId, pagedIssues, organizationId);

        return new CustomPage(reporterFormES, allFilteredIssues);
    }


    private List<DefectReporterFormVO> doCreateFromDefectToIssue(List<IssueInfosVO> issueInfosVO, Long projectId, Long organizationId) {
        List<DefectReporterFormVO> formES = Lists.newArrayList();
        if (ObjectUtils.isEmpty(issueInfosVO)) {
            return formES;
        }
        List<Long> issues = new ArrayList<>();
        for (IssueInfosVO infos : issueInfosVO) {
            DefectReporterFormVO form = new DefectReporterFormVO(infos);
            formES.add(form);
            issues.add(infos.getIssueId());
        }
        Long[] issueIds = issues.toArray(new Long[issues.size()]);
        List<TestCycleCaseDefectRelDTO> defectLists = testCycleCaseDefectRelMapper.queryInIssues(issueIds, projectId);
        if (ObjectUtils.isEmpty(defectLists)) {
            return formES;
        }

        Map<Long, List<TestCycleCaseDefectRelDTO>> caseDefectLinkMap = defectLists.stream()
                .filter(u -> u.getDefectType().equals(TestCycleCaseDefectCode.CYCLE_CASE)).collect(Collectors.groupingBy(TestCycleCaseDefectRelDTO::getDefectLinkId));
        Long[] caseIds = defectLists.stream()
                .filter(u -> u.getDefectType().equals(TestCycleCaseDefectCode.CYCLE_CASE)).map(TestCycleCaseDefectRelDTO::getDefectLinkId).distinct().toArray(Long[]::new);

        Map<Long, List<TestCycleCaseDefectRelDTO>> stepDefectLinkMap = defectLists.stream()
                .filter(u -> u.getDefectType().equals(TestCycleCaseDefectCode.CASE_STEP)).collect(Collectors.groupingBy(TestCycleCaseDefectRelDTO::getDefectLinkId));
        Long[] stepIds = defectLists.stream()
                .filter(u -> u.getDefectType().equals(TestCycleCaseDefectCode.CASE_STEP)).map(TestCycleCaseDefectRelDTO::getDefectLinkId).toArray(Long[]::new);

        List<Long> issueIdLists = new ArrayList<>();
        List<TestCycleCaseVO> cycleCases = null;
        List<TestCycleCaseStepVO> cycleCaseSteps = null;
        if (caseIds.length > 0) {
            cycleCases = modelMapper.map(testCycleCaseMapper.queryCycleCaseForReporter(caseIds), new TypeToken<List<TestCycleCaseVO>>() {
            }.getType());
            issueIdLists.addAll(cycleCases.stream().map(TestCycleCaseVO::getIssueId).collect(Collectors.toList()));

        }
        if (stepIds.length > 0) {
            cycleCaseSteps = modelMapper.map(testCycleCaseStepMapper.queryCycleCaseForReporter(stepIds), new TypeToken<List<TestCycleCaseStepVO>>() {
            }.getType());
            issueIdLists.addAll(cycleCaseSteps.stream().map(TestCycleCaseStepVO::getCaseId).collect(Collectors.toList()));

        }

        List<IssueLinkDTO> linkDTOS = testCaseService.getLinkIssueFromTestToIssue(projectId, issueIdLists);

        if (cycleCases != null) {
            DefectReporterFormVO.populateCaseIssueLink(linkDTOS, cycleCases);
            for (DefectReporterFormVO form : formES) {
                form.populateCycleCase(cycleCases, caseDefectLinkMap);
            }
        }
        if (cycleCaseSteps != null) {
            DefectReporterFormVO.populateStepIssueLink(linkDTOS, cycleCaseSteps);
            for (DefectReporterFormVO form : formES) {
                form.populateCycleCaseStep(cycleCaseSteps, stepDefectLinkMap);
            }
        }

        if (!issueIdLists.isEmpty()) {
            Map<Long, IssueInfosVO> map = testCaseService.getIssueInfoMap(projectId, issueIdLists.toArray(new Long[issueIdLists.size()]), false, organizationId);
            formES.forEach(v -> v.populateIssueInfo(map));
        }

        formES = formES.stream().sorted(Comparator.comparing(DefectReporterFormVO::getIssueId).reversed()).collect(Collectors.toList());
        return formES;
    }
}
