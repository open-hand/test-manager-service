import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Card } from 'choerodon-ui';
import Progress from '../../../../components/Progress';
import './TestPlanStatusCard.less';

export default observer(() => (
  <Card className="c7ntest-testPlan-statusCard" title="测试状态总览"> 
    <div className="c7ntest-testPlan-statusCard-content">
      <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={10} title="通过" strokeColor="#00bf96" />
      <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={20} title="失败" strokeColor="#f44336" />
      <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={30} title="重测" strokeColor="#ffb100" />
      <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={60} title="无需测试" strokeColor="#4D90FE" />
      <Progress className="c7ntest-testPlan-statusCard-content-progress" percent={70} title="未执行" strokeColor="rgba(0, 0, 0, 0.2)" />
    </div>
  </Card>
));
