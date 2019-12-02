import React, { memo, useState, useContext } from 'react';
import PropTypes from 'prop-types';
import { FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react-lite';
import {
  Tooltip, Menu, Card, Button, 
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
  onTableRowClick: PropTypes.func.isRequired,
  onDeleteExecute: PropTypes.func.isRequired,
  onQuickPass: PropTypes.func.isRequired,
  onQuickFail: PropTypes.func.isRequired,
  onAssignToChange: PropTypes.func.isRequired,
  onOpenUpdateRemind: PropTypes.func.isRequired,
};
const TestPlanTable = observer(({
  onDragEnd,
  onTableChange,
  onTableRowClick,
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
    return (
      <TableDropMenu
        menu={menu}
        text={text}
        isHasMenu={record.projectId !== 0}
        // onClickEdit={onTableRowClick.bind(this, record)}
      />
    );
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
          value={record.caseId}
          field="caseId"
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
              <Button shape="circle" funcType="flat" icon="check_circle" onClick={onQuickPass.bind(this, record)} />
            </Tooltip>
            <Tooltip title={<FormattedMessage id="execute_quickFail" />}>
              <Button shape="circle" funcType="flat" icon="cancel" onClick={onQuickFail.bind(this, record)} />
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
      render(source) {
        return (
          <div
            className="c7ntest-text-dot"
          >
            {source && source}
          </div>
        );
      },
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
        dragKey="caseId"
        checkedMap={checkIdMap}
        checkField="caseId"
      />
    </Card>
  );
});
TestPlanTable.propTypes = propTypes;
export default memo(TestPlanTable);
