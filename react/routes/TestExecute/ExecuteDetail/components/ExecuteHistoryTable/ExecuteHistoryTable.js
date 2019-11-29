import React, { memo } from 'react';
import PropTypes from 'prop-types';
import { Tooltip } from 'choerodon-ui';
import { Table } from 'choerodon-ui/pro';
import { RichTextShow, User } from '../../../../../components';
import './ExecuteHistoryTable.less';

const { Column } = Table;
const propTypes = {
  dataSet: PropTypes.shape({}).isRequired,
};
const ExecuteHistoryTable = ({
  dataSet,
}) => {
  function renderUser({ value }) {
    return <User user={value} />;
  }
  return (

    // 状态
    <Table dataSet={dataSet}>
      <Column name="user" renderer={renderUser} />
      <Column name="lastUpdateDate" />
      <Column name="field" />
      <Column name="oldValue" />
      <Column name="newValue" />
    </Table>
  );
};
ExecuteHistoryTable.propTypes = propTypes;
export default ExecuteHistoryTable;
