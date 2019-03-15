package io.choerodon.test.manager.domain.test.manager.entity;

import io.choerodon.agile.infra.common.utils.RankUtil;
import io.choerodon.test.manager.domain.repository.TestCycleRepository;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleEFactory;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by 842767365@qq.com on 6/11/18.
 */
@Component
@Scope("prototype")
public class TestCycleE {

    public static final String FOLDER = "folder";
    public static final String CYCLE = "cycle";
    public static final String TEMP = "temp";
    public static final String TEMP_CYCLE_NAME = "临时";

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

    private CountMap cycleCaseList;

    private Long folderId;

    private Long lastUpdatedBy;

    private String rank;

    private String lastRank;

    private String nextRank;

    @Autowired
    TestCycleRepository testCycleRepository;

    public List<TestCycleE> queryAll() {
        return testCycleRepository.queryAll();
    }

    public List<TestCycleE> querySelf() {
        return testCycleRepository.query(this);
    }

    public List<TestCycleE> queryChildCycle() {
        return testCycleRepository.queryChildCycle(this);
    }

    public TestCycleE queryOne() {
        return testCycleRepository.queryOne(this);
    }


    public TestCycleE cloneCycle(TestCycleE proto) {
        parentCycleId = Optional.ofNullable(parentCycleId).orElse(proto.getParentCycleId());
        cycleName = Optional.ofNullable(cycleName).orElse(proto.getCycleName());
        versionId = Optional.ofNullable(versionId).orElse(proto.getVersionId());
        description = Optional.ofNullable(description).orElse(proto.getDescription());
        build = Optional.ofNullable(build).orElse(proto.getBuild());
        environment = Optional.ofNullable(environment).orElse(proto.getEnvironment());
        fromDate = Optional.ofNullable(fromDate).orElse(proto.getFromDate());
        toDate = Optional.ofNullable(toDate).orElse(proto.getToDate());
        type = Optional.ofNullable(type).orElse(proto.getType());
        folderId = Optional.ofNullable(folderId).orElse(proto.getFolderId());
        rank = Optional.ofNullable(rank).orElse(proto.getRank());
        return addSelf();
    }

    public List<TestCycleE> getChildFolder() {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setParentCycleId(cycleId);
        testCycleE.setType(FOLDER);
        return testCycleE.querySelf();
    }

    public List<TestCycleE> getChildFolderByRank() {
        TestCycleE testCycleE = TestCycleEFactory.create();
        testCycleE.setParentCycleId(cycleId);
        testCycleE.setType(FOLDER);
        return testCycleRepository.queryChildFolderByRank(testCycleE);
    }

    public List<TestCycleE> getChildFolder(List<TestCycleE> testCycleES) {
        return testCycleES.stream().filter(v -> this.cycleId.equals(v.getParentCycleId()) && v.getType().equals(FOLDER)).collect(Collectors.toList());
    }

    public List<TestCycleE> querySelfWithBar(Long[] versionIds, Long assignedTo) {
        return testCycleRepository.queryBar(versionIds, assignedTo);
    }

    public List<TestCycleE> querySelfWithBarOneCycle(Long cycleId) {
        return testCycleRepository.queryBarOneCycle(cycleId);
    }

    public String getLastedRank() {
        return testCycleRepository.getLastedRank(this);
    }

    public TestCycleE addSelf() {
        return testCycleRepository.insert(this);
    }

    public void checkRank() {
        if (testCycleRepository.getCount(this) != 0
                && StringUtils.isEmpty(testCycleRepository.getLastedRank(this))) {
            fixRank(this);
        }
    }

    private void fixRank(TestCycleE testCycleE) {
        List<TestCycleE> cycleES;
        if (testCycleE.getType().equals(CYCLE)) {
            cycleES = testCycleRepository.queryCycleInVersion(testCycleE);
        } else {
            TestCycleE testCycleE1 = TestCycleEFactory.create();
            testCycleE1.setCycleId(testCycleE.getParentCycleId());
            cycleES = testCycleRepository.queryChildCycle(testCycleE1);
        }
        for (int a = 0; a < cycleES.size(); a++) {
            TestCycleE testCycleETemp = cycleES.get(a);
            List<TestCycleE> list = testCycleETemp.querySelf();
            TestCycleE testCycleETemp1 = list.get(0);
            if (a == 0) {
                testCycleETemp1.setRank(RankUtil.Operation.INSERT.getRank(null, null));
            } else {
                testCycleETemp1.setRank(RankUtil.Operation.INSERT.getRank(cycleES.get(a - 1).getRank(), null));
            }
            testCycleETemp1 = testCycleETemp1.updateSelf();
            cycleES.set(a, testCycleETemp1);
        }
    }

    public TestCycleE updateSelf() {
        return testCycleRepository.update(this);
    }

    public void deleteSelf() {
        testCycleRepository.delete(this);
    }

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

    public void setCycleCaseList(List<Map<String, Object>> cycleCaseList) {
        CountMap map = new CountMap();
        cycleCaseList.forEach(v -> {
                    ProcessBarSection processBarSection = (ProcessBarSection) v.get("processBarSection");
                    map.put((String) v.get("color"), processBarSection);
                }
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

    public void countChildStatus(List<TestCycleE> cycleCaseList) {
        cycleCaseList.forEach(v ->
                this.cycleCaseList.merge(v.getCycleCaseList())
        );

    }

    private static class CountMap extends HashMap<String, ProcessBarSection> {
        private void merge(Map<String, ProcessBarSection> plus) {
            plus.forEach((k, v) -> {
                if (this.containsKey(k)) {
                    ProcessBarSection processBarSection = super.get(k);
                    processBarSection.setCounts(processBarSection.getCounts() + v.getCounts());
                    this.put(k, processBarSection);
                } else {
                    this.put(k, v);
                }
            });
        }
    }

    private static class ProcessBarSection {
        private Long statusId;
        private String statusName;
        private Long counts;
        private Long projectId;

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
}
