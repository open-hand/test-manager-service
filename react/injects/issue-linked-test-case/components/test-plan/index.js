import React, { useCallback } from 'react';
import classNames from 'classnames';
import { Icon } from 'choerodon-ui';
import { TestPlanLink } from '@/common/utils';
import UserHead from '@/components/UserHead';
import StatusTag from '@/components/StatusTag';
import { useHistory } from 'react-router';
import styles from './index.less';

const TestPlanItem = ({
  data,
}) => {
  const history = useHistory();
  const handleClick = useCallback(() => {
    history.push(TestPlanLink('11'));
  }, [history]);
  return (
    <div className={styles.item}>
      <div role="none" className={classNames(styles.name)} onClick={handleClick}>
        <Icon type="insert_invitation" style={{ marginRight: 6 }} />
        计划名称
      </div>
      <div className={styles.assignee}>
        <UserHead user={{
          realName: '啦啦啦',
        }}
        />
      </div>
      <StatusTag
        className={styles.status}
        status={{
          colour: 'red', name: 'statusName', type: 'todo',
        }}
      />
    </div>
  );
};

export default TestPlanItem;
