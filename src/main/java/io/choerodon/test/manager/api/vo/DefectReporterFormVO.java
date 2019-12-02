package io.choerodon.test.manager.api.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.choerodon.agile.api.vo.IssueLinkDTO;
import io.choerodon.agile.api.vo.IssueListTestVO;
import io.choerodon.test.manager.infra.dto.TestCycleCaseDefectRelDTO;

/**
 * Created by 842767365@qq.com on 7/16/18.
 */
public class DefectReporterFormVO {

    private IssueInfosVO issueInfosVO;

    List<TestCycleCaseVO> testCycleCaseES = new ArrayList<>();

    List<TestCycleCaseStepVO> testCycleCaseStepES = new ArrayList<>();


    public DefectReporterFormVO(IssueListTestVO issueListTestVO) {
        issueInfosVO = new IssueInfosVO(issueListTestVO);
    }

    public DefectReporterFormVO(IssueInfosVO issueInfosVO) {
        this.issueInfosVO = issueInfosVO;
    }


    public void populateCycleCase(List<TestCycleCaseVO> list, Map<Long, List<TestCycleCaseDefectRelDTO>> caseDefectLinkMap) {
        for (TestCycleCaseVO cases : list) {
            if (caseDefectLinkMap.containsKey(cases.getExecuteId())) {
                for (TestCycleCaseDefectRelDTO defect : caseDefectLinkMap.get(cases.getExecuteId())) {
                    if (defect.getIssueId().equals(issueInfosVO.getIssueId())) {
                        testCycleCaseES.add(cases);
                    }
                }
            }
        }
    }

    public void populateCycleCaseStep(List<TestCycleCaseStepVO> list, Map<Long, List<TestCycleCaseDefectRelDTO>> caseDefectLinkMap) {
        for (TestCycleCaseStepVO cases : list) {
            if (caseDefectLinkMap.containsKey(cases.getExecuteStepId())) {
                for (TestCycleCaseDefectRelDTO defect : caseDefectLinkMap.get(cases.getExecuteStepId())) {
                    if (defect.getIssueId().equals(issueInfosVO.getIssueId())) {
                        testCycleCaseStepES.add(cases);
                    }
                }
            }
        }
    }

    public void populateIssueInfo(Map<Long, IssueInfosVO> infos) {
        for (TestCycleCaseVO caseE : testCycleCaseES) {
            caseE.setIssueInfosVO(infos.get(caseE.getIssueId()));
        }

        for (TestCycleCaseStepVO stepE : testCycleCaseStepES) {
            stepE.setIssueInfosVO(infos.get(stepE.getCaseId()));
        }
    }


    public static void populateCaseIssueLink(List<IssueLinkDTO> issueLinkDTOS, List<TestCycleCaseVO> testCycleCaseVO) {
        for (TestCycleCaseVO caseE : testCycleCaseVO) {
            for (IssueLinkDTO link : issueLinkDTOS) {
                if (caseE.getIssueId().equals(link.getIssueId())) {
                    caseE.addIssueLinkDTOS(link);
                }
            }
        }

    }

    public static void populateStepIssueLink(List<IssueLinkDTO> issueLinkDTOS, List<TestCycleCaseStepVO> testCycleCaseStepES) {

        for (TestCycleCaseStepVO stepE : testCycleCaseStepES) {
            for (IssueLinkDTO link : issueLinkDTOS) {
                if (stepE.getCaseId().equals(link.getIssueId())) {
                    stepE.addIssueLinkDTOS(link);
                }
            }
        }
    }

    public List<TestCycleCaseVO> getTestCycleCaseES() {
        return testCycleCaseES;
    }

    public void setTestCycleCaseES(List<TestCycleCaseVO> testCycleCaseES) {
        this.testCycleCaseES = testCycleCaseES;
    }

    public List<TestCycleCaseStepVO> getTestCycleCaseStepES() {
        return testCycleCaseStepES;
    }

    public void setTestCycleCaseStepES(List<TestCycleCaseStepVO> testCycleCaseStepES) {
        this.testCycleCaseStepES = testCycleCaseStepES;
    }

    public IssueInfosVO getIssueInfosVO() {
        return issueInfosVO;
    }

    public void setIssueInfosVO(IssueInfosVO issueInfosVO) {
        this.issueInfosVO = issueInfosVO;
    }

    public Long getIssueId() {
        return issueInfosVO.getIssueId();
    }
}
