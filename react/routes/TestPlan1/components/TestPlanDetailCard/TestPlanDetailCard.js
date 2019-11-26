import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import { Card } from 'choerodon-ui';
import User from '../../../../components/User';
import './TestPlanDetailCard.less';

export default observer(() => (
  <Card className="c7ntest-testPlan-detailCard" title="测试计划详情"> 
    <div className="c7ntest-testPlan-detailCard-content">
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.16rem' }}>
        <div className="c7ntest-testPlan-detailCard-content-item">
          <span className="c7ntest-testPlan-detailCard-content-item-field">起止时间</span>
          <span className="c7ntest-testPlan-detailCard-content-item-value">2019-10-31～2019-11-30</span>
        </div>
        <div className="c7ntest-testPlan-detailCard-content-item">
          <span className="c7ntest-testPlan-detailCard-content-item-field">负责人</span>
          <span className="c7ntest-testPlan-detailCard-content-item-value">
            <User user={
                  { 
                    loginName: '20615',
                    realName: '李文斐',
                    name: '李文斐',  
                    imageUrl: null,
                  }
              }
            />
          </span>
        </div>
      </div>
      <div className="c7ntest-testPlan-detailCard-content-item">
        <span className="c7ntest-testPlan-detailCard-content-item-field">描述</span>
        <span className="c7ntest-testPlan-detailCard-content-item-value">这是0.20.0版本的测试计划</span>
      </div>
    </div>
  </Card>
));
