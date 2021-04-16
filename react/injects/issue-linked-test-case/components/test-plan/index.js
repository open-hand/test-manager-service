import React, { useCallback } from 'react';
import classNames from 'classnames';
import { Icon } from 'choerodon-ui';
import { executeDetailLink } from '@/common/utils';
import UserHead from '@/components/UserHead';
import StatusTag from '@/components/StatusTag';
import { useHistory } from 'react-router';
import styles from './index.less';

const TestPlanItem = ({
  data,
}) => {
  const history = useHistory();
  const handleClick = useCallback(() => {
    history.push(executeDetailLink(data.executeId, {
      cycle_id: data.cycleId,
      plan_id: data.planId,
    }));
  }, [data.cycleId, data.executeId, data.planId, history]);
  return (
    <div className={styles.item}>
      <div role="none" className={classNames(styles.name)} onClick={handleClick}>
        <Icon type="insert_invitation" style={{ marginRight: 6 }} />
        {data?.planName}
      </div>
      <UserHead
        style={{
          marginLeft: 'auto',
        }}
        user={data?.lastUpdateUser}
      />
      <StatusTag
        style={{
          marginLeft: 10,
        }}
        status={{
          colour: data?.statusColor, name: data?.executionStatusName,
        }}
      />
    </div>
  );
};

export default TestPlanItem;
