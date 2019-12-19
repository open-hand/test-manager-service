import React, { memo, useContext } from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react-lite';
import {
  Tooltip, Card, Button, Icon,
} from 'choerodon-ui';
import { Action } from '@choerodon/boot';
import _ from 'lodash';
import {
  SelectFocusLoad, StatusTags, DragTable,
} from '../../../../components';
import CustomCheckBox from '../../../../components/CustomCheckBox';
import User from '../../../../components/User';
import './TestPlanTable.less';

import Store from '../../stores';

const propTypes = {
  onDragEnd: PropTypes.func.isRequired,
  onTableChange: PropTypes.func.isRequired,
  onTableSummaryClick: PropTypes.func.isRequired,
  onDeleteExecute: PropTypes.func.isRequired,
  onQuickPass: PropTypes.func.isRequired,
  onQuickFail: PropTypes.func.isRequired,
  onAssignToChange: PropTypes.func.isRequired,
  onSearchAssign: PropTypes.func.isRequired,
  onOpenUpdateRemind: PropTypes.func.isRequired,
};
const TestPlanTable = observer(({
  onDragEnd,
  onTableChange,
  onTableSummaryClick,
  onDeleteExecute,
  onQuickPass,
  onQuickFail,
  onAssignToChange,
  onSearchAssign,
  onOpenUpdateRemind,
}) => {
  const {
    testPlanStore,
  } = useContext(Store);

  const {
    tableLoading, statusList, executePagination, testList, checkIdMap, testPlanStatus,
  } = testPlanStore;

  const renderMenu = (text, record) => (testPlanStatus !== 'done' ? (
    <span style={{ display: 'flex', alignItems: 'center' }}>
      <Tooltip title={text}><span style={{ cursor: 'pointer', maxWidth: testPlanStatus === 'todo' ? '2rem' : '1.5rem' }} className="c7ntest-testPlan-table-summary" role="none" onClick={onTableSummaryClick.bind(this, record)}>{text}</span></Tooltip>
      <Tooltip title="此用例需更新">
        <span
          style={
            {
              display: testPlanStatus !== 'done' && record.hasChange ? 'flex' : 'none',
              alignItems: 'center',
              justifyContent: 'center',
              width: 46,
              height: 20,
              background: '#fff',
              marginLeft: 3,
              fontSize: 12,
              color: '#00BF96',
              border: '1px solid #00BF96',
            }
          }
        >
          未更新
        </span>
      </Tooltip>
    </span>
  ) : (
    <Tooltip title={text}>
      <span className="c7ntest-testPlan-table-summary" style={{ cursor: 'pointer', maxWidth: '2rem' }} role="none" onClick={onTableSummaryClick.bind(this, record)}>{text}</span>
    </Tooltip>
  ));

  const renderMoreAction = (record) => {
    const action = [{
      text: '删除',
      action: () => onDeleteExecute(record),
    }];
    if (testPlanStatus !== 'done' && record.hasChange) {
      action.unshift({
        text: '查看更新',
        action: () => onOpenUpdateRemind(record),
      });
    }
    return testPlanStatus !== 'done' && <Action className="action-icon" data={action} />;
  };


  const renderSource = (source) => {
    if (!source || source === 'none') {
      return '';
    } else {
      return (
        <div className="c7ntest-text-dot">
          {source === 'auto' ? (
            <span style={{ display: 'flex', alignItems: 'center' }}>
              <Icon style={{ color: '#FA8C16', fontSize: 20 }} type="test-automation" />
              自动测试
            </span>
          ) : (
            <span style={{ display: 'flex', alignItems: 'center' }}>
              <Icon style={{ color: '#4D90FE', fontSize: 20 }} type="test-case" />
                手动测试
            </span>
          )}
        </div>
      );
    }
  };

  const getStatusFilteredValue = () => {
    if (testPlanStore.filter && testPlanStore.filter.executionStatus) {
      return [testPlanStore.filter.executionStatus];
    } else {
      return [];
    }
  };
  const columns = [{
    title: <span>用例名</span>,
    dataIndex: 'summary',
    key: 'summary',
    filters: [],
    flex: 2,
    render: (text, record) => renderMenu(record.summary, record),
  }, {
    title: '被指派人',
    dataIndex: 'assignedUser',
    key: 'assignedUser',
    flex: 1,
    render(assignedUser) {
      return (
        <div
          className="c7ntest-text-dot"
        >
          <User user={assignedUser} />
        </div>
      );
    },
  }, {
    title: '执行人',
    dataIndex: 'lastUpdateUser',
    key: 'lastUpdateUser',
    flex: 1,
    render(lastUpdateUser) {
      return (
        <div
          className="c7ntest-text-dot"
        >
          <User user={lastUpdateUser} />
        </div>
      );
    },
  }, {
    title: <FormattedMessage id="cycle_updatedDate" />,
    dataIndex: 'lastUpdateDate',
    key: 'lastUpdateDate',
    flex: 1.5,
    style: {
      overflow: 'hidden',
    },
    render(lastUpdateDate) {
      return (
        <div
          className="c7ntest-text-dot"
          style={{ color: 'rgba(0, 0, 0, 0.65)' }}
        >
          {lastUpdateDate}
        </div>
      );
    },
  }, {
    title: <FormattedMessage id="status" />,
    dataIndex: 'executionStatus',
    key: 'executionStatus',
    filters: statusList && statusList.map(status => ({ text: status.statusName, value: status.statusId.toString() })),
    filteredValue: getStatusFilteredValue(),
    flex: 1,
    width: 100,
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

  if (testPlanStatus !== 'done') {
    columns.unshift({
      title: '',
      key: 'checkbox',
      width: 40,
      render: (text, record) => (
        <CustomCheckBox
          checkedMap={checkIdMap}
          value={record.executeId}
          field="executeId"
          dataSource={testList}
        />
      ),
    });
    columns.splice(2, 0, {
      title: '',
      dataIndex: 'more',
      key: 'more',
      width: 55,
      render: (text, record) => renderMoreAction(record),
    });
  }

  if (testPlanStatus === 'doing') {
    columns.push({
      title: '',
      key: 'action',
      width: 90,
      render: (text, record) => (
        record.projectId !== 0
        && (
          <div style={{ display: 'flex' }}>
            <Tooltip title={<FormattedMessage id="execute_quickPass" />}>
              <Button shape="circle" funcType="flat" icon="check_circle" onClick={onQuickPass.bind(this, record, true)} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="execute_quickFail" />}>
              <Button shape="circle" funcType="flat" icon="cancel" onClick={onQuickFail.bind(this, record, false)} />
            </Tooltip>
          </div>
        )
      ),
    });
  }

  // if (testPlanStatus !== 'todo') {
  //   columns.splice(testPlanStatus === 'doing' ? 3 : 2, 0, {
  //     title: <FormattedMessage id="cycle_testSource" />,
  //     dataIndex: 'source',
  //     key: 'source',
  //     flex: 1,
  //     render: (text, record) => renderSource(text),
  //   });
  // }

  return (
    <Card
      className="c7ntest-testPlan-testPlanTableCard"
      title="测试用例"
      extra={(
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <SelectFocusLoad
            allowClear
            disabled={!checkIdMap.size}
            style={{ width: 216, display: `${testPlanStatus === 'done' ? 'none' : 'unset'}` }}
            placeholder="批量指派"
            getPopupContainer={trigger => trigger.parentNode}
            type="user"
            onChange={onAssignToChange}
            value={testPlanStore.assignToUserId}
          />
          <SelectFocusLoad
            allowClear
            style={{ width: 216, marginLeft: 10 }}
            placeholder="被指派人"
            getPopupContainer={trigger => trigger.parentNode}
            type="user"
            onChange={onSearchAssign}
            value={testPlanStore.filter.assignUser}
          />
        </div>
      )}
    >
      <DragTable
        pagination={executePagination}
        loading={tableLoading}
        onChange={onTableChange}
        dataSource={testList}
        columns={columns}
        onDragEnd={onDragEnd}
        dragKey="executeId"
        checkedMap={checkIdMap}
        checkField="executeId"
        key={testPlanStore.currentCycle.id}
      />
    </Card>
  );
});
TestPlanTable.propTypes = propTypes;
export default TestPlanTable;
