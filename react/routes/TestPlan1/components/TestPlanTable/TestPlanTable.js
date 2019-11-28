import React, { memo } from 'react';
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

const propTypes = {
  loading: PropTypes.bool.isRequired,
  statusList: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  pagination: PropTypes.shape({}).isRequired,
  dataSource: PropTypes.arrayOf(PropTypes.shape({})).isRequired,
  checkIdMap: PropTypes.object.isRequired,
  onDragEnd: PropTypes.func.isRequired,
  onTableChange: PropTypes.func.isRequired,
  onTableRowClick: PropTypes.func.isRequired,
  onDeleteExecute: PropTypes.func.isRequired,
  onQuickPass: PropTypes.func.isRequired,
  onQuickFail: PropTypes.func.isRequired,
};
const TestPlanTable = observer(({
  loading,
  statusList,
  pagination,
  dataSource,
  onDragEnd,
  onTableChange,
  onTableRowClick,
  onDeleteExecute,
  onQuickPass,
  onQuickFail,
  checkIdMap,
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
    title: '',
    key: 'checkbox',
    width: 40,
    render: (text, record) => (
      <CustomCheckBox
        checkedMap={checkIdMap}
        value={record.executeId}
        field="executeId"
      />
    ),
  }, {
    title: <span>用例名</span>,
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
    title: <FormattedMessage id="cycle_testSource" />,
    dataIndex: 'testSource',
    key: 'testSource',
    flex: 1,
    render(testSource) {
      return (
        <div
          className="c7ntest-text-dot"
        >
          {testSource && testSource.realName}
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
  }];

  return (
    <Card
      className="c7ntest-testPlan-testPlanTableCard"
      title="测试用例"
      extra={(
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <SelectFocusLoad
            allowClear
            className="c7ntest-select"
            dropdownClassName="c7ntest-testPlan-userDropDown"
            style={{ width: 216 }}
            placeholder="指派给"
            getPopupContainer={trigger => trigger.parentNode}
            type="user"
          />
        </div>
      )}
    >
      <DragTable
        pagination={pagination}
        loading={loading}
        onChange={onTableChange}
        dataSource={dataSource}
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
