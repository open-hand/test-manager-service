import React, { memo } from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage } from 'react-intl';
import { Tooltip, Menu } from 'choerodon-ui';
import _ from 'lodash';
import {
  SelectFocusLoad, StatusTags, DragTable, SmartTooltip,
} from '../../../../components';
import './TestPlanTable.less';
import TableDropMenu from '../../../../common/TableDropMenu';

const propTypes = {
  loading: PropTypes.bool.isRequired,
  statusList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  pagination: PropTypes.shape({}).isRequired,
  dataSource: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  onDragEnd: PropTypes.func.isRequired,
  onTableChange: PropTypes.func.isRequired,
  onLastUpdatedByChange: PropTypes.func.isRequired,
  onAssignedToChange: PropTypes.func.isRequired,
  onTableRowClick: PropTypes.func.isRequired,
  onDeleteExecute: PropTypes.func.isRequired,
};
const TestPlanTable = ({
  loading,
  statusList,
  pagination,
  dataSource,
  onDragEnd,
  onTableChange,
  onLastUpdatedByChange,
  onAssignedToChange,
  onTableRowClick,
  onDeleteExecute,
}) => {
  const renderMenu = (text, record) => {
    const handleItemClick = ({ key }) => {
      if (key === 'delete') {
        onDeleteExecute(record);
      }
    };
    const menu = (
      <Menu onClick={handleItemClick}>
        <Menu.Item key="delete">
          <Tooltip placement="top" title={<FormattedMessage id="delete" />}>
            <span style={{ cursor: 'pointer' }} role="none"><FormattedMessage id="delete" /></span>
          </Tooltip>
        </Menu.Item>
      </Menu>
    );
    return (
      <TableDropMenu
        menu={menu}
        text={text}
        isHasMenu={record.projectId !== 0}
        onClickEdit={onTableRowClick.bind(this, record)}
      />
    );
  };

  const columns = [{
    title: <span>用例名称</span>,
    dataIndex: 'summary',
    key: 'summary',
    filters: [],
    flex: 2,
    render: (text, record) => renderMenu(record.issueInfosVO.summary, record),
  }, {
    title: <FormattedMessage id="cycle_executeBy" />,
    dataIndex: 'lastUpdateUser',
    key: 'lastUpdateUser',
    flex: 1,
    render(lastUpdateUser) {
      return (
        <div
          className="c7ntest-text-dot"
        >
          {lastUpdateUser && lastUpdateUser.realName}
        </div>
      );
    },
  }, {
    title: <FormattedMessage id="cycle_assignedTo" />,
    dataIndex: 'assigneeUser',
    key: 'assigneeUser',
    flex: 1,
    render(assigneeUser) {
      return (
        <div
          className="c7ntest-text-dot"
        >
          {assigneeUser && assigneeUser.realName}
        </div>
      );
    },
  }, {
    title: <FormattedMessage id="status" />,
    dataIndex: 'executionStatus',
    key: 'executionStatus',
    filters: statusList.map(status => ({ text: status.statusName, value: status.statusId.toString() })),
    flex: 1,
    render(executionStatus) {
      const statusColor = _.find(statusList, { statusId: executionStatus })
        ? _.find(statusList, { statusId: executionStatus }).statusColor : '';
      return (
        _.find(statusList, { statusId: executionStatus }) && (
          <StatusTags
            color={statusColor}
            name={_.find(statusList, { statusId: executionStatus }).statusName}
          />
        )
      );
    },
  }];

  return (
    <div className="c7ntest-TestPlan-content-right-bottom">
      <div style={{ display: 'flex', margin: '20px 0px', alignItems: 'center' }}>
        <div style={{
          fontWeight: 600,
          marginRight: 10,
          fontSize: '14px',
        }}
        >
          快速筛选:
        </div>
        <SelectFocusLoad
          allowClear
          className="c7ntest-select"
          style={{ width: 200 }}
          placeholder={<FormattedMessage id="cycle_executeBy" />}
          getPopupContainer={trigger => trigger.parentNode}
          type="user"
          onChange={onLastUpdatedByChange}
        />
        <SelectFocusLoad
          allowClear
          style={{ marginLeft: 20, width: 200 }}
          className="c7ntest-select"
          placeholder={<FormattedMessage id="cycle_assignedTo" />}
          getPopupContainer={trigger => trigger.parentNode}
          type="user"
          onChange={onAssignedToChange}
        />
      </div>
      <DragTable
        pagination={pagination}
        loading={loading}
        onChange={onTableChange}
        dataSource={dataSource}
        columns={columns}
        onDragEnd={onDragEnd}
        dragKey="executeId"
      />
    </div>
  );
};
TestPlanTable.propTypes = propTypes;
export default memo(TestPlanTable);
