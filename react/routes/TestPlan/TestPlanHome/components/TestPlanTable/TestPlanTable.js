import React, { memo } from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage } from 'react-intl';
import { Button } from 'choerodon-ui';
import _ from 'lodash';
import {
  SelectFocusLoad, StatusTags, DragTable, SmartTooltip,
} from '../../../../../components';
import { getUsers } from '../../../../../api/IamApi';
import { renderPriority } from '../../../../IssueManage/IssueManageComponent/IssueTable/tags';

const propTypes = {
  loading: PropTypes.bool.isRequired,
  prioritys: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
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
  prioritys,
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
  const columns = [{
    title: <span>用例名称</span>,
    dataIndex: 'summary',
    key: 'summary',
    filters: [],
    flex: 2,
    render(issueId, record) {
      const { issueInfosVO } = record;
      return (
        issueInfosVO && (
        <SmartTooltip style={{ color: '#3F51B5' }}>
          {issueInfosVO.summary}
        </SmartTooltip>
        )
      );
    },
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
    title: <span>用例优先级</span>,
    dataIndex: 'priorityId',
    key: 'priorityId',
    filters: prioritys.map(priority => ({ text: priority.name, value: priority.id.toString() })),
    flex: 1,
    render(issueId, record) {
      const { issueInfosVO } = record;
      return (
        issueInfosVO && renderPriority(issueInfosVO.priorityVO)
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
  }, {
    title: '',
    key: 'action',
    flex: 1,
    render: (text, record) => (
      record.projectId !== 0
        && (
          <div style={{ display: 'flex' }}>
            <div className="c7ntest-flex-space" />
            <Button
              shape="circle"
              funcType="flat"
              icon="delete_forever"
              style={{
                marginRight: 10,
              }}
              onClick={(e) => {
                e.stopPropagation();
                onDeleteExecute(record);
              }}
            />
          </div>
        )
    ),
  }];

  return (
    <div className="c7ntest-TestPlan-content-right-bottom">
      <div style={{ display: 'flex', marginBottom: 20, alignItems: 'center' }}>
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
          type="user"
          onChange={onLastUpdatedByChange}
        />
        <SelectFocusLoad
          allowClear
          style={{ marginLeft: 20, width: 200 }}
          className="c7ntest-select"
          placeholder={<FormattedMessage id="cycle_assignedTo" />}
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
        onRow={record => ({
          onClick: (event) => { onTableRowClick(record); },
        })}
        dragKey="executeId"
      />
    </div>
  );
};
TestPlanTable.propTypes = propTypes;
export default memo(TestPlanTable);
