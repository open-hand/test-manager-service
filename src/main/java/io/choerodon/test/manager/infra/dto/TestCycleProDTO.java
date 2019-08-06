package io.choerodon.test.manager.infra.dto;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;

public class TestCycleProDTO {

    private Long cycleId;

    private Long parentCycleId;

    private String cycleName;

    private Long versionId;

    private String description;

    private String build;

    private String environment;

    private Date fromDate;

    private Date toDate;

    private String type;

    private Long objectVersionNumber;

    private Long createdBy;

    private TestCycleProDTO.CountMap cycleCaseList;

    private Long folderId;

    private Long lastUpdatedBy;

    private String rank;

    private String lastRank;

    private String nextRank;

    private Long projectId;

    public Long getCycleId() {
        return cycleId;
    }

    public Long getParentCycleId() {
        return parentCycleId;
    }

    public String getCycleName() {
        return cycleName;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getDescription() {
        return description;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getBuild() {
        return build;
    }

    public String getEnvironment() {
        return environment;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public String getType() {
        return type;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public Map getCycleCaseList() {
        return cycleCaseList;
    }

    public void setCycleCaseList(List<Map<Long, Object>> cycleCaseList) {
        TestCycleProDTO.CountMap map = new TestCycleProDTO.CountMap();
        cycleCaseList.forEach(v -> map.put((Long) v.get("statusId"), (TestCycleProDTO.ProcessBarSection) v.get("processBarSection"))
        );
        this.cycleCaseList = map;
    }

    public void setCycleId(Long cycleId) {
        this.cycleId = cycleId;
    }

    public void setParentCycleId(Long parentCycleId) {
        this.parentCycleId = parentCycleId;
    }

    public void setCycleName(String cycleName) {
        this.cycleName = cycleName;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public void countChildStatus(List<TestCycleProDTO> cycleCaseList) {
        if (cycleCaseList != null) {
            cycleCaseList.forEach(v ->
                    this.cycleCaseList.merge(v.getCycleCaseList())
            );
        }
    }

    private static class CountMap extends LinkedHashMap<Long, TestCycleProDTO.ProcessBarSection> {
        private void merge(Map<Long, TestCycleProDTO.ProcessBarSection> plus) {
            plus.forEach((k, v) -> {
                TestCycleProDTO.ProcessBarSection processBarSection = super.get(k);
                if (processBarSection != null) {
                    processBarSection.setCounts(processBarSection.getCounts() + v.getCounts());
                    this.put(k, processBarSection);
                } else {
                    TestCycleProDTO.ProcessBarSection newPro = new TestCycleProDTO.ProcessBarSection();
                    BeanUtils.copyProperties(v, newPro);
                    this.put(k, newPro);
                }
            });
            LinkedHashMap<Long, TestCycleProDTO.ProcessBarSection> res = this.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            this.clear();
            this.putAll(res);
        }
    }

    public static class ProcessBarSection {
        private String color;
        private Long statusId;
        private String statusName;
        private Long counts;
        private Long projectId;

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Long getStatusId() {
            return statusId;
        }

        public void setStatusId(Long statusId) {
            this.statusId = statusId;
        }

        public String getStatusName() {
            return statusName;
        }

        public void setStatusName(String statusName) {
            this.statusName = statusName;
        }

        public Long getCounts() {
            return counts;
        }

        public void setCounts(Long counts) {
            this.counts = counts;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
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

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
