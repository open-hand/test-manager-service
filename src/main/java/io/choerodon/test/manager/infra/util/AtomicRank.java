package io.choerodon.test.manager.infra.util;

/**
 * @author jiaxu.cui@hand-china.com 2020/8/4 下午3:18
 */
public class AtomicRank {

    private String initValue;

    public AtomicRank(String initValue) {
        this.initValue = initValue;
    }

    public String getNext(){
        return this.initValue = RankUtil.genNext(initValue);
    }
}
