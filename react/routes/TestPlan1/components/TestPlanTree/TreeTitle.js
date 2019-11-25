import React, { memo } from 'react';
import isEqual from 'react-fast-compare';
import { observer } from 'mobx-react';
import './TreeTitle.scss';
import { SmartTooltip } from '../../../../components';

const TreeTitle = ({
  title,
}) => (
  <div className="c7ntest-tree-title">
    <SmartTooltip title={title} width={150}>
      {title}
    </SmartTooltip>
  </div>
);

export default observer(memo(TreeTitle, isEqual));
