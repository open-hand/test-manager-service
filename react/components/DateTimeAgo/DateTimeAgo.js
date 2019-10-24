import React from 'react';
import { Choerodon } from '@choerodon/boot';
import TimeAgo from 'timeago-react';
import { Tooltip } from 'choerodon-ui';

const Timeago = ({
  date,
}) => (
  <div>
    <Tooltip placement="top" title={date || ''}>
      <TimeAgo datetime={date || ''} locale={Choerodon.getMessage('zh_CN', 'en')} />
    </Tooltip>
  </div>
);
export default Timeago;
