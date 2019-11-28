import React, { PureComponent } from 'react';
import { Choerodon } from '@choerodon/boot';
import PropTypes, { func } from 'prop-types';
import {
  Input, Icon, Select, Tooltip, Button,
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { Table } from 'choerodon-ui/pro/lib';

import './StepTable.less';


const { Column } = Table;
function StepTable(props) {
  const { dataSet } = props;
  return (
    <Table dataSet={dataSet}>
      <Column name="" />
      <Column name="testStep" />
      <Column name="testData" />
      <Column name="expectedResult" />
      <Column name="stepStatus" />
      <Column name="stepAttachment" />
      <Column name="comment" />
      <Column name="defects" />
      <Column name="action" lock="right" />
    </Table>
  );
}

export default StepTable;
