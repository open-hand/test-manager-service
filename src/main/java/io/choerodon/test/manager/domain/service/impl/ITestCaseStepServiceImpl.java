package io.choerodon.test.manager.domain.service.impl;

import io.choerodon.test.manager.domain.test.manager.entity.TestCaseStepE;
import io.choerodon.test.manager.domain.test.manager.entity.TestCycleCaseStepE;
import io.choerodon.test.manager.domain.test.manager.factory.TestCycleCaseStepEFactory;
import io.choerodon.test.manager.domain.service.ITestCaseStepService;
import io.choerodon.test.manager.domain.service.ITestCycleCaseStepService;
import io.choerodon.agile.infra.common.utils.RankUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by jialongZuo@hand-china.com on 6/11/18.
 */

@Service
public class ITestCaseStepServiceImpl implements ITestCaseStepService {

    @Autowired
    ITestCycleCaseStepService testCycleCaseStepService;


    @Override
    public List<TestCaseStepE> query(TestCaseStepE testCaseStepE) {
        return testCaseStepE.querySelf();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removeStep(TestCaseStepE testCaseStepE) {
        testCaseStepE.querySelf().forEach(v -> deleteCycleCaseStep(v));
        testCaseStepE.deleteSelf();
    }

    private void deleteCycleCaseStep(TestCaseStepE testCaseStepE) {
        TestCycleCaseStepE testCycleCaseStepE = TestCycleCaseStepEFactory.create();
        testCycleCaseStepE.setStepId(testCaseStepE.getStepId());
        testCycleCaseStepService.deleteStep(testCycleCaseStepE);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<TestCaseStepE> batchInsertStep(List<TestCaseStepE> testCaseStepES) {
        List<TestCaseStepE> result = new ArrayList<>();
        String[] rank = new String[1];
        testCaseStepES.forEach(v -> {
            v.setLastRank(rank[0]);
            TestCaseStepE temp = changeStep(v);
            rank[0] = temp.getRank();
            result.add(temp);
        });
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public TestCaseStepE changeStep(TestCaseStepE testCaseStepE) {

        if (testCaseStepE.getStepId() == null) {
            testCaseStepE.setRank(RankUtil.Operation.INSERT.getRank(testCaseStepE.getLastRank(), testCaseStepE.getNextRank()));
            testCaseStepE = testCaseStepE.addSelf();
        } else {
            testCaseStepE.setRank(RankUtil.Operation.UPDATE.getRank(testCaseStepE.getLastRank(), testCaseStepE.getNextRank()));
            testCaseStepE = testCaseStepE.updateSelf();
        }
        return testCaseStepE;
    }

//    enum Operation {
//        INSERT {
//            @Override
//            public String getRank(String lastRank, String nextRank) {
//                String rank;
//                if (StringUtils.isEmpty(lastRank) && StringUtils.isEmpty(nextRank)) {
//                    rank = RankUtil.mid();
//                } else {
//                    rank = super.getRank(lastRank, nextRank);
//                }
//                return rank;
//            }
//        }, UPDATE {
//            @Override
//            public String getRank(String lastRank, String nextRank) {
//                String rank;
//                if (StringUtils.isEmpty(lastRank) && StringUtils.isEmpty(nextRank)) {
//                    rank = null;
//                } else {
//                    rank = super.getRank(lastRank, nextRank);
//                }
//                return rank;
//            }
//        };
//
//        public String getRank(String lastRank, String nextRank) {
//            String rank;
//            if (StringUtils.isEmpty(lastRank) && StringUtils.isEmpty(nextRank)) {
//                throw new CommonException("error.get.rank");
//            } else if (StringUtils.isEmpty(lastRank)) {
//                lastRank = RankUtil.genPre(nextRank);
//                rank = RankUtil.between(lastRank, nextRank);
//            } else if (StringUtils.isEmpty(nextRank)) {
//                nextRank = RankUtil.genNext(lastRank);
//                rank = RankUtil.between(lastRank, nextRank);
//            } else {
//                rank = RankUtil.between(lastRank, nextRank);
//            }
//            return rank;
//        }
//    }

}
