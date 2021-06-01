import React from 'react';
import { Tooltip, Icon } from 'choerodon-ui';

function Tip({ title }) {
  return (
    <Tooltip title={title}>
      <Icon type="help" style={{ color: 'var(--text-color4)', marginLeft: 5 }} />
    </Tooltip>
  );
}
export default Tip;
