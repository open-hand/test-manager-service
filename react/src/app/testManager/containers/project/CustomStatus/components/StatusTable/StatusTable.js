/*
 * @Author: LainCarl 
 * @Date: 2019-02-26 15:29:21 
 * @Last Modified by:   LainCarl 
 * @Last Modified time: 2019-02-26 15:29:21 
 * @Feature: 状态table
 */

import React, { memo } from 'react';
import PropTypes from 'prop-types';
import { Table, Icon, Modal } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';

const { confirm } = Modal;
const propTypes = {  
  dataSource: PropTypes.arrayOf(PropTypes.shape({})).isRequired, 
  onEditStatusClick: PropTypes.func.isRequired,
  onDeleteOk: PropTypes.func.isRequired,
 
};
const StatusTable = ({
  dataSource,
  onDeleteOk,
  onEditStatusClick,
}) => {
  const deleteStatus = (data) => {
    confirm({
      title: '确定要删除状态?',
      onOk: () => { onDeleteOk(data); },
    });
  };
  const columns = [{
    title: <FormattedMessage id="status_name" />,
    dataIndex: 'statusName',
    key: 'statusName',
    filters: [],
    onFilter: (value, record) => {
      const reg = new RegExp(value, 'g');
      return reg.test(record.statusName);
    },
  }, {
    title: <FormattedMessage id="status_comment" />,
    dataIndex: 'description',
    key: 'description',
    filters: [],
    onFilter: (value, record) => {
      const reg = new RegExp(value, 'g');
      return record.description && reg.test(record.description);
    },
  }, {
    title: <FormattedMessage id="status_color" />,
    dataIndex: 'statusColor',
    key: 'statusColor',
    render(statusColor) {
      return (
        <div style={{ width: 18, height: 18, background: statusColor }} />
      );
    },
  }, {
    title: '',
    key: 'action',
    render: (text, record) => (
      record.projectId !== 0
      && (
        <div>
          <Icon
            type="mode_edit"
            style={{ cursor: 'pointer' }}
            onClick={() => {
              onEditStatusClick(record);
            }}
          />
          <Icon
            type="delete_forever"
            style={{ cursor: 'pointer', marginLeft: 10 }}
            onClick={() => { deleteStatus(record); }}
          />
        </div>
      )
    ),
  }];
  return (
    <Table
      rowKey="statusId"
      columns={columns}
      dataSource={dataSource}
    />
  );
};
StatusTable.propTypes = propTypes;
export default memo(StatusTable);
