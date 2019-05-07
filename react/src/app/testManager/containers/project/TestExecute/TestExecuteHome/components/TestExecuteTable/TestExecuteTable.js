import React, { Component } from 'react';
import PropTypes from 'prop-types';
import isEqual from 'react-fast-compare';
import {
  Tooltip, Table, Button, Icon, 
} from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import _ from 'lodash';
import { renderPriority } from '../../../../../../components/IssueManageComponent/IssueTable/tags';
import {
  SelectFocusLoad, StatusTags, SmartTooltip,
} from '../../../../../../components/CommonComponent';
import { getUsers } from '../../../../../../api/IamApi';
import './TestExecuteTable.scss';

const propTypes = {
  loading: PropTypes.bool.isRequired,
  prioritys: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  statusList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  treeAssignedTo: PropTypes.number.isRequired,
  pagination: PropTypes.shape({}).isRequired,
  dataSource: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  onTableChange: PropTypes.func.isRequired,
  onAssignedToChange: PropTypes.func.isRequired,
  onTableRowClick: PropTypes.func.isRequired,
  quickPass: PropTypes.func.isRequired,
  quickFail: PropTypes.func.isRequired,
};
class TestExecuteTable extends Component {
  shouldComponentUpdate(nextProps) {
    return !isEqual(this.props, nextProps);
  }

  render() {
    const {
      prioritys,
      currentCycle,
      statusList,
      onExecuteByChange,
      onAssignedToChange,
      treeAssignedTo,
      dataSource,
      quickPass,
      quickFail,
      onTableRowClick, 
      onTableChange,
      pagination,
      loading,
    } = this.props;
    const {
      cycleId, title, type, cycleCaseList,
    } = currentCycle;
    const prefix = <Icon type="filter_list" />;
    

    const columns = [{
      title: <span>用例名称</span>,
      dataIndex: 'summary',
      key: 'summary',
      filters: [],
      width: '30%',
      render(issueId, record) {
        const { issueInfosDTO } = record;
        return (
          issueInfosDTO && (
          <SmartTooltip style={{ color: '#3F51B5' }}>
            {issueInfosDTO.summary}
          </SmartTooltip>
          )
        );
      },
    }, {
      title: <FormattedMessage id="cycle_executeBy" />,
      dataIndex: 'lastUpdateUser',
      key: 'lastUpdateUser',        
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
      render(issueId, record) {
        const { issueInfosDTO } = record;
        return (
          issueInfosDTO && renderPriority(issueInfosDTO.priorityDTO)
        );
      },
    }, {
      title: <FormattedMessage id="status" />,
      dataIndex: 'executionStatus',
      key: 'executionStatus',
      filters: statusList.map(status => ({ text: status.statusName, value: status.statusId.toString() })),     
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
      width: 90,
      render: (text, record) => (
        record.projectId !== 0
          && (
            <div style={{ display: 'flex' }}>
              <Tooltip title={<FormattedMessage id="execute_quickPass" />}>
                <Button shape="circle" funcType="flat" icon="check_circle" onClick={quickPass.bind(this, record)} />
              </Tooltip>
              <Tooltip title={<FormattedMessage id="execute_quickFail" />}>
                <Button shape="circle" funcType="flat" icon="cancel" onClick={quickFail.bind(this, record)} />
              </Tooltip>
            </div>
          )
      ),
    }];
    const nameColumn = {
      title: <FormattedMessage id="cycle_stageName" />,
      dataIndex: 'cycleName',
      key: 'cycleName',
      render(cycleName) {
        return (
          <div
            className="c7ntest-text-dot"
          >
            {cycleName}
          </div>
        );
      },
    };
    if (type === 'cycle') {
      columns.splice(4, 0, nameColumn);
    }
    return (
      <div className="c7ntest-TestExecuteTable">
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
            placeholder={<FormattedMessage id="cycle_executeBy" />}
            type="user"
            onChange={onExecuteByChange}
          />
          {treeAssignedTo === 0 && (
          <SelectFocusLoad
            allowClear
            style={{ marginLeft: 20, width: 200 }}
            className="c7ntest-select"
            placeholder={<FormattedMessage id="cycle_assignedTo" />}
            type="user"
            onChange={onAssignedToChange}
          />
          )}
        </div>
        <Table
          rowKey={record => record.executeId}
          pagination={pagination}
          loading={loading}
          onChange={onTableChange}
          dataSource={dataSource}
          columns={columns}
          onRow={record => ({
            onClick: (event) => { onTableRowClick(record); },
          })}
        />
      </div>
    );
  }
}

TestExecuteTable.propTypes = propTypes;

export default TestExecuteTable;
