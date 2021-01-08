import React, {
  useContext, useRef, useEffect, useState,
} from 'react';
import PropTypes from 'prop-types';
import { toJS } from 'mobx';
import { FormattedMessage } from 'react-intl';
import { observer } from 'mobx-react-lite';
import {
  Tooltip, Button, Icon, Popover,
} from 'choerodon-ui';
import { Action, stores, Choerodon } from '@choerodon/boot';
import _ from 'lodash';
import {
  SelectFocusLoad, StatusTags, DragTable,
} from '../../../../components';
import CustomCheckBox from '../../../../components/CustomCheckBox';
import User from '../../../../components/User';
import './TestPlanTable.less';

import Store from '../../stores';
import { getRankByDate } from '../../../../api/TestPlanApi';
import PriorityTag from '../../../../components/PriorityTag';

const { AppState } = stores;

const propTypes = {
  onDragEnd: PropTypes.func.isRequired,
  onTableChange: PropTypes.func.isRequired,
  onTableSummaryClick: PropTypes.func.isRequired,
  onDeleteExecute: PropTypes.func.isRequired,
  onQuickPass: PropTypes.func.isRequired,
  onQuickFail: PropTypes.func.isRequired,
  onOpenUpdateRemind: PropTypes.func.isRequired,
};
const TestPlanTable = observer(({
  onDragEnd,
  onTableChange,
  onTableSummaryClick,
  onDeleteExecute,
  onQuickPass,
  onQuickFail,
  onOpenUpdateRemind,
  onAssignToChange,
  onSearchAssign,
  hasCheckBox,
  isMine,
}) => {
  const {
    testPlanStore,
  } = useContext(Store);

  const {
    tableLoading, statusList, executePagination, mineExecutePagination, testList, checkIdMap, testPlanStatus, priorityList,
  } = testPlanStore;

  const divRef = useRef();
  const [tipVisible, setTipVisible] = useState(false);
  // eslint-disable-next-line consistent-return
  useEffect(() => {
    if (divRef.current) {
      divRef.current.addEventListener('mousedown', (e) => {
        if (!checkIdMap.size) {
          e.stopPropagation();
          e.preventDefault();
        }
      }, true);

      divRef.current.addEventListener('click', (e) => {
        if (!checkIdMap.size) {
          e.stopPropagation();
          setTipVisible(true);
        }
      }, true);

      return () => {
        if (divRef.current) {
          divRef.current.removeEventListener('mousedown', (e) => {
            if (!checkIdMap.size) {
              e.stopPropagation();
              e.preventDefault();
            }
          }, true);
        }
      };
    }
  }, [checkIdMap.size]);

  const handleCheckBoxChange = () => {
    if (checkIdMap.size && tipVisible) {
      setTipVisible(false);
    }
  };

  const renderMenu = (text, record) => (testPlanStatus !== 'done' ? (
    <span style={{ display: 'flex', overflow: 'hidden', alignItems: 'center' }}>
      <Tooltip title={text}><span style={{ cursor: 'pointer' }} className="c7ntest-testPlan-table-summary" role="none" onClick={onTableSummaryClick.bind(this, record)}>{text}</span></Tooltip>
      <Tooltip title="此用例需更新">
        <span
          style={
            {
              display: testPlanStatus !== 'done' && record.hasChange ? 'flex' : 'none',
              flexShrink: 0,
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
      <span className="c7n-agile-table-cell-click" style={{ cursor: 'pointer', maxWidth: '2rem' }} role="none" onClick={onTableSummaryClick.bind(this, record)}>{text}</span>
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
    }
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
  };

  const getSummaryFilterValue = () => {
    if (!isMine) {
      return testPlanStore.filter && testPlanStore.filter.summary ? [testPlanStore.filter.summary] : [];
    }
    return testPlanStore.mineFilter && testPlanStore.mineFilter.summary ? [testPlanStore.mineFilter.summary] : [];
  };

  const getStatusFilteredValue = () => {
    if (!isMine) {
      return testPlanStore.filter && testPlanStore.filter.executionStatus ? [testPlanStore.filter.executionStatus] : [];
    }
    return testPlanStore.mineFilter && testPlanStore.mineFilter.executionStatus ? [testPlanStore.mineFilter.executionStatus] : [];
  };
  const getPriorityFilteredValue = () => {
    if (!isMine) {
      return testPlanStore.filter && testPlanStore.filter.priorityId ? [testPlanStore.filter.priorityId] : [];
    }
    return testPlanStore.mineFilter && testPlanStore.mineFilter.priorityId ? [testPlanStore.mineFilter.priorityId] : [];
  };
  const columns = [{
    title: <span>用例名</span>,
    dataIndex: 'summary',
    key: 'summary',
    filters: [],
    filteredValue: getSummaryFilterValue(),
    flex: 1.6,
    style: {
      overflow: 'hidden',
    },
    render: (text, record) => renderMenu(record.summary, record),
  }, {
    title: <span>被指派人</span>,
    dataIndex: 'assignedUser',
    key: 'assignedUser',
    flex: 1.2,
    style: {
      overflow: 'hidden',
    },
    render(assignedUser) {
      return (
        <div
          className="c7ntest-text-dot"
        >
          <User user={assignedUser} />
        </div>
      );
    },
  },
  {
    title: <span>执行人</span>,
    dataIndex: 'lastUpdateUser',
    key: 'lastUpdateUser',
    flex: 1.2,
    style: {
      overflow: 'hidden',
    },
    // filters: [],
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
          <Tooltip title={lastUpdateDate}>
            {lastUpdateDate}
          </Tooltip>
        </div>
      );
    },
  },
  {
    title: <FormattedMessage id="priority" />,
    dataIndex: 'priorityId',
    key: 'priorityId',
    filters: priorityList && priorityList.filter((priorityVO) => priorityVO.enableFlag)
      .map((priorityVO) => ({ text: priorityVO.name, value: priorityVO.id })),
    filteredValue: getPriorityFilteredValue(),
    flex: 1,
    width: 100,
    render(priorityId) {
      const priorityVO = _.find(priorityList, { id: priorityId }) || {};
      return (
        <PriorityTag
          priority={priorityVO}
        />
      );
    },
  },
  {
    title: <FormattedMessage id="status" />,
    dataIndex: 'executionStatus',
    key: 'executionStatus',
    filters: statusList && statusList.map((status) => ({ text: status.statusName, value: status.statusId.toString() })),
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
    if (hasCheckBox && !isMine) {
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
            onChangeCallBack={handleCheckBoxChange}
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
    } else {
      columns.splice(1, 0, {
        title: '',
        dataIndex: 'more',
        key: 'more',
        width: 55,
        render: (text, record) => renderMoreAction(record),
      });
    }
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

  const data = isMine ? testList.filter((item) => (item.assignedTo && item.assignedTo.toString() === AppState.userInfo.id.toString())) : testList;
  return (
    <div className={`c7ntest-testPlanTable ${isMine ? 'c7ntest-mineTestPlanTable' : ''}`}>
      {
        (!isMine && (data.length > 0 || testPlanStore.filter.assignUser)) && (
          <div style={{
            marginTop: '-55px', marginBottom: 10, flexDirection: 'row-reverse', alignItems: 'center', display: testPlanStore.mainActiveTab === 'testPlanTable' ? 'flex' : 'none',
          }}
          >

            <SelectFocusLoad
              allowClear
              style={{ width: 180, zIndex: 100, marginLeft: 10 }}
              placeholder="被指派人"
              loadWhenMount
              getPopupContainer={(trigger) => trigger.parentNode}
              type="user"
              onChange={onSearchAssign}
              value={testPlanStore.filter.assignUser}
            />
            <Popover
              content="请先选择测试用例"
              title=""
              trigger="click"
              visible={tipVisible}
              getPopupContainer={(trigger) => trigger.parentNode}
            >
              <div
                ref={divRef}
                role="none"
                style={{ width: 180, zIndex: 100, display: `${testPlanStatus === 'done' ? 'none' : 'unset'}` }}
              >
                <SelectFocusLoad
                  key={testPlanStore.assignToUserId}
                  allowClear
                  style={{ display: 'flex' }}
                  placeholder="批量指派"
                  getPopupContainer={(trigger) => trigger.parentNode}
                  type="user"
                  onChange={onAssignToChange}
                />
              </div>
            </Popover>
          </div>
        )
      }
      <DragTable
        pagination={isMine ? mineExecutePagination : executePagination}
        loading={tableLoading}
        onChange={onTableChange}
        dataSource={data}
        columns={columns}
        onDragEnd={onDragEnd}
        dragKey="executeId"
        checkedMap={checkIdMap}
        checkField="executeId"
        key={testPlanStore.currentCycle.id}
        onChangeCallBack={handleCheckBoxChange}
      />
    </div>
  );
});
TestPlanTable.propTypes = propTypes;
export default TestPlanTable;
