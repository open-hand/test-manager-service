package io.choerodon.test.manager.infra.util;

import io.choerodon.test.manager.infra.util.arilerank.AgileRank;
import io.choerodon.core.exception.CommonException;
import org.apache.commons.lang.StringUtils;

/**
 * Created by jian_zhang02@163.com on 2018/5/28.
 */
public class RankUtil {

    private RankUtil() {
    }

    public static String mid() {
        AgileRank minRank = AgileRank.min();
        AgileRank maxRank = AgileRank.max();
        return minRank.between(maxRank).format();
    }

    public static String genNext(String rank) {
        return AgileRank.parse(rank).genNext().format();
    }

    public static String genPre(String minRank) {
        return AgileRank.parse(minRank).genPrev().format();
    }

    public static String between(String leftRank, String rightRank) {
        AgileRank left = AgileRank.parse(leftRank);
        AgileRank right = AgileRank.parse(rightRank);
        return left.between(right).format();
    }


    public enum Operation {
        INSERT {
            @Override
            public String getRank(String lastRank, String nextRank) {
                String rank;
                if (StringUtils.isEmpty(lastRank) && StringUtils.isEmpty(nextRank)) {
                    rank = RankUtil.mid();
                } else {
                    rank = super.getRank(lastRank, nextRank);
                }
                return rank;
            }
        }, UPDATE {
            @Override
            public String getRank(String lastRank, String nextRank) {
                String rank;
                if (StringUtils.isEmpty(lastRank) && StringUtils.isEmpty(nextRank)) {
                    rank = null;
                } else {
                    rank = super.getRank(lastRank, nextRank);
                }
                return rank;
            }
        };

        public String getRank(String lastRank, String nextRank) {
            String rank;
            if (StringUtils.isEmpty(lastRank) && StringUtils.isEmpty(nextRank)) {
                throw new CommonException("error.get.rank");
            } else if (StringUtils.isEmpty(lastRank)) {
                lastRank = RankUtil.genPre(nextRank);
                rank = RankUtil.between(lastRank, nextRank);
            } else if (StringUtils.isEmpty(nextRank)) {
                nextRank = RankUtil.genNext(lastRank);
                rank = RankUtil.between(lastRank, nextRank);
            } else {
                rank = RankUtil.between(lastRank, nextRank);
            }
            return rank;
        }
    }

}
