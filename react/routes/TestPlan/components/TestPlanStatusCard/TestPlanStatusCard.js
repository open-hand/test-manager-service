import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Card, Tooltip } from 'choerodon-ui';
import Progress from '../../../../components/Progress';
import './TestPlanStatusCard.less';
import Store from '../../stores';

export default observer(() => {
  const { testPlanStore } = useContext(Store);
  const { statusRes } = testPlanStore;
  return (
    <Card className="c7ntest-testPlan-statusCard" title="测试状态总览"> 
      <div className="c7ntest-testPlan-statusCard-content">
        {/* <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={10} title="通过" strokeColor="#00bf96" />
        <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={20} title="失败" strokeColor="#f44336" />
        <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={30} title="重测" strokeColor="#ffb100" />
        <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={60} title="无需测试" strokeColor="#4D90FE" />
        <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={70} title="未执行" strokeColor="rgba(0, 0, 0, 0.2)" /> */}
        {
          statusRes && statusRes.statusVOList && statusRes.statusVOList.length > 0 && statusRes.statusVOList.map(item => (
            <Tooltip title={`${item.statusName}：${item.count || 0}`}>
              <div style={{ flexShrink: 0, paddingRight: '0.3rem', overflow: 'hidden' }}>
                <Progress percent={(item.count && statusRes.total) ? (item.count / statusRes.total).toFixed(2) * 100 : 0} title={item.statusName} strokeColor={item.statuColor} />
              </div>
            </Tooltip>
          ))
        }
      </div>
    </Card>
  );
});
