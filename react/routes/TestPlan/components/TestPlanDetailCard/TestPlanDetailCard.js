import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Tooltip } from 'choerodon-ui';
import moment from 'moment';
import useHasAgile from '@/hooks/useHasAgile';
import User from '../../../../components/User';
import SmartToolTip from '../../../../components/SmartTooltip';
import './TestPlanDetailCard.less';
import Store from '../../stores';

export default observer(() => {
  const { testPlanStore } = useContext(Store);
  const { planInfo } = testPlanStore;
  const hasAgile = useHasAgile();
  return (
    <div className="c7ntest-testPlan-detailCard-content">
      <div className="c7ntest-testPlan-detailCard-content-item">
        <span className="c7ntest-testPlan-detailCard-content-item-field">起止时间：</span>
        <span className="c7ntest-testPlan-detailCard-content-item-value">
          <Tooltip title={`${moment(planInfo.startDate).format('YYYY-MM-DD') || ''} ~ ${moment(planInfo.endDate).format('YYYY-MM-DD') || ''}`}>
            {`${(planInfo.startDate && planInfo.startDate.split(' ')[0]) || ''}～${(planInfo.endDate && planInfo.endDate.split(' ')[0]) || ''}`}
          </Tooltip>
        </span>
      </div>
      <div className="c7ntest-testPlan-detailCard-content-item">
        <span className="c7ntest-testPlan-detailCard-content-item-field">负责人：</span>
        <span className="c7ntest-testPlan-detailCard-content-item-value">
          <User user={planInfo.managerUser} />
        </span>
      </div>
      {hasAgile && (
        <>
          <div className="c7ntest-testPlan-detailCard-content-item">
            <span className="c7ntest-testPlan-detailCard-content-item-field">所属冲刺：</span>
            <span className="c7ntest-testPlan-detailCard-content-item-value">
              {planInfo.sprintNameDTO?.sprintName ?? '-'}
            </span>
          </div>
          <div className="c7ntest-testPlan-detailCard-content-item">
            <span className="c7ntest-testPlan-detailCard-content-item-field">所属版本：</span>
            <span className="c7ntest-testPlan-detailCard-content-item-value">
              {planInfo.productVersionDTO?.name ?? '-'}
            </span>
          </div>
        </>
      )}
      <div className="c7ntest-testPlan-detailCard-content-item">
        <span className="c7ntest-testPlan-detailCard-content-item-field">描述：</span>
        <SmartToolTip title={planInfo.description || '无'}>{planInfo.description || '无'}</SmartToolTip>
      </div>
    </div>
  );
});
