import React, { memo } from 'react';
import isEqual from 'react-fast-compare';
import { observer } from 'mobx-react';
import './TreeTitle.scss';
import { SmartTooltip, TestProgressLine } from '../../../../../components';

const TreeTitle = ({
  title,
  progress,
  data,
}) => (
  <div className="c7ntest-tree-title">
    <SmartTooltip width={78}>
      {title}
    </SmartTooltip>
    <TestProgressLine style={{ marginLeft: data.type === 'cycle' || data.type === 'temp' ? '18px' : 0 }} progress={progress} />
  </div>
);

export default observer(memo(TreeTitle, isEqual));
