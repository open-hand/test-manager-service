import React, { memo, useState, useContext } from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react-lite';
import {
  Tooltip, Menu, Card, Button, Icon, 
} from 'choerodon-ui';
import _ from 'lodash';
import {
  SelectFocusLoad, StatusTags, DragTable, SmartTooltip,
} from '../../../../components';
import CustomCheckBox from '../../../../components/CustomCheckBox';
import './TestPlanTable.less';
import TableDropMenu from '../../../../common/TableDropMenu';
import Store from '../../stores';

const propTypes = {
  onDragEnd: PropTypes.func.isRequired,
  onTableChange: PropTypes.func.isRequired,
  onTableSummaryClick: PropTypes.func.isRequired,
  onDeleteExecute: PropTypes.func.isRequired,
  onQuickPass: PropTypes.func.isRequired,
  onQuickFail: PropTypes.func.isRequired,
  onAssignToChange: PropTypes.func.isRequired,
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
  onOpenUpdateRemind,
}) => {
  const { 
    testPlanStore,
  } = useContext(Store);

  const {
    tableLoading, statusList, executePagination, testList, checkIdMap, testPlanStatus,
  } = testPlanStore;

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
    return testPlanStatus !== 'done' ? (
      <TableDropMenu
        menu={menu}
        text={(
          <span style={{ display: 'flex', alignItems: 'center' }}>
            <Tooltip title={text}><span style={{ maxWidth: testPlanStatus === 'todo' ? '2rem' : '1.5rem' }} className="c7ntest-testPlan-table-summary">{text}</span></Tooltip>
            <span 
              style={
                { 
                  display: testPlanStatus === 'todo' && record.hasChange ? 'inline-block' : 'none', 
                  width: 6, 
                  height: 6, 
                  borderRadius: '50%', 
                  background: '#ffb100',
                  marginLeft: 3,
                }
              }
            />
          </span>
        )}
        isHasMenu={record.projectId !== 0}
        onClickEdit={(record.hasChange && testPlanStatus === 'todo') ? onOpenUpdateRemind.bind(this, record) : onTableSummaryClick.bind(this, record)}
      />
    ) : (
      <Tooltip title={text}>
        <span className="c7ntest-testPlan-table-summary" style={{ cursor: 'pointer', maxWidth: '2rem' }} role="none" onClick={onTableSummaryClick.bind(this, record)}>{text}</span>
      </Tooltip>
    );
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
    title: <FormattedMessage id="cycle_executeBy" />,
    dataIndex: 'assignedUser',
    key: 'assignedUser',
    flex: 1,
    filters: [],
    render(assignedUser) {
      return (
        <div
          className="c7ntest-text-dot"
        >
          {assignedUser && assignedUser.realName}
        </div>
      );
    },
  }, {
    title: <FormattedMessage id="cycle_updatedDate" />,
    dataIndex: 'lastUpdateDate',
    key: 'lastUpdateDate',
    flex: 1,
    render(lastUpdateDate) {
      return (
        <div
          className="c7ntest-text-dot"
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

  if (testPlanStatus !== 'todo') {
    columns.splice(testPlanStatus === 'doing' ? 3 : 2, 0, {
      title: <FormattedMessage id="cycle_testSource" />,
      dataIndex: 'source',
      key: 'source',
      flex: 1,
      render: (text, record) => renderSource(text),
    });
  }

  return (
    <Card
      className="c7ntest-testPlan-testPlanTableCard"
      title="测试用例"
      extra={checkIdMap.size ? (
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <SelectFocusLoad
            allowClear
            className="c7ntest-select c7ntest-testPlan-assignToSelect"
            dropdownClassName="c7ntest-testPlan-assignToDropDown"
            style={{ width: 216 }}
            placeholder="指派给"
            getPopupContainer={trigger => trigger.parentNode}
            type="user"
            onChange={onAssignToChange}
          />
        </div>
      ) : ''}
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
      />
    </Card>
  );
});
TestPlanTable.propTypes = propTypes;
export default memo(TestPlanTable);
