/*
 * @Author: LainCarl
 * @Date: 2019-02-26 15:29:21
 * @Last Modified by:   LainCarl
 * @Last Modified time: 2019-02-26 15:29:21
 * @Feature: 状态table
 */

import React, { memo } from 'react';
import PropTypes from 'prop-types';
import {
  Table, Menu,
} from 'choerodon-ui';
import { Modal } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
import TableDropMenu from '../TableDropMenu';
import './StatusTable.less';

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
    Modal.open({
      title: '删除状态',
      children: `确定要删除状态“${data.statusName}”?`,
      onOk: () => { onDeleteOk(data); },
    });
  };
  const renderStatusName = (text, record) => {
    if (record.projectId === 0) {
      return text;
    }
    const menu = (
      <Menu>
        <Menu.Item key="delete">
          <div role="none" onClick={deleteStatus.bind(this, record)}>删除</div>
        </Menu.Item>
      </Menu>
    );
    return (
      <TableDropMenu
        menu={menu}
        text={text}
        isHasMenu={record.projectId !== 0}
        onClickEdit={onEditStatusClick.bind(this, record)}
      />
    );
  };
  const columns = [{
    title: <FormattedMessage id="status_name" />,
    dataIndex: 'statusName',
    key: 'statusName',
    filters: [],
    width: '40%',
    onFilter: (value, record) => {
      const reg = new RegExp(value, 'g');
      return reg.test(record.statusName);
    },
    render: (text, record) => renderStatusName(text, record),
  }, {
    title: <FormattedMessage id="status_comment" />,
    dataIndex: 'description',
    key: 'description',
    filters: [],
    width: '40%',
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
  },
  ];
  return (
    <Table
      filterBarPlaceholder="过滤表"
      filterBar={false}
      rowKey="statusId"
      columns={columns}
      dataSource={dataSource}
    />
  );
};
StatusTable.propTypes = propTypes;
export default memo(StatusTable);
