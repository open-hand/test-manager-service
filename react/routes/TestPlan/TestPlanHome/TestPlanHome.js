/* eslint-disable react/jsx-no-bind */
import React, {
  useCallback, useContext, useEffect,
} from 'react';
import { observer } from 'mobx-react-lite';
import { withRouter } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import {
  Page, Header, Content, Breadcrumb, Choerodon, stores,
} from '@choerodon/boot';
import { Icon, Tabs, Card } from 'choerodon-ui';
import { Modal, Button } from 'choerodon-ui/pro';
import { localPageCacheStore } from '@choerodon/agile/lib/stores/common/LocalPageCacheStore';
import {
  deleteExecute, updateExecute, comfirmUpdate, ignoreUpdate, getRankByDate,
} from '../../../api/TestPlanApi';
import CreateAutoTest from '../components/CreateAutoTest';
import TestPlanDetailCard from '../components/TestPlanDetailCard';
import TestPlanStatusCard from '../components/TestPlanStatusCard';
import UpdateRemindModalChildren from '../components/UpdateRemindModalChildren';
import TestPlanTree from '../components/TestPlanTree';
import TestPlanTable from '../components/TestPlanTable';
import TestPlanHeader from '../components/TestPlanHeader';
import { openCreatePlan } from '../components/TestPlanModal';
import EventCalendar from '../components/EventCalendar';
import Empty from '../../../components/Empty';
import { SelectFocusLoad } from '../../../components';
import testCaseEmpty from './testCaseEmpty.svg';

import Store from '../stores';
import './TestPlanHome.less';
import { getDragRank, executeDetailLink } from '../../../common/utils';

const { AppState } = stores;

const { TabPane } = Tabs;
const { confirm } = Modal;
const updateRemindModal = Modal.key();
let updateModal;

function TestPlanHome({ history }) {
  const {
    prefixCls, createAutoTestStore, testPlanStore,
  } = useContext(Store);
  const {
    loading, checkIdMap, testList, testPlanStatus, planInfo, statusList, currentCycle, mainActiveTab, times, calendarLoading,
  } = testPlanStore;
  const handleTabsChange = (value) => {
    testPlanStore.setTestPlanStatus(value);
    testPlanStore.setCurrentCycle({});
    testPlanStore.setFilter({});
    testPlanStore.loadAllData();
  };

  const handleMainTabsChange = (value) => {
    testPlanStore.setMainActiveTab(value);
    if (value === 'testPlanTable') {
      testPlanStore.setFilter({});
      testPlanStore.setBarFilter([]);
      testPlanStore.setExecutePagination({ current: 1, pageSize: 20 });
      testPlanStore.loadExecutes();
    }
    if (value === 'mineTestPlanTable') {
      testPlanStore.setMineFilter({});
      testPlanStore.setMineBarFilter([]);
      testPlanStore.setMineExecutePagination({ current: 1, pageSize: 20 });
      testPlanStore.loadExecutes(undefined, undefined, true);
    }
  };

  const handleUpdateOk = (record) => {
    const data = {
      caseId: record.caseId,
      executeId: record.executeId,
      syncToCase: false,
      changeCase: record.changeCase,
      changeStep: record.changeStep,
      changeAttach: record.changeAttach,
    };
    comfirmUpdate(data).then(() => {
      Choerodon.prompt('更新成功');
      testPlanStore.loadExecutes();
    }).catch(() => {
      Choerodon.prompt('更新失败');
    });
  };

  const handleIgnoreUpdate = (record) => {
    ignoreUpdate(record.executeId).then(() => {
      Choerodon.prompt('已忽略本次更新');
      testPlanStore.loadExecutes();
      updateModal.close();
    }).catch(() => {
      Choerodon.prompt('忽略更新失败');
      return false;
    });
  };

  const handleOpenCreatePlan = () => {
    openCreatePlan({
      onCreate: () => {
        if (testPlanStatus !== 'todo') {
          testPlanStore.setTestPlanStatus('todo');
        }
        testPlanStore.loadIssueTree();
      },
    });
  };

  const handleOpenUpdateRemind = (record) => {
    const cycle = (testPlanStore.treeData.treeFolder || []).find((item) => item.data.folderId === record.cycleId);
    const cycleName = cycle && cycle.data.name;
    updateModal = Modal.open({
      key: updateRemindModal,
      drawer: true,
      title: '用例变更提醒',
      children: <UpdateRemindModalChildren testPlanStore={testPlanStore} executeId={record.executeId} cycleName={cycleName} />,
      style: { width: '10.9rem' },
      className: 'c7ntest-testPlan-updateRemind-modal',
      okText: '更新',
      cancelText: '取消',
      onOk: handleUpdateOk.bind(this, record),
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          <Button funcType="funcType" onClick={handleIgnoreUpdate.bind(this, record)}>忽略更新</Button>
          {cancelBtn}
        </div>
      ),
    });
  };

  const handleTableSummaryClick = (record) => {
    const lastIndexOf = testPlanStore.currentCycle.id.toString().lastIndexOf('%');
    const cycleId = lastIndexOf === -1 ? '' : testPlanStore.currentCycle.id.substring(lastIndexOf + 1);
    let assignerId = testPlanStore.getFilters.assignUser;
    const { contents, searchArgs: { executionStatus, summary } } = testPlanStore.getSearchObj;
    if (mainActiveTab === 'mineTestPlanTable') {
      assignerId = AppState.userInfo.id.toString();
    }
    const filters = {
      cycle_id: cycleId,
      plan_id: testPlanStore.getCurrentPlanId,
      assignerId,
      contents: contents.map((c) => c),
      executionStatus,
      summary,
    };
    history.push(executeDetailLink(record.executeId, filters));
  };

  const onDragEnd = (sourceIndex, targetIndex) => {
    const { lastRank, nextRank } = getDragRank(sourceIndex, targetIndex, testList);
    const source = testList[sourceIndex];
    updateExecute({
      executeId: source.executeId,
      objectVersionNumber: source.objectVersionNumber,
      lastRank,
      nextRank,
    }).then(() => {
      testPlanStore.loadExecutes();
    }).catch(() => {
      Choerodon.prompt('网络错误');
      testPlanStore.setTableLoading(false);
    });
  };

  const handleExecuteTableChange = (pagination, filters, sorter, barFilters) => {
    let { filter } = testPlanStore;
    // eslint-disable-next-line array-callback-return
    Object.keys(filters).map((key) => {
      if (filters[key] && filters[key].length > 0) {
        filter = { ...filter, [key]: filters[key][0] };
      } else {
        filter[key] = null;
      }
    });
    testPlanStore.setBarFilter(barFilters || []);
    if (pagination.current) {
      testPlanStore.setFilter(filter);
      testPlanStore.setExecutePagination(pagination);
      testPlanStore.loadExecutes();
    }
  };

  const handleMineExecuteTableChange = (pagination, filters, sorter, barFilters) => {
    let { mineFilter } = testPlanStore;
    // eslint-disable-next-line array-callback-return
    Object.keys(filters).map((key) => {
      if (filters[key] && filters[key].length > 0) {
        mineFilter = { ...mineFilter, [key]: filters[key][0] };
      } else {
        mineFilter[key] = null;
      }
    });
    testPlanStore.setMineBarFilter(barFilters || []);
    if (pagination.current) {
      testPlanStore.setMineFilter(mineFilter);
      testPlanStore.setMineExecutePagination(pagination);
      testPlanStore.loadExecutes(undefined, undefined, true);
    }
  };

  const handleDeleteExecute = (record) => {
    const { executeId } = record;
    confirm({
      width: 560,
      title: Choerodon.getMessage('确认删除吗?', 'Confirm delete'),
      content: Choerodon.getMessage('当您点击删除后，该条执行将从此计划阶段中移除!', 'When you click delete, after which the data will be deleted !'),
      onOk: () => {
        deleteExecute(executeId)
          .then(() => {
            testPlanStore.loadExecutes();
          }).catch((err) => {
            /* console.log(err); */
            Choerodon.prompt('删除失败');
          });
      },
      okText: '删除',
      okType: 'danger',
    });
  };

  const handleQuickPassOrFail = (execute, isPass = true, e) => {
    e.stopPropagation();
    let executionStatus;
    let executionStatusName;
    if (isPass) {
      const { statusId, statusName } = statusList.find((status) => status.statusName === '通过') || {};
      executionStatus = statusId;
      executionStatusName = statusName;
    } else {
      const { statusId, statusName } = statusList.find((status) => status.statusName === '失败') || {};
      executionStatus = statusId;
      executionStatusName = statusName;
    }
    const data = {
      executionStatus,
      executionStatusName,
      executeId: execute.executeId,
      objectVersionNumber: execute.objectVersionNumber,

    };
    updateExecute(data).then(() => {
      if (mainActiveTab === 'mineTestPlanTable') {
        testPlanStore.loadExecutes(undefined, undefined, true);
      } else {
        testPlanStore.loadExecutes();
      }
      testPlanStore.loadStatusRes();
    }).catch(() => {
      if (isPass) {
        Choerodon.prompt('快速通过失败');
      } else {
        Choerodon.prompt('操作失败');
      }
    });
  };

  const handleAssignToChange = (value) => {
    testPlanStore.setAssignToUserId(value);
    if (value && checkIdMap.size) {
      testPlanStore.executesAssignTo(value).then(() => {
        checkIdMap.clear();
        testPlanStore.setAssignToUserId(undefined);
      });
    }
  };

  const handleSearchAssign = (value) => {
    const { filter } = testPlanStore;
    filter.assignUser = value || undefined;
    testPlanStore.setFilter(filter);
    testPlanStore.loadExecutes();
  };

  useEffect(() => {
    // 加载缓存
    const defaultTreeActiveTab = localPageCacheStore.getItem('testPlan.tree.activeTab');
    const defaultTreeSelected = localPageCacheStore.getItem('testPlan.tree.selected');
    const defaultTableActiveTab = localPageCacheStore.getItem('testPlan.table.activeTab');
    const defaultTreeQueryParams = localPageCacheStore.getItem('testPlan.table.queryParams');
    if (defaultTreeActiveTab) {
      testPlanStore.setTestPlanStatus(defaultTreeActiveTab);
      testPlanStore.setFilter({});
    }
    defaultTreeSelected && testPlanStore.setCurrentCycle(defaultTreeSelected || {});
    if (defaultTableActiveTab) {
      testPlanStore.setMainActiveTab(defaultTableActiveTab);
    }
    if (defaultTreeQueryParams) {
      const {
        search, current, pageSize,
      } = defaultTreeQueryParams;
      const { searchArgs, contents } = search;
      if (defaultTableActiveTab === 'mineTestPlanTable') {
        testPlanStore.setMineExecutePagination({ current, pageSize });
        testPlanStore.setMineFilter(searchArgs);
        testPlanStore.setMineBarFilter(contents);
      } else {
        testPlanStore.setExecutePagination({ current, pageSize });
        testPlanStore.setFilter(searchArgs);
        testPlanStore.setBarFilter(contents);
      }
    }
    testPlanStore.loadAllData();
  }, [testPlanStore]);
  const handleRefresh = useCallback(() => {
    testPlanStore.loadAllData();
  }, [testPlanStore]);
  const noSelected = !currentCycle.id;
  let description;
  if (testPlanStatus === 'todo') {
    description = '当前项目下无未开始的计划';
  } else if (testPlanStatus === 'doing') {
    description = '当前项目下无进行中的计划';
  } else if (testPlanStatus === 'done') {
    description = '当前项目下无已完成的计划';
  }
  return (
    <Page
      className={prefixCls}
      service={[
        'choerodon.code.project.test.test-plan.ps.default',
      ]}
    >
      <Header
        title={<FormattedMessage id="testPlan_name" />}
      >
        <Button icon="playlist_add" onClick={handleOpenCreatePlan}>
          <FormattedMessage id="testPlan_createPlan" />
        </Button>
        <TestPlanHeader />
        <Button icon="refresh" onClick={handleRefresh}>
          <FormattedMessage id="refresh" />
        </Button>

      </Header>
      <Breadcrumb />
      <Content style={{ display: 'flex', padding: '0', borderTop: '0.01rem solid rgba(0,0,0,0.12)' }}>
        <div className={`${prefixCls}-contentWrap`}>
          <div className={`${prefixCls}-contentWrap-left`}>
            <div className={`${prefixCls}-contentWrap-testPlanTree`}>
              <Tabs defaultActiveKey="todo" onChange={handleTabsChange} activeKey={testPlanStatus}>
                <TabPane tab="未开始" key="todo" />
                <TabPane tab="进行中" key="doing" />
                <TabPane tab="已完成" key="done" />
              </Tabs>
              <TestPlanTree />
            </div>
          </div>
          {
            noSelected ? (
              <Empty
                loading={loading}
                pic={testCaseEmpty}
                title="暂无计划"
                description={description}
              />
            ) : (
              <div className={`${prefixCls}-contentWrap-right`}>
                <div className={`${prefixCls}-contentWrap-right-currentPlanName`}>
                  <Icon type="insert_invitation" style={{ marginTop: 3 }} />
                  <span>{planInfo.name}</span>
                </div>
                <div className={`${prefixCls}-contentWrap-right-warning`}>
                  {/* <Icon type="error" />
                <span>该计划正在进行自动化测试，手工测试结果可能会将自动化测试结果覆盖！</span> */}
                </div>
                <div className={`${prefixCls}-contentWrap-right-card`}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', flexWrap: 'nowrap' }}>
                    <div style={{
                      flex: 1, marginRight: '0.16rem', paddingTop: '0.05rem', paddingBottom: '0.05rem',
                    }}
                    >
                      <TestPlanDetailCard />
                    </div>
                    <div style={{
                      flex: 1, overflowX: 'hidden', paddingTop: '0.05rem', paddingBottom: '0.05rem',
                    }}
                    >
                      <TestPlanStatusCard />
                    </div>
                  </div>
                  <div className={`${prefixCls}-contentWrap-main`}>
                    <Card
                      className={`${prefixCls}-contentWrap-main-card`}
                    >
                      <Tabs
                        defaultActiveKey="testPlanTable"
                        onChange={handleMainTabsChange}
                        activeKey={mainActiveTab}
                      >
                        <TabPane tab="测试用例" key="testPlanTable" />
                        <TabPane tab="我的执行" key="mineTestPlanTable" />
                        {
                            testPlanStore.isPlan(currentCycle.id) ? (
                              <TabPane tab="计划日历" key="testPlanSchedule">
                                <EventCalendar key={currentCycle.id} showMode="multi" times={times} calendarLoading={calendarLoading} />
                              </TabPane>
                            ) : ''
                          }
                      </Tabs>
                      {
                          mainActiveTab !== 'testPlanSchedule' && (
                            <TestPlanTable
                              onDragEnd={onDragEnd}
                              onTableChange={mainActiveTab === 'testPlanTable' ? handleExecuteTableChange : handleMineExecuteTableChange}
                              onDeleteExecute={handleDeleteExecute}
                              onQuickPass={handleQuickPassOrFail}
                              onQuickFail={handleQuickPassOrFail}
                              onOpenUpdateRemind={handleOpenUpdateRemind}
                              onTableSummaryClick={handleTableSummaryClick}
                              onAssignToChange={handleAssignToChange}
                              onSearchAssign={handleSearchAssign}
                              hasCheckBox={mainActiveTab === 'testPlanTable'}
                              isMine={mainActiveTab === 'mineTestPlanTable'}
                              key={mainActiveTab}
                            />
                          )
                        }
                    </Card>
                  </div>
                </div>
              </div>
            )
          }
        </div>
      </Content>
      <CreateAutoTest createAutoTestStore={createAutoTestStore} />
    </Page>
  );
}

export default withRouter(observer(TestPlanHome));
