import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Card, Tooltip } from 'choerodon-ui';
import User from '../../../../components/User';
import SmartToolTip from '../../../../components/SmartTooltip';
import './TestPlanDetailCard.less';
import Store from '../../stores';

export default observer(() => {
  const { testPlanStore } = useContext(Store);
  const { planInfo } = testPlanStore;
  return (
    <Card className="c7ntest-testPlan-detailCard" title="测试计划详情"> 
      <div className="c7ntest-testPlan-detailCard-content">
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.16rem' }}>
          <div className="c7ntest-testPlan-detailCard-content-item">
            <span className="c7ntest-testPlan-detailCard-content-item-field">起止时间</span>
            <span className="c7ntest-testPlan-detailCard-content-item-value">
              <Tooltip title={`${planInfo.startDate || ''} ~ ${planInfo.endDate || ''}`}>
                {`${(planInfo.startDate && planInfo.startDate.split(' ')[0]) || ''}～${(planInfo.endDate && planInfo.endDate.split(' ')[0]) || ''}`}
              </Tooltip>
            </span>
          </div>
          <div className="c7ntest-testPlan-detailCard-content-item">
            <span className="c7ntest-testPlan-detailCard-content-item-field">负责人</span>
            <span className="c7ntest-testPlan-detailCard-content-item-value">
              <User user={planInfo.managerUser} />
            </span>
          </div>
        </div>
        <div className="c7ntest-testPlan-detailCard-content-item">
          <span className="c7ntest-testPlan-detailCard-content-item-field">描述</span>
          <SmartToolTip title={planInfo.description} width="3.5rem" />
        </div>
      </div>
    </Card>
  );
});
