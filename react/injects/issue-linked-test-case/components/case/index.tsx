import React, { useState } from 'react';
import { Icon } from 'choerodon-ui';
import classNames from 'classnames';
import { LinkedTestCase } from '../..';
import styles from './index.less';

interface CaseProps {
  data: LinkedTestCase
}
const Case: React.FC<CaseProps> = () => {
  const [expand, setExpand] = useState(false);
  return (
    <div className={styles.case}>
      <Icon
        type="baseline-arrow_right"
        className={classNames(styles.icon, {
          [styles.icon_expand]: expand,
        })}
      />
    </div>
  );
};

export default Case;
