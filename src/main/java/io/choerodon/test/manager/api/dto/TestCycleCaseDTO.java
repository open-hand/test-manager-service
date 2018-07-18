package io.choerodon.test.manager.api.dto;

import io.choerodon.agile.api.dto.IssueLinkDTO;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseDefectRelE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.infra.dataobject.TestCycleCaseAttachmentRelDO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
public class TestCycleCaseDTO {
    private Long executeId;

    private Long cycleId;

    private Long issueId;

    private String rank;

	private Long executionStatus;

	private String executionStatusName;

    private Long assignedTo;

    private String comment;

    private Long objectVersionNumber;

    private String lastRank;

    private String nextRank;

	private String reporterRealName;

	private String reporterJobNumber;

	private String assignedUserRealName;

	private String assignedUserJobNumber;

	private Long lastUpdatedBy;

    private Date lastUpdateDate;

	private String cycleName;

	private IssueInfosDTO issueInfosDTO;

	private String folderName;

	private String versionName;

    private List<TestCycleCaseAttachmentRelDTO> caseAttachment;

    private List<TestCycleCaseDefectRelDTO> defects;

	private List<TestCycleCaseDefectRelDTO> subStepDefects;

	private List<IssueLinkDTO> issueLinkDTOS;

	public List<TestCycleCaseDefectRelDTO> getSubStepDefects() {
		if (subStepDefects == null) {
			subStepDefects = new ArrayList<>();
		}
		return subStepDefects;
	}

	public List<IssueLinkDTO> getIssueLinkDTOS() {
		return issueLinkDTOS;
	}

	public void setIssueLinkDTOS(List<IssueLinkDTO> issueLinkDTOS) {
		this.issueLinkDTOS = issueLinkDTOS;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public void setSubStepDefects(List<TestCycleCaseDefectRelDTO> subStepDefects) {
		this.subStepDefects = subStepDefects;
	}

	public String getExecutionStatusName() {
		return executionStatusName;
	}

	public void setExecutionStatusName(String executionStatusName) {
		this.executionStatusName = executionStatusName;
	}

    public Long getExecuteId() {
        return executeId;
    }

    public void setExecuteId(Long executeId) {
        this.executeId = executeId;
    }

    public Long getCycleId() {
        return cycleId;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public Long getIssueId() {
        return issueId;
    }

    public void setIssueId(Long issueId) {
        this.issueId = issueId;
    }

	public Long getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(Long executionStatus) {
		this.executionStatus = executionStatus;
	}

	public Long getAssignedTo() {
//		if (assignedTo == null) {
//			return new Long(0);
//		}
        return assignedTo;
    }

	public IssueInfosDTO getIssueInfosDTO() {
		return issueInfosDTO;
	}

	public void setIssueInfosDTO(IssueInfosDTO issueInfosDTO) {
		this.issueInfosDTO = issueInfosDTO;
	}

	public String getCycleName() {
		return cycleName;
	}

	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

	public String getReporterRealName() {
		return reporterRealName;
	}

	public void setReporterRealName(String reporterRealName) {
		this.reporterRealName = reporterRealName;
	}

	public String getReporterJobNumber() {
		return reporterJobNumber;
    }

	public void setReporterJobNumber(String reporterJobNumber) {
		this.reporterJobNumber = reporterJobNumber;
    }

	public String getAssignedUserJobNumber() {
		return assignedUserJobNumber;
	}

	public void setAssignedUserJobNumber(String assignedUserJobNumber) {
		this.assignedUserJobNumber = assignedUserJobNumber;
	}

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getLastRank() {
        return lastRank;
    }

    public void setLastRank(String lastRank) {
        this.lastRank = lastRank;
    }

    public String getNextRank() {
        return nextRank;
    }

    public void setNextRank(String nextRank) {
        this.nextRank = nextRank;
    }

    public List<TestCycleCaseAttachmentRelDTO> getCaseAttachment() {
        return caseAttachment;
    }

    public void setCaseAttachment(List<TestCycleCaseAttachmentRelDO> caseAttachment) {
        this.caseAttachment = ConvertHelper.convertList(caseAttachment, TestCycleCaseAttachmentRelDTO.class);
    }

    public List<TestCycleCaseDefectRelDTO> getDefects() {
		if (defects == null) {
			defects = new ArrayList<>();
		}
        return defects;
    }

    public void setDefects(List<TestCycleCaseDefectRelE> defects) {
        this.defects = ConvertHelper.convertList(defects, TestCycleCaseDefectRelDTO.class);
    }

	public Long getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(Long lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

	public String getAssignedUserRealName() {
		return assignedUserRealName;
	}

	public void setAssignedUserRealName(String assignedUserRealName) {
		this.assignedUserRealName = assignedUserRealName;
	}
}
