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
  Table, Icon,
} from 'choerodon-ui';
import { Modal, Menu, Dropdown } from 'choerodon-ui/pro';
import { FormattedMessage } from 'react-intl';
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
  const renderAction = (text, record) => {
    if (record.projectId === 0) {
      return null;
    }
    const handleMenuClick = (e) => {
      switch (e.key) {
        case 'edit': {
          onEditStatusClick(record);
          break;
        }
        case 'delete': {
          deleteStatus(record);
          break;
        }
        default: {
          break;
        }
      }
    };
    const menu = (
      <Menu onClick={handleMenuClick}>
        <Menu.Item key="edit">
          编辑
        </Menu.Item>
        <Menu.Item key="delete">
          删除
        </Menu.Item>
      </Menu>
    );
    return (
      <Dropdown
        overlay={menu}
        trigger={['click']}
      >
        <Icon
          type="more_vert"
          style={{
            fontSize: 18,
            cursor: 'pointer',
            color: 'var(--primary-color)',
          }}
        />
      </Dropdown>
    );
  };

  const columns = [
    {
      title: <FormattedMessage id="status_name" />,
      dataIndex: 'statusName',
      key: 'statusName',
      filters: [],
      width: '40%',
      onFilter: (value, record) => {
        const reg = new RegExp(value, 'g');
        return reg.test(record.statusName);
      },
      render: (text, record) => text,
    },
    {
      dataIndex: 'action',
      key: 'action',
      render: (text, record) => renderAction(text, record),
    },
    {
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
