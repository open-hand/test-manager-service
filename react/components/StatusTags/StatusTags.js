import React, { memo } from 'react';
import { Tooltip } from 'choerodon-ui/pro';
import './StatusTags.less';

const Color = {
  success: '#00bf96',
  error: '#f44336',
};

const StatusTags = ({
  style,
  name, color, colorCode,
}) => (
  <div
    className="c7ntest-status-tags"
    style={{
      background: color || Color[colorCode] || 'rgba(0, 0, 0, 0.28)',
      ...style,
    }}
  >
    <Tooltip title={name || ''}><div>{ name || '' }</div></Tooltip>
  </div>
);
export default memo(StatusTags);
