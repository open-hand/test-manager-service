import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Tooltip } from 'choerodon-ui';
import './TestPlanStatusCard.less';
import Store from '../../stores';

export default observer(() => {
  const { testPlanStore } = useContext(Store);
  const { statusRes } = testPlanStore;

  const handleQueryExecutesByStatus = (status) => {
    const { filter } = testPlanStore;
    testPlanStore.setMainActiveTab('testPlanTable');
    testPlanStore.setFilter({ ...filter, ...{ executionStatus: status.statusId } });
    testPlanStore.setExecutePagination({ current: 1 });
    testPlanStore.loadExecutes();
  };
  const totalCount = statusRes.total;
  const doneCount = statusRes?.statusVOList?.reduce((res, current) => {
    if (String(current.projectId) === '0' && current.statusName === '未执行') {
      return res;
    }
    return res + current.count;
  }, 0);
  return (
    <div className="c7ntest-testPlan-statusCard">
      <div className="c7ntest-testPlan-statusCard-progress">
        {
          statusRes && statusRes.statusVOList && statusRes.statusVOList.length > 0 && statusRes.statusVOList.map((item) => (
            <Tooltip title={`${item.statusName}：${(item.count && statusRes.total) ? `${Math.round(item.count * 100 / statusRes.total * 100) / 100}%` : 0}`}>
              <div
                style={{
                  flexShrink: 0, overflow: 'hidden', cursor: 'pointer', flexGrow: item.count, background: item.statusColor,
                }}
                role="none"
                onClick={handleQueryExecutesByStatus.bind(this, item)}
              />
            </Tooltip>
          ))
        }
      </div>
      {doneCount || totalCount ? (
        <div className="c7ntest-testPlan-statusCard-count">
          {`已测: ${doneCount ?? 0}/${totalCount ?? 0}`}
        </div>
      ) : null}
    </div>
  );
});
